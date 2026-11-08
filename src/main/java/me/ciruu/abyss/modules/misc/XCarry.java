package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;

public class XCarry extends Module {
    public XCarry() {
        super("XCarry", "Carry items in the crafting slots.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packets = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (event.getPacket() instanceof ServerboundContainerClosePacket packet && packet.getContainerId() == mc.player.inventoryMenu.containerId) {
            event.cancel();
        }
    });
}
