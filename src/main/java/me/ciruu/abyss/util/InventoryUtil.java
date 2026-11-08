package me.ciruu.abyss.util;

import java.util.function.Predicate;
import me.ciruu.abyss.Globals;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class InventoryUtil implements Globals {
    private InventoryUtil() {
    }

    public static int findHotbar(Predicate<ItemStack> test) {
        for (int i = 0; i < 9; i++) {
            if (test.test(mc.player.getInventory().getItem(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int findHotbar(Item item) {
        return findHotbar(stack -> stack.is(item));
    }

    public static int findHotbarBlock(Block block) {
        return findHotbar(stack -> stack.getItem() instanceof BlockItem bi && bi.getBlock() == block);
    }

    public static int findInventory(Predicate<ItemStack> test, boolean includeHotbar) {
        for (int i = includeHotbar ? 0 : 9; i < 36; i++) {
            if (test.test(mc.player.getInventory().getItem(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int count(Item item) {
        int count = 0;
        for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static int selected() {
        return mc.player.getInventory().getSelectedSlot();
    }

    public static void switchTo(int slot) {
        if (slot < 0 || slot > 8 || slot == selected()) {
            return;
        }
        mc.player.getInventory().setSelectedSlot(slot);
        PacketUtil.send(new ServerboundSetCarriedItemPacket(slot));
    }

    public static void switchSilent(int slot) {
        PacketUtil.send(new ServerboundSetCarriedItemPacket(slot));
    }

    public static int toContainerSlot(int index) {
        return index < 9 ? index + 36 : index;
    }

    public static void click(int containerSlot, int button, ContainerInput input) {
        mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, containerSlot, button, input, mc.player);
    }

    public static void move(int from, int to) {
        click(from, 0, ContainerInput.PICKUP);
        click(to, 0, ContainerInput.PICKUP);
        click(from, 0, ContainerInput.PICKUP);
    }

    public static void swapWithHotbar(int containerSlot, int hotbar) {
        click(containerSlot, hotbar, ContainerInput.SWAP);
    }

    public static final int OFFHAND = 45;
}
