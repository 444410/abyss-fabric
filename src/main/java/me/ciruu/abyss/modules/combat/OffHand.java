package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.HoleUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class OffHand extends Module {
    public enum Mode {
        Totem, Crystal, Gapple
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Mode", this, Mode.Totem);
    private final Setting<Float> toggleHealth = new Setting<>("ToggleHealth", "Back to a totem below this health", this, 13f, 0f, 36f);
    private final Setting<Boolean> gappleHole = new Setting<>("GappleHole", "Hold a gapple while you are in a hole", this, false);
    private final Setting<Boolean> rightClickGap = new Setting<>("RightClickGap", "Gapple while holding right click with a sword", this, false);
    private final Setting<Boolean> crystalAC = new Setting<>("CrystalAC", "Will put a crystal in your offhand if CA is active", this, false);
    private final Setting<Boolean> hotbarFirst = new Setting<>("HotbarFirst", "Take items from the hotbar first", this, false);
    private final Setting<Integer> delay = new Setting<>("Delay", "Ticks between swaps", this, 0, 0, 10);
    private int ticks;

    public OffHand() {
        super("OffHand", "Automatically put certain items in your offHand.", Category.COMBAT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + String.valueOf(InventoryUtil.count(wanted()));
    }

    private Item wanted() {
        if (DamageUtil.health(mc.player) <= toggleHealth.f() || mc.player.fallDistance > 10) {
            return Items.TOTEM_OF_UNDYING;
        }
        if (rightClickGap.on() && mc.options.keyUse.isDown() && mc.player.getMainHandItem().is(ItemTags.SWORDS)) {
            return Items.ENCHANTED_GOLDEN_APPLE;
        }
        if (gappleHole.on() && HoleUtil.isInHole(mc.player)) {
            return Items.ENCHANTED_GOLDEN_APPLE;
        }
        if (crystalAC.on() && AutoCrystal.isActive()) {
            return Items.END_CRYSTAL;
        }
        return switch (mode.getValue()) {
            case Totem -> Items.TOTEM_OF_UNDYING;
            case Crystal -> Items.END_CRYSTAL;
            case Gapple -> Items.ENCHANTED_GOLDEN_APPLE;
        };
    }

    private int find(Item item) {
        int slot = -1;
        if (hotbarFirst.on()) {
            slot = InventoryUtil.findHotbar(item);
        }
        if (slot == -1) {
            slot = InventoryUtil.findInventory(s -> s.is(item), true);
        }
        if (slot == -1 && item == Items.ENCHANTED_GOLDEN_APPLE) {
            slot = InventoryUtil.findInventory(s -> s.is(Items.GOLDEN_APPLE), true);
        }
        return slot;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.gui.screen() instanceof AbstractContainerScreen<?> && !(mc.gui.screen() instanceof InventoryScreen)) {
            return;
        }
        if (++ticks <= delay.i()) {
            return;
        }
        Item want = wanted();
        if (mc.player.getOffhandItem().is(want) || (want == Items.ENCHANTED_GOLDEN_APPLE && mc.player.getOffhandItem().is(Items.GOLDEN_APPLE))) {
            return;
        }
        int index = find(want);
        if (index == -1 && want != Items.TOTEM_OF_UNDYING) {
            index = find(Items.TOTEM_OF_UNDYING);
        }
        if (index == -1) {
            return;
        }
        if (index < 9) {
            InventoryUtil.click(InventoryUtil.OFFHAND, index, ContainerInput.SWAP);
        } else {
            int slot = InventoryUtil.toContainerSlot(index);
            InventoryUtil.click(slot, 0, ContainerInput.PICKUP);
            InventoryUtil.click(InventoryUtil.OFFHAND, 0, ContainerInput.PICKUP);
            if (!mc.player.containerMenu.getCarried().isEmpty()) {
                InventoryUtil.click(slot, 0, ContainerInput.PICKUP);
            }
        }
        ticks = 0;
    });
}
