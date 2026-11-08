package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudModule extends Module {
    public final Setting<Integer> x;
    public final Setting<Integer> y;
    public int width = 50;
    public int height = 10;

    protected HudModule(String name, String description, int defaultX, int defaultY) {
        super(name, description, Category.HUD);
        x = new Setting<>("X", "", this, defaultX, -1, 4000).visible(() -> false);
        y = new Setting<>("Y", "", this, defaultY, -1, 4000).visible(() -> false);
    }

    public abstract void draw(GuiGraphicsExtractor g, float partialTicks);

    public int posX(GuiGraphicsExtractor g) {
        return Math.max(0, Math.min(g.guiWidth() - width, x.i() < 0 ? g.guiWidth() - width - 2 : x.i()));
    }

    public int posY(GuiGraphicsExtractor g) {
        return Math.max(0, Math.min(g.guiHeight() - height, y.i() < 0 ? g.guiHeight() - height - 2 : y.i()));
    }

    protected boolean rightSide(GuiGraphicsExtractor g) {
        return posX(g) + width / 2 > g.guiWidth() / 2;
    }

    protected static int accent() {
        return ColorUtil.accent();
    }
}
