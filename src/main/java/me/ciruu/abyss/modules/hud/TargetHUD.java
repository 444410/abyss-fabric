package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.combat.AutoCrystal;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.EntityUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TargetHUD extends HudModule {
    public TargetHUD() {
        super("TargetHUD", "Shows info about your target.", 300, 150);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        width = 140;
        height = 46;
        Player target = AutoCrystal.isActive() ? Abyss.moduleManager.get(AutoCrystal.class).getTarget() : null;
        if (target == null) {
            target = EntityUtil.closestTarget(12);
        }
        if (target == null) {
            return;
        }
        g.fill(0, 0, width, height, 0x90000000);
        g.fill(0, 0, width, 1, accent());
        String name = target.getGameProfile().name();
        g.text(mc.font, name, 4, 4, 0xFFFFFFFF, true);
        float hp = DamageUtil.health(target);
        int bar = (int) ((width - 8) * Math.min(1f, hp / 36f));
        g.fill(4, 15, width - 4, 19, 0xFF333333);
        g.fill(4, 15, 4 + bar, 19, ColorUtil.healthColor(hp, 36f));
        String info = String.format("%.1f hp  %.1fm  pops %d", hp, mc.player.distanceTo(target), Abyss.totemPopManager.getPops(name));
        g.text(mc.font, info, 4, 22, 0xFFAAAAAA, true);
        int xx = 4;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            ItemStack stack = target.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                g.pose().pushMatrix();
                g.pose().translate(xx, 31);
                g.pose().scale(0.75f, 0.75f);
                g.item(stack, 0, 0);
                g.pose().popMatrix();
            }
            xx += 14;
        }
    }
}
