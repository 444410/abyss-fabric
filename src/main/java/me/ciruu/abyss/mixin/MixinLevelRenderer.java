package me.ciruu.abyss.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Inject(method = "submitEntities", at = @At("RETURN"))
    private void abyss$renderWorld(PoseStack poseStack, LevelRenderState levelState, SubmitNodeCollector collector, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        RenderUtil.begin(poseStack, collector, levelState.cameraRenderState.pos);
        try {
            Abyss.EVENT_BUS.post(new EventRenderWorld(levelState.worldPartialTicks));
        } finally {
            RenderUtil.end();
        }
    }
}
