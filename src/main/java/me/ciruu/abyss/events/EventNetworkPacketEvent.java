package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;
import net.minecraft.network.protocol.Packet;

public final class EventNetworkPacketEvent extends Cancellable {
    private Packet<?> packet;

    public EventNetworkPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
