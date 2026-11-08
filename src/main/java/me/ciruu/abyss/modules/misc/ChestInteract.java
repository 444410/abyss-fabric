package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ChestInteract extends Module {
    public enum Mode {
        Steal, Store, Drop
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Steal);
    private final Setting<Float> delay = new Setting<>("Delay", "Delay in seconds", this, 0.05f, 0f, 10f);
    private final Setting<Boolean> onlyShulkers = new Setting<>("OnlyShulkers", "", this, false);
    private final Timer timer = new Timer();

    public ChestInteract() {
        super("ChestInteract", "Steal, store or drops items from a chest.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (!(mc.gui.screen() instanceof AbstractContainerScreen<?>) || mc.gui.screen() instanceof InventoryScreen || !timer.passed((long) (delay.f() * 1000))) {
            return;
        }
        AbstractContainerMenu menu = mc.player.containerMenu;
        int containerSlots = menu.slots.size() - 36;
        for (Slot slot : menu.slots) {
            boolean inContainer = slot.index < containerSlots;
            if (!slot.hasItem() || (mode.is(Mode.Store) == inContainer)) {
                continue;
            }
            if (onlyShulkers.on() && !(slot.getItem().getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock)) {
                continue;
            }
            if (mode.is(Mode.Drop)) {
                mc.gameMode.handleContainerInput(menu.containerId, slot.index, 1, ContainerInput.THROW, mc.player);
            } else {
                mc.gameMode.handleContainerInput(menu.containerId, slot.index, 0, ContainerInput.QUICK_MOVE, mc.player);
            }
            timer.reset();
            if (delay.f() > 0) {
                return;
            }
        }
    });
}
