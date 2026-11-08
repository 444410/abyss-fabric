package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class BurrowESP extends Module {
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 255, 0, 60));

    public BurrowESP() {
        super("BurrowESP", "Shows players burrowed in blocks.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (Player player : mc.level.players()) {
            if (player == mc.player) {
                continue;
            }
            BlockPos feet = CombatUtil.feet(player);
            if (!BlockUtil.isReplaceable(feet) && !mc.level.getBlockState(feet).getCollisionShape(mc.level, feet).isEmpty()) {
                RenderUtil.block(feet, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
            }
        }
    });
}
