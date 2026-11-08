package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Tracers extends Module {
    private final Setting<Boolean> players = new Setting<>("Players", "", this, true);
    private final Setting<Boolean> mobs = new Setting<>("Mobs", "", this, false);
    private final Setting<Boolean> animals = new Setting<>("Animals", "", this, false);
    private final Setting<Float> range = new Setting<>("Range", "", this, 128f, 1f, 512f);
    private final Setting<Boolean> distanceColor = new Setting<>("DistanceColor", "Red when close, green when far", this, true);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 255, 255, 255)).visible(() -> !distanceColor.on());
    private final Setting<Color> friendColor = new Setting<>("FriendColor", "", this, new Color(85, 255, 255, 255));
    private final Setting<Float> width = new Setting<>("LineWidth", "", this, 1.5f, 0.1f, 5f);

    public Tracers() {
        super("Tracers", "Draws lines to entities.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        Vec3 cam = RenderUtil.cameraPos();
        Vec3 from = cam.add(mc.player.getViewVector(event.partialTicks()).scale(2));
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player || !entity.isAlive() || mc.player.distanceTo(entity) > range.f()) {
                continue;
            }
            boolean show = (entity instanceof Player && players.on()) || (mobs.on() && EntityUtil.isHostile(entity)) || (animals.on() && EntityUtil.isPassive(entity));
            if (!show) {
                continue;
            }
            int c;
            if (entity instanceof Player p && Abyss.friendManager.isFriend(p)) {
                c = friendColor.argb();
            } else if (distanceColor.on()) {
                c = ColorUtil.healthColor(mc.player.distanceTo(entity), 50f);
            } else {
                c = color.argb();
            }
            Vec3 to = entity.getPosition(event.partialTicks()).add(0, entity.getBbHeight() / 2, 0);
            RenderUtil.line(from, to, c, width.f());
        }
    });
}
