package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.util.InventoryUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CrystalCounter extends HudModule {
    public CrystalCounter() {
        super("CrystalCounter", "Shows how many crystals, gapples and xp bottles you have.", 340, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        int xx = 0;
        for (var item : new net.minecraft.world.item.Item[]{Items.END_CRYSTAL, Items.ENCHANTED_GOLDEN_APPLE, Items.EXPERIENCE_BOTTLE}) {
            g.item(new ItemStack(item), xx, 0);
            g.text(mc.font, String.valueOf(InventoryUtil.count(item)), xx + 17, 4, 0xFFFFFFFF, true);
            xx += 36;
        }
        width = xx;
        height = 16;
    }
}
