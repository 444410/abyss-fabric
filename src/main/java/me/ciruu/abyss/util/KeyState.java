package me.ciruu.abyss.util;

import com.mojang.blaze3d.platform.InputConstants;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.mixin.accessor.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

public final class KeyState implements Globals {
    private KeyState() {
    }

    public static boolean physical(KeyMapping mapping) {
        InputConstants.Key key = ((KeyMappingAccessor) mapping).abyss$getKey();
        return key.getType() == InputConstants.Type.KEYBOARD && key.getValue() >= 0 && InputConstants.isKeyDown(key.getValue());
    }

    public static void updateMovementKeys() {
        for (KeyMapping mapping : new KeyMapping[]{mc.options.keyUp, mc.options.keyDown, mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keySprint}) {
            mapping.setDown(physical(mapping));
        }
    }
}
