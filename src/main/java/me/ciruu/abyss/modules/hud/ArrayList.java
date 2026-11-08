package me.ciruu.abyss.modules.hud;

import java.util.Comparator;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ArrayList extends HudModule {
    public enum Sort {
        Length, Alphabet
    }

    private final Setting<Sort> sort = new Setting<>("Sort", "", this, Sort.Length);
    private final Setting<Boolean> rainbow = new Setting<>("Rainbow", "", this, true);
    private final Setting<Boolean> background = new Setting<>("Background", "", this, false);
    private final Setting<Boolean> line = new Setting<>("Line", "Side line", this, true);
    private final Setting<Boolean> suffix = new Setting<>("Suffix", "Show module info", this, true);
    private final Setting<Boolean> animation = new Setting<>("Animation", "Slide in", this, true);

    public ArrayList() {
        super("ArrayList", "Lists the enabled modules.", -1, 2);
    }

    private String label(Module m) {
        String info = suffix.on() ? m.getHudInfo() : null;
        return m.getDisplayName() + (info == null ? "" : ChatFormatting.GRAY + " [" + ChatFormatting.RESET + info + ChatFormatting.GRAY + "]");
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        List<Module> mods = new java.util.ArrayList<>();
        for (Module m : Abyss.moduleManager.getModules()) {
            if (m.isEnabled() && !m.isHidden() && !(m instanceof HudModule)) {
                mods.add(m);
            }
        }
        if (sort.is(Sort.Length)) {
            mods.sort(Comparator.comparingInt((Module m) -> mc.font.width(label(m))).reversed());
        } else {
            mods.sort(Comparator.comparing(Module::getDisplayName));
        }
        boolean right = rightSide(g);
        int maxW = 40;
        for (Module m : mods) {
            maxW = Math.max(maxW, mc.font.width(label(m)) + 4);
        }
        width = maxW;
        int yy = 0;
        int i = 0;
        for (Module m : mods) {
            m.animation = animation.on() ? Math.min(1f, m.animation + 0.08f) : 1f;
            String text = label(m);
            int w = mc.font.width(text);
            int slide = (int) ((1f - m.animation) * (w + 6));
            int tx = right ? maxW - w - 2 + slide : 2 - slide;
            int color = rainbow.on() ? ColorUtil.rainbow(i * 300L, 1.5f, 0.6f, 1f) : accent();
            if (background.on()) {
                g.fill(right ? tx - 2 : 0, yy, right ? maxW : tx + w + 2, yy + 10, 0x80000000);
            }
            if (line.on()) {
                g.fill(right ? maxW - 1 : 0, yy, right ? maxW : 1, yy + 10, color);
            }
            g.text(mc.font, text, tx, yy + 1, color, true);
            yy += 10;
            i++;
        }
        height = Math.max(10, yy);
    }
}
