package me.ciruu.abyss.managers;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.gui.ClickGuiScreen;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.hud.HudModule;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudManager implements Listenable, Globals {
    @EventHandler
    private final Listener<EventRender2D> render = new Listener<>(EventRender2D.class, event -> {
        if (mc.gui.hud.isHidden() || mc.gui.screen() instanceof ClickGuiScreen) {
            return;
        }
        renderAll(event.graphics(), event.partialTicks());
    });

    public void renderAll(GuiGraphicsExtractor g, float partialTicks) {
        for (Module module : Abyss.moduleManager.getModules()) {
            if (module instanceof HudModule hud && hud.isEnabled()) {
                g.pose().pushMatrix();
                g.pose().translate(hud.posX(g), hud.posY(g));
                try {
                    hud.draw(g, partialTicks);
                } catch (Exception ignored) {
                }
                g.pose().popMatrix();
            }
        }
    }
}
