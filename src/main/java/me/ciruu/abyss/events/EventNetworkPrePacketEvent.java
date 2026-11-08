package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;
import net.minecraft.network.protocol.Packet;

public final class EventNetworkPrePacketEvent extends Cancellable {
    private Packet<?> packet;

    public EventNetworkPrePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
