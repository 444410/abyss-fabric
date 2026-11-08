package me.ciruu.abyss.modules.render;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;

public class ViewClip extends Module {
    public final Setting<Float> distance = new Setting<>("Distance", "Third person camera distance", this, 4f, 1f, 20f);

    public ViewClip() {
        super("ViewClip", "The third person camera goes through blocks.", Category.RENDER);
    }

    public static ViewClip active() {
        ViewClip module = Abyss.moduleManager == null ? null : Abyss.moduleManager.get(ViewClip.class);
        return module != null && module.isEnabled() ? module : null;
    }
}
