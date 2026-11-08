package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmourHUD extends HudModule {
    private final Setting<Boolean> percent = new Setting<>("Percent", "Durability in %", this, true);

    public ArmourHUD() {
        super("ArmourHUD", "Shows your armour and its durability.", 400, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int xx = 0;
        for (EquipmentSlot slot : slots) {
            ItemStack stack = mc.player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                g.item(stack, xx, 8);
                g.itemDecorations(mc.font, stack, xx, 8);
                if (percent.on() && stack.isDamageableItem()) {
                    int pct = Math.round(100f * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
                    String s = pct + "%";
                    g.text(mc.font, s, xx + 8 - mc.font.width(s) / 2, 0, ColorUtil.healthColor(pct, 100), true);
                }
            }
            xx += 18;
        }
        width = 72;
        height = 24;
    }
}
