package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.combat.AutoCrystal;
import me.ciruu.abyss.modules.combat.AutoTrap;
import me.ciruu.abyss.modules.combat.KillAura;
import me.ciruu.abyss.modules.combat.OffHand;
import me.ciruu.abyss.modules.combat.Surround;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CombatInfo extends HudModule {
    public CombatInfo() {
        super("CombatInfo", "Shows which combat modules are on.", 2, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        Class<?>[] list = {AutoCrystal.class, KillAura.class, Surround.class, AutoTrap.class, OffHand.class};
        String[] names = {"AC", "KA", "SU", "AT", "OH"};
        int yy = 0;
        for (int i = 0; i < list.length; i++) {
            @SuppressWarnings("unchecked")
            Module m = Abyss.moduleManager.get((Class<? extends Module>) list[i]);
            boolean on = m != null && m.isEnabled();
            g.text(mc.font, names[i] + ": " + (on ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF"), 0, yy, 0xFFFFFFFF, true);
            yy += 10;
        }
        width = 40;
        height = yy;
    }
}
