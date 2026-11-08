package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.network.protocol.Packet;

public final class PacketUtil implements Globals {
    private static boolean silent;

    private PacketUtil() {
    }

    public static boolean isSilent() {
        return silent;
    }

    public static void send(Packet<?> packet) {
        if (mc.getConnection() != null) {
            mc.getConnection().send(packet);
        }
    }

    public static void sendSilent(Packet<?> packet) {
        if (mc.getConnection() == null) {
            return;
        }
        silent = true;
        try {
            mc.getConnection().send(packet);
        } finally {
            silent = false;
        }
    }
}
