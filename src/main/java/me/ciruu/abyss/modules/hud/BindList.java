package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.client.ClickGUI;
import me.ciruu.abyss.util.KeyUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class BindList extends HudModule {
    public BindList() {
        super("BindList", "Lists your keybinds.", 2, 60);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        int yy = 0;
        int w = 40;
        for (Module m : Abyss.moduleManager.getModules()) {
            if (m.getKey() < 0 || m instanceof ClickGUI) {
                continue;
            }
            String s = (m.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.GRAY) + m.getDisplayName() + ChatFormatting.WHITE + " [" + KeyUtil.name(m.getKey()) + "]";
            g.text(mc.font, s, 0, yy, 0xFFFFFFFF, true);
            w = Math.max(w, mc.font.width(s));
            yy += 10;
        }
        width = w;
        height = Math.max(9, yy);
    }
}
