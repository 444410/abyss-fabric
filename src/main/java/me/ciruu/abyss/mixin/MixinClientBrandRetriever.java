package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.exploit.NoHandShake;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public abstract class MixinClientBrandRetriever {
    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void abyss$brand(CallbackInfoReturnable<String> cir) {
        if (NoHandShake.active()) {
            cir.setReturnValue("vanilla");
        }
    }
}
