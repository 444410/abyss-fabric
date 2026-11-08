package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class ChatUtil implements Globals {
    public static final String PREFIX = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "Abyss" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.RESET;

    private ChatUtil() {
    }

    public static void send(String message) {
        raw(PREFIX + message);
    }

    public static void raw(String message) {
        if (mc.gui == null || mc.gui.hud == null) {
            return;
        }
        mc.execute(() -> mc.gui.hud.getChat().addClientSystemMessage(Component.literal(message)));
    }

    public static void error(String message) {
        send(ChatFormatting.RED + message);
    }

    public static void sendServer(String message) {
        if (mc.getConnection() == null) {
            return;
        }
        if (message.startsWith("/")) {
            mc.getConnection().sendCommand(message.substring(1));
        } else {
            mc.getConnection().sendChat(message);
        }
    }
}
