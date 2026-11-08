package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.render.NoRender;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class MixinLevel {
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    private void abyss$noRain(float a, CallbackInfoReturnable<Float> cir) {
        if (((Level) (Object) this).isClientSide() && NoRender.on(m -> m.weather)) {
            cir.setReturnValue(0f);
        }
    }

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
    private void abyss$noThunder(float a, CallbackInfoReturnable<Float> cir) {
        if (((Level) (Object) this).isClientSide() && NoRender.on(m -> m.weather)) {
            cir.setReturnValue(0f);
        }
    }
}
