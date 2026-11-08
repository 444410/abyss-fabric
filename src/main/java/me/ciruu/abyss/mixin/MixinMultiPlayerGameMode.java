package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerClickBlock;
import me.ciruu.abyss.events.EventPlayerDamageBlock;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {
    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void abyss$click(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Abyss.EVENT_BUS.post(new EventPlayerClickBlock(pos, direction)).isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void abyss$damage(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Abyss.EVENT_BUS.post(new EventPlayerDamageBlock(pos, direction)).isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
