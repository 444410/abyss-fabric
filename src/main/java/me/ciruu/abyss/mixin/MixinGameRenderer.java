package me.ciruu.abyss.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ciruu.abyss.modules.render.NoRender;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void abyss$noHurtCam(CameraRenderState camera, PoseStack poseStack, CallbackInfo ci) {
        if (NoRender.on(m -> m.hurtCam)) {
            ci.cancel();
        }
    }
}
