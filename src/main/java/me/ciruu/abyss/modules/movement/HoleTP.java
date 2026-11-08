package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.util.HoleUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;

public class HoleTP extends Module {
    public HoleTP() {
        super("HoleTP", "Teleports you to nearby holes.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (!mc.player.onGround() || mc.player.isInWater() || mc.player.isInLava() || mc.options.keyJump.isDown()) {
            return;
        }
        BlockPos below = BlockPos.containing(mc.player.getX(), mc.player.getY() - 0.2, mc.player.getZ());
        for (int i = 0; i < 3; i++) {
            BlockPos pos = below.below(i);
            if (HoleUtil.hole(pos) != HoleUtil.Type.NONE && mc.level.getBlockState(below).isAir()) {
                mc.player.setDeltaMovement(0, -3, 0);
                return;
            }
        }
    });
}
