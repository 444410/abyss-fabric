package me.ciruu.abyss.modules.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public class InvViewer extends HudModule {
    public InvViewer() {
        super("InvViewer", "Shows your inventory on the HUD.", -1, 40);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        width = 9 * 18 + 4;
        height = 3 * 18 + 4;
        g.fill(0, 0, width, height, 0x80000000);
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            int col = (i - 9) % 9;
            int row = (i - 9) / 9;
            g.item(stack, 2 + col * 18, 2 + row * 18);
            g.itemDecorations(mc.font, stack, 2 + col * 18, 2 + row * 18);
        }
    }
}
