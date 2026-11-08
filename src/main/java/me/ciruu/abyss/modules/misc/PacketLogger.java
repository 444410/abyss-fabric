package me.ciruu.abyss.modules.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;

public class PacketLogger extends Module {
    private final Setting<Boolean> client = new Setting<>("Client", "Logs client packets", this, true);
    private final Setting<Boolean> server = new Setting<>("Server", "Logs server packets", this, true);
    private final Setting<Boolean> logData = new Setting<>("LogData", "Logs data in packets", this, true);
    private final Setting<Boolean> saveFile = new Setting<>("SaveFile", "Saves to Abyss/packets.log", this, false);
    private final Setting<Boolean> showInChat = new Setting<>("ShowInChat", "Show in chat", this, false);

    public PacketLogger() {
        super("PacketLogger", "Allows you to log certain types of packets", Category.MISC);
    }

    private void log(String direction, Packet<?> packet) {
        String text = direction + " " + packet.getClass().getSimpleName() + (logData.on() ? " " + packet : "");
        Abyss.LOGGER.info(text);
        if (showInChat.on()) {
            ChatUtil.raw(ChatFormatting.GRAY + text);
        }
        if (saveFile.on()) {
            try {
                Files.createDirectories(Abyss.directory());
                Files.writeString(Abyss.directory().resolve("packets.log"), text + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ignored) {
            }
        }
    }

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> out = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (client.on()) {
            log("[C->S]", event.getPacket());
        }
    }, -1000);

    @EventHandler
    private final Listener<EventNetworkPacketEvent> in = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (server.on()) {
            log("[S->C]", event.getPacket());
        }
    }, -1000);
}
