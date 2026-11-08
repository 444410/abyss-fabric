package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.exploit.LiquidInteract;
import me.ciruu.abyss.modules.exploit.NoEntityTrace;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerPick {
    @ModifyArg(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"), index = 2)
    private static boolean abyss$liquids(boolean original) {
        return original || LiquidInteract.active();
    }

    @Inject(method = "raycastHitResult", at = @At("RETURN"), cancellable = true)
    private void abyss$noEntityTrace(float a, Entity cameraEntity, CallbackInfoReturnable<HitResult> cir) {
        if (cir.getReturnValue() instanceof EntityHitResult && NoEntityTrace.active()) {
            LocalPlayer self = (LocalPlayer) (Object) this;
            cir.setReturnValue(cameraEntity.pick(self.blockInteractionRange(), a, LiquidInteract.active()));
        }
    }
}
