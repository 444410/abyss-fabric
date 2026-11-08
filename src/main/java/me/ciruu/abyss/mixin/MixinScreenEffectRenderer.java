package me.ciruu.abyss.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ciruu.abyss.modules.render.NoRender;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {
    @Inject(method = "submitFire", at = @At("HEAD"), cancellable = true)
    private static void abyss$noFire(PoseStack poseStack, SubmitNodeCollector collector, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (NoRender.on(m -> m.fire)) {
            ci.cancel();
        }
    }
}
