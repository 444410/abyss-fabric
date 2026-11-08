package me.ciruu.abyss.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.ChannelFutureListener;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventNetworkPostPacketEvent;
import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.util.PacketUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MixinConnection {
    @Unique
    private boolean abyss$swapping;

    @Shadow
    public abstract void send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush);

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"), cancellable = true)
    private void abyss$send(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (abyss$swapping || PacketUtil.isSilent()) {
            return;
        }
        EventNetworkPrePacketEvent event = Abyss.EVENT_BUS.post(new EventNetworkPrePacketEvent(packet));
        if (event.isCancelled()) {
            ci.cancel();
        } else if (event.getPacket() != packet) {
            ci.cancel();
            abyss$swapping = true;
            try {
                send(event.getPacket(), listener, flush);
            } finally {
                abyss$swapping = false;
            }
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("RETURN"))
    private void abyss$sent(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (!abyss$swapping && !PacketUtil.isSilent()) {
            Abyss.EVENT_BUS.post(new EventNetworkPostPacketEvent(packet));
        }
    }

    @WrapOperation(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"))
    private void abyss$receive(Packet<?> packet, PacketListener listener, Operation<Void> original) {
        EventNetworkPacketEvent event = Abyss.EVENT_BUS.post(new EventNetworkPacketEvent(packet));
        if (!event.isCancelled()) {
            original.call(event.getPacket(), listener);
        }
    }
}
