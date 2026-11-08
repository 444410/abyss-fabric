package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public final class GuiFont implements Globals {
    private static final Style STYLE = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("abyss", "gui")));

    private GuiFont() {
    }

    public static Component of(String text) {
        return Component.literal(text).setStyle(STYLE);
    }

    public static int width(String text) {
        return mc.font.width(of(text));
    }

    public static void draw(GuiGraphicsExtractor g, String text, int x, int y, int color, boolean shadow) {
        g.text(mc.font, of(text), x, y, color, shadow);
    }

    public static void centered(GuiGraphicsExtractor g, String text, int cx, int y, int color) {
        draw(g, text, cx - width(text) / 2, y, color, true);
    }
}
