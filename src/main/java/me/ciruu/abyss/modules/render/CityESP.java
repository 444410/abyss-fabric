package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class CityESP extends Module {
    private final Setting<Float> range = new Setting<>("Range", "", this, 8f, 1f, 20f);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 0, 0, 60));

    public CityESP() {
        super("CityESP", "Shows blocks you can city.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (Player player : EntityUtil.targets(range.f())) {
            BlockPos feet = CombatUtil.feet(player);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = feet.relative(dir);
                if (BlockUtil.block(side) == Blocks.OBSIDIAN || BlockUtil.block(side) == Blocks.ENDER_CHEST) {
                    RenderUtil.block(side, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
                }
            }
        }
    });
}
