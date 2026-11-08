package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.world.phys.AABB;

public class BreakESP extends Module {
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 0, 0, 80));
    private final Map<Integer, BlockPos> breaking = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> progress = new ConcurrentHashMap<>();

    public BreakESP() {
        super("BreakESP", "Renders blocks being mined.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientboundBlockDestructionPacket packet) {
            if (packet.getProgress() < 0 || packet.getProgress() > 9) {
                breaking.remove(packet.getId());
                progress.remove(packet.getId());
            } else {
                breaking.put(packet.getId(), packet.getPos());
                progress.put(packet.getId(), packet.getProgress());
            }
        }
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        breaking.forEach((id, pos) -> {
            if (mc.level.getBlockState(pos).isAir()) {
                breaking.remove(id);
                return;
            }
            double s = 0.5 * (progress.getOrDefault(id, 0) + 1) / 10.0;
            AABB box = new AABB(pos.getX() + 0.5 - s, pos.getY() + 0.5 - s, pos.getZ() + 0.5 - s, pos.getX() + 0.5 + s, pos.getY() + 0.5 + s, pos.getZ() + 0.5 + s);
            RenderUtil.box(box, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
        });
    });
}
