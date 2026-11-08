package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.util.InventoryUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TotemCounter extends HudModule {
    public TotemCounter() {
        super("TotemCounter", "Shows how many totems you have.", 380, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        int count = InventoryUtil.count(Items.TOTEM_OF_UNDYING);
        g.item(new ItemStack(Items.TOTEM_OF_UNDYING), 0, 0);
        g.text(mc.font, String.valueOf(count), 17, 4, count > 0 ? 0xFFFFFFFF : 0xFFFF5555, true);
        width = 30;
        height = 16;
    }
}
