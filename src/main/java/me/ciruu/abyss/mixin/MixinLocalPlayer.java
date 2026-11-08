package me.ciruu.abyss.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerMotionUpdate;
import me.ciruu.abyss.events.EventPlayerMove;
import me.ciruu.abyss.events.EventPlayerPushOutOfBlocks;
import me.ciruu.abyss.events.EventPlayerSlowdown;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.zero.alpine.event.EventStage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {
    @Unique
    private EventPlayerMotionUpdate abyss$motion;
    @Unique
    private boolean abyss$moving;

    public MixinLocalPlayer(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void abyss$tick(CallbackInfo ci) {
        if (this.level() != null) {
            Abyss.EVENT_BUS.post(EventPlayerUpdate.INSTANCE);
        }
    }

    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    private void abyss$preMotion(CallbackInfo ci) {
        abyss$motion = Abyss.EVENT_BUS.post(new EventPlayerMotionUpdate(EventStage.PRE, getYRot(), getXRot(), onGround()));
        if (abyss$motion.isCancelled()) {
            abyss$motion = null;
            ci.cancel();
        }
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
    private void abyss$postMotion(CallbackInfo ci) {
        abyss$motion = null;
        Abyss.EVENT_BUS.post(new EventPlayerMotionUpdate(EventStage.POST, getYRot(), getXRot(), onGround()));
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getYRot()F"))
    private float abyss$packetYaw(float original) {
        return abyss$motion != null ? abyss$motion.yaw : original;
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXRot()F"))
    private float abyss$packetPitch(float original) {
        return abyss$motion != null ? abyss$motion.pitch : original;
    }

    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;onGround()Z"))
    private boolean abyss$packetGround(boolean original) {
        return abyss$motion != null ? abyss$motion.onGround : original;
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void abyss$move(MoverType type, Vec3 delta, CallbackInfo ci) {
        if (abyss$moving) {
            return;
        }
        EventPlayerMove event = Abyss.EVENT_BUS.post(new EventPlayerMove(type, delta));
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        Vec3 modified = event.motion();
        if (!modified.equals(delta)) {
            ci.cancel();
            abyss$moving = true;
            try {
                ((LocalPlayer) (Object) this).move(type, modified);
            } finally {
                abyss$moving = false;
            }
        }
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
    private void abyss$pushOutOfBlocks(double x, double z, CallbackInfo ci) {
        if (Abyss.EVENT_BUS.post(new EventPlayerPushOutOfBlocks()).isCancelled()) {
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "itemUseSpeedMultiplier", at = @At("RETURN"))
    private float abyss$slowdown(float original) {
        return Abyss.EVENT_BUS.post(new EventPlayerSlowdown(original)).multiplier;
    }
}
