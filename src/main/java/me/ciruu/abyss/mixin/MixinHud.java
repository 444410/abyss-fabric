package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.modules.render.NoRender;
import me.ciruu.abyss.util.Projection;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class MixinHud {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void abyss$render2d(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        Projection.update();
        Abyss.EVENT_BUS.post(new EventRender2D(graphics, Projection.partialTicks()));
    }

    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void abyss$noOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.overlays.on()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractConfusionOverlay", at = @At("HEAD"), cancellable = true)
    private void abyss$noNausea(GuiGraphicsExtractor graphics, float strength, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.nausea.on()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractVignette", at = @At("HEAD"), cancellable = true)
    private void abyss$noVignette(GuiGraphicsExtractor graphics, Entity entity, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.vignette.on()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void abyss$noPortal(GuiGraphicsExtractor graphics, float alpha, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.portal.on()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractBossOverlay", at = @At("HEAD"), cancellable = true)
    private void abyss$noBoss(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.bossBar.on()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void abyss$noEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        NoRender module = NoRender.get();
        if (module != null && module.isEnabled() && module.potionIcons.on()) {
            ci.cancel();
        }
    }
}
