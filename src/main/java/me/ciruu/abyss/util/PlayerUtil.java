package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;

public final class PlayerUtil implements Globals {
    private PlayerUtil() {
    }

    public static void swing(InteractionHand hand) {
        mc.player.swing(hand, SwingAnimation.DEFAULT, false);
    }
}
