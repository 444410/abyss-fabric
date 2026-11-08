package me.ciruu.abyss.modules.render;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;

public class TabFriends extends Module {
    public TabFriends() {
        super("TabFriends", "Highlights your friends in the tab list.", Category.RENDER);
    }

    public static boolean active() {
        return Abyss.moduleManager != null && Abyss.moduleManager.isModuleEnabled(TabFriends.class);
    }
}
