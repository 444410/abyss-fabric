package me.ciruu.abyss.events;

import net.minecraft.network.protocol.Packet;

public record EventNetworkPostPacketEvent(Packet<?> packet) {
    public Packet<?> getPacket() {
        return packet;
    }
}
