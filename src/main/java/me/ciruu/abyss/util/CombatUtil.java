package me.ciruu.abyss.util;

import java.util.List;
import me.ciruu.abyss.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class CombatUtil implements Globals {
    public enum SwitchMode {
        Normal, Silent, None
    }

    private CombatUtil() {
    }

    public static int obsidianSlot() {
        int slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        return slot != -1 ? slot : InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);
    }

    public static int blockSlot(Block block) {
        return InventoryUtil.findHotbarBlock(block);
    }

    public static int placeAll(List<BlockPos> positions, int slot, int limit, boolean rotate, SwitchMode mode) {
        if (slot == -1 || positions.isEmpty()) {
            return 0;
        }
        boolean offhand = mc.player.getOffhandItem().getItem() instanceof BlockItem bi
                && bi == mc.player.getInventory().getItem(slot).getItem();
        int old = InventoryUtil.selected();
        boolean switched = false;
        int placed = 0;
        for (BlockPos pos : positions) {
            if (placed >= limit) {
                break;
            }
            if (!BlockUtil.canPlace(pos)) {
                continue;
            }
            if (!offhand && !switched && slot != old) {
                if (mode == SwitchMode.Silent) {
                    InventoryUtil.switchSilent(slot);
                } else if (mode == SwitchMode.Normal) {
                    InventoryUtil.switchTo(slot);
                } else {
                    return 0;
                }
                switched = true;
            }
            if (BlockUtil.place(pos, offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, rotate, true)) {
                placed++;
            }
        }
        if (switched) {
            if (mode == SwitchMode.Silent) {
                InventoryUtil.switchSilent(old);
            } else {
                InventoryUtil.switchTo(old);
            }
        }
        return placed;
    }

    public static BlockPos feet(Entity entity) {
        return BlockPos.containing(entity.getX(), entity.getY() + 0.2, entity.getZ());
    }
}
