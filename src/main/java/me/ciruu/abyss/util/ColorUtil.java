package me.ciruu.abyss.util;

import java.awt.Color;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.client.ClickGUI;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static int argb(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 255) << 24);
    }

    public static int rainbow(long offset, float speed, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offset) % (long) (10000f / Math.max(0.001f, speed))) / (10000f / Math.max(0.001f, speed));
        return 0xFF000000 | Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int accent() {
        ClickGUI gui = Abyss.moduleManager == null ? null : Abyss.moduleManager.get(ClickGUI.class);
        return gui == null ? 0xFF6A00C1 : gui.enabledColor();
    }

    public static int healthColor(float health, float max) {
        float ratio = Math.max(0f, Math.min(1f, health / max));
        return 0xFF000000 | Color.HSBtoRGB(ratio / 3f, 1f, 1f);
    }
}
