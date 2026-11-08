package me.ciruu.abyss.mixin.accessor;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
    @Mutable
    @Accessor("onGround")
    void abyss$setOnGround(boolean onGround);

    @Mutable
    @Accessor("y")
    void abyss$setY(double y);

    @Mutable
    @Accessor("yRot")
    void abyss$setYRot(float yRot);

    @Mutable
    @Accessor("xRot")
    void abyss$setXRot(float xRot);
}
