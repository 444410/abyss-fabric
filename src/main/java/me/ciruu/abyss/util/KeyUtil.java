package me.ciruu.abyss.util;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Locale;

public final class KeyUtil {
    private KeyUtil() {
    }

    public static String name(int key) {
        if (key < 0) {
            return "NONE";
        }
        try {
            return InputConstants.Type.KEYBOARD.getOrCreate(key).getDisplayName().getString().toUpperCase(Locale.ROOT);
        } catch (Exception e) {
            return String.valueOf(key);
        }
    }

    public static int parse(String name) {
        if (name == null || name.isBlank() || name.equalsIgnoreCase("NONE")) {
            return -1;
        }
        String upper = name.toUpperCase(Locale.ROOT);
        for (int key = 0; key < 512; key++) {
            if (name(key).equals(upper)) {
                return key;
            }
        }
        try {
            String id = name.startsWith("key.") ? name : "key.keyboard." + name.toLowerCase(Locale.ROOT);
            int value = InputConstants.getKey(id).getValue();
            if (value >= 0) {
                return value;
            }
        } catch (Exception ignored) {
        }
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
