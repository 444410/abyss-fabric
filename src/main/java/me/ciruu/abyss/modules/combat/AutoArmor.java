package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;

public class AutoArmor extends Module {
    private final Setting<Integer> delay = new Setting<>("Delay(ms)", "", this, 50, 0, 500);
    private final Setting<Boolean> curseOfBinding = new Setting<>("CurseOfBinding", "Allow putting on cursed armour", this, false);
    private final Setting<Boolean> elytraPriority = new Setting<>("ElytraPriority", "Keep an elytra in the chest slot", this, false);
    private final Timer timer = new Timer();

    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public AutoArmor() {
        super("AutoArmor", "Automatically puts on armor.", Category.COMBAT);
    }

    private static int containerSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            default -> 8;
        };
    }

    private int score(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) {
            return -1;
        }
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != slot) {
            return -1;
        }
        if (slot == EquipmentSlot.CHEST && stack.is(Items.ELYTRA)) {
            return elytraPriority.on() ? 1000 : 0;
        }
        if (!curseOfBinding.on() && DamageUtil.level(stack, Enchantments.BINDING_CURSE) > 0) {
            return -1;
        }
        double[] armor = {0};
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        modifiers.forEach(slot, (attribute, modifier) -> {
            if (attribute.is(Attributes.ARMOR) || attribute.is(Attributes.ARMOR_TOUGHNESS)) {
                armor[0] += modifier.amount();
            }
        });
        return (int) (armor[0] * 10) + DamageUtil.level(stack, Enchantments.PROTECTION) * 3 + DamageUtil.level(stack, Enchantments.BLAST_PROTECTION) * 2;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (!timer.passed(delay.i()) || (mc.gui.screen() instanceof AbstractContainerScreen<?> && !(mc.gui.screen() instanceof InventoryScreen))) {
            return;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack worn = mc.player.getItemBySlot(slot);
            if (!worn.isEmpty() && DamageUtil.level(worn, Enchantments.BINDING_CURSE) > 0) {
                continue;
            }
            int best = -1;
            int bestScore = score(worn, slot);
            for (int i = 0; i < 36; i++) {
                int s = score(mc.player.getInventory().getItem(i), slot);
                if (s > bestScore) {
                    bestScore = s;
                    best = i;
                }
            }
            if (best == -1) {
                continue;
            }
            int from = InventoryUtil.toContainerSlot(best);
            if (worn.isEmpty()) {
                InventoryUtil.click(from, 0, ContainerInput.QUICK_MOVE);
            } else {
                InventoryUtil.click(from, 0, ContainerInput.PICKUP);
                InventoryUtil.click(containerSlot(slot), 0, ContainerInput.PICKUP);
                InventoryUtil.click(from, 0, ContainerInput.PICKUP);
            }
            timer.reset();
            return;
        }
    });
}
