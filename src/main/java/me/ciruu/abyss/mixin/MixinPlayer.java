package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerPushedByWater;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @Inject(method = "isPushedByFluid", at = @At("HEAD"), cancellable = true)
    private void abyss$fluidPush(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Minecraft.getInstance().player && Abyss.EVENT_BUS.post(new EventPlayerPushedByWater()).isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
