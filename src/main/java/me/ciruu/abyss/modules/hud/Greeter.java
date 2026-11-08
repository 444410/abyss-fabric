package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.settings.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Greeter extends HudModule {
    private final Setting<String> text = new Setting<>("Text", "%s is replaced by your name", this, "Welcome to Abyss, %s :^)");

    public Greeter() {
        super("Greeter", "Greets you.", 200, 2);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        String s = text.getValue().replace("%s", mc.player.getGameProfile().name());
        width = mc.font.width(s);
        height = 9;
        g.text(mc.font, s, 0, 0, accent(), true);
    }
}
