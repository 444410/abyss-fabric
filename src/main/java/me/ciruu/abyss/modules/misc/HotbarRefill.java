package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.InventoryUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

public class HotbarRefill extends Module {
    private final Setting<Integer> stackThreshold = new Setting<>("StackThreshold", "Refill a stack below this count", this, 16, 1, 63);
    private final Setting<Integer> delay = new Setting<>("Delay", "TickDelay", this, 2, 1, 100);
    private final Setting<Boolean> noGui = new Setting<>("NoGui", "Not while a container is open", this, true);
    private int ticks;

    public HotbarRefill() {
        super("HotbarRefill", "Refill items from your inventory to your hotbar.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (++ticks < delay.i() || (noGui.on() && mc.gui.screen() instanceof AbstractContainerScreen<?>)) {
            return;
        }
        ticks = 0;
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            ItemStack stack = mc.player.getInventory().getItem(hotbar);
            if (stack.isEmpty() || !stack.isStackable() || stack.getCount() >= Math.min(stackThreshold.i(), stack.getMaxStackSize())) {
                continue;
            }
            int from = InventoryUtil.findInventory(s -> ItemStack.isSameItemSameComponents(s, stack), false);
            if (from != -1) {
                InventoryUtil.click(from, 0, ContainerInput.QUICK_MOVE);
                return;
            }
        }
    });
}
