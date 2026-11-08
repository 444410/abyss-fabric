package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;

public class MultiTask extends Module {
    public MultiTask() {
        super("MultiTask", "Attack and mine while eating or using items.", Category.COMBAT);
    }

    public static boolean active() {
        return Abyss.moduleManager != null && Abyss.moduleManager.isModuleEnabled(MultiTask.class);
    }
}
