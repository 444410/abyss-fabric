package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class CrystalESP extends Module {
    private final Setting<Float> range = new Setting<>("Range", "", this, 16f, 1f, 64f);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(142, 0, 255, 50));
    private final Setting<Boolean> circle = new Setting<>("Circle", "A ring on the ground instead of a box", this, false);

    public CrystalESP() {
        super("CrystalESP", "Highlights end crystals.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof EndCrystal crystal && mc.player.distanceTo(crystal) <= range.f()) {
                var pos = crystal.getPosition(event.partialTicks());
                if (circle.on()) {
                    RenderUtil.circle(pos, 1.0, ColorUtil.withAlpha(color.argb(), 255), 2f, 32);
                } else {
                    RenderUtil.box(crystal.getBoundingBox().move(pos.subtract(crystal.position())), color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
                }
            }
        }
    });
}
