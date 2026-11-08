package me.ciruu.abyss;

import net.minecraft.client.Minecraft;

public interface Globals {
    Minecraft mc = Minecraft.getInstance();

    static boolean nullCheck() {
        return mc.player == null || mc.level == null;
    }
}
