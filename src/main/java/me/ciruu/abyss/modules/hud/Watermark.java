package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.settings.Setting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Watermark extends HudModule {
    private final Setting<String> text = new Setting<>("Text", "", this, "Abyss");
    private final Setting<Boolean> version = new Setting<>("Version", "", this, true);

    public Watermark() {
        super("Watermark", "Shows the client name.", 2, 2);
        setEnabled(true);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        String s = text.getValue() + (version.on() ? ChatFormatting.GRAY + " " + Abyss.VERSION : "");
        width = mc.font.width(s);
        height = 9;
        g.text(mc.font, s, 0, 0, accent(), true);
    }
}
