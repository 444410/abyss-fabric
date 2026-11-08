package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.render.FullBright;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class MixinLightmapRenderStateExtractor {
    @Inject(method = "extract", at = @At("RETURN"))
    private void abyss$fullBright(LightmapRenderState state, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!state.needsUpdate || mc.level == null || Abyss.moduleManager == null) {
            return;
        }
        FullBright module = Abyss.moduleManager.get(FullBright.class);
        if (module != null && module.isEnabled()) {
            module.apply(state);
        }
    }
}
