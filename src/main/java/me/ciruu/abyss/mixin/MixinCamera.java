package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.render.ViewClip;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void abyss$viewClip(float cameraDist, CallbackInfoReturnable<Float> cir) {
        ViewClip module = ViewClip.active();
        if (module != null) {
            cir.setReturnValue(module.distance.f());
        }
    }
}
