package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.events.EventWorldChange;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.Projection;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class LogoutSpot extends Module {
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 0, 0, 60));
    private final Setting<Boolean> message = new Setting<>("Message", "Chat message when someone logs out", this, true);

    private record Spot(String name, AABB box, float health) {
    }

    private final Map<UUID, Spot> spots = new ConcurrentHashMap<>();

    public LogoutSpot() {
        super("LogoutSpot", "Shows where players logged out.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (!(event.getPacket() instanceof ClientboundPlayerInfoRemovePacket packet)) {
            return;
        }
        mc.execute(() -> {
            if (mc.level == null) {
                return;
            }
            for (UUID id : packet.profileIds()) {
                Player player = mc.level.getPlayerByUUID(id);
                if (player != null && player != mc.player) {
                    spots.put(id, new Spot(player.getGameProfile().name(), player.getBoundingBox(), player.getHealth() + player.getAbsorptionAmount()));
                    if (message.on()) {
                        ChatUtil.send(player.getGameProfile().name() + " logged out at " + player.blockPosition().toShortString());
                    }
                }
            }
        });
    });

    @EventHandler
    private final Listener<EventWorldChange> world = new Listener<>(EventWorldChange.class, e -> spots.clear());

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        spots.keySet().removeIf(id -> mc.level.getPlayerByUUID(id) != null);
        for (Spot spot : spots.values()) {
            RenderUtil.box(spot.box(), color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
        }
    });

    @EventHandler
    private final Listener<EventRender2D> text = new Listener<>(EventRender2D.class, event -> {
        for (Spot spot : spots.values()) {
            double[] p = Projection.project(spot.box().getCenter().x, spot.box().maxY + 0.3, spot.box().getCenter().z);
            if (p != null) {
                String label = spot.name() + " " + String.format("%.0f", spot.health()) + "hp";
                event.graphics().text(mc.font, label, (int) p[0] - mc.font.width(label) / 2, (int) p[1], 0xFFFF5555, true);
            }
        }
    });
}
