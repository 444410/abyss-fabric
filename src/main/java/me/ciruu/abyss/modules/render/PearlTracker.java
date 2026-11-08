package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.ciruu.abyss.util.Projection;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.phys.Vec3;

public class PearlTracker extends Module {
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(142, 0, 255, 255));
    private final Setting<Boolean> notify = new Setting<>("Notify", "Chat message when someone throws a pearl", this, true);
    private final Map<Integer, List<Vec3>> trails = new ConcurrentHashMap<>();
    private final Map<Integer, String> owners = new ConcurrentHashMap<>();

    public PearlTracker() {
        super("PearlTracker", "Tracks thrown ender pearls.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        List<Integer> alive = new ArrayList<>();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof ThrownEnderpearl pearl)) {
                continue;
            }
            alive.add(pearl.getId());
            trails.computeIfAbsent(pearl.getId(), id -> {
                if (pearl.getOwner() instanceof Player owner) {
                    owners.put(id, owner.getGameProfile().name());
                    if (notify.on() && owner != mc.player) {
                        ChatUtil.send(owner.getGameProfile().name() + " threw a pearl");
                    }
                }
                return new ArrayList<>();
            }).add(pearl.position());
        }
        trails.keySet().removeIf(id -> !alive.contains(id));
        owners.keySet().removeIf(id -> !alive.contains(id));
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (List<Vec3> trail : trails.values()) {
            RenderUtil.strip(new ArrayList<>(trail), color.argb(), 2f);
        }
    });

    @EventHandler
    private final Listener<EventRender2D> labels = new Listener<>(EventRender2D.class, event -> {
        owners.forEach((id, name) -> {
            Entity pearl = mc.level.getEntity(id);
            if (pearl == null) {
                return;
            }
            double[] p = Projection.project(pearl.getX(), pearl.getY() + 0.5, pearl.getZ());
            if (p != null) {
                event.graphics().text(mc.font, name, (int) p[0] - mc.font.width(name) / 2, (int) p[1], color.argb() | 0xFF000000, true);
            }
        });
    });
}
