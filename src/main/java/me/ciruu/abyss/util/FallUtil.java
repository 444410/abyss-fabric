package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public final class FallUtil implements Globals {
    private FallUtil() {
    }

    public static void bucket() {
        BlockPos below = mc.player.blockPosition();
        for (int i = 1; i <= 3; i++) {
            if (!mc.level.getBlockState(below.below(i)).isAir()) {
                int slot = InventoryUtil.findHotbar(Items.WATER_BUCKET);
                if (slot == -1) {
                    return;
                }
                int old = InventoryUtil.selected();
                InventoryUtil.switchTo(slot);
                PacketUtil.send(new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), 90f, mc.player.onGround(), mc.player.horizontalCollision));
                mc.player.setXRot(90f);
                mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                InventoryUtil.switchTo(old);
                return;
            }
        }
    }
}
