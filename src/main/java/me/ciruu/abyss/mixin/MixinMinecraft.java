package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventClientTick;
import me.ciruu.abyss.events.EventWorldChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void abyss$start(net.minecraft.client.main.GameConfig config, CallbackInfo ci) {
        Abyss.start();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void abyss$tick(CallbackInfo ci) {
        if (Abyss.moduleManager == null) {
            return;
        }
        Abyss.EVENT_BUS.post(EventClientTick.INSTANCE);
    }

    @Inject(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At("HEAD"))
    private void abyss$levelChange(@Nullable ClientLevel level, boolean stopSound, CallbackInfo ci) {
        Abyss.EVENT_BUS.post(EventWorldChange.INSTANCE);
    }
}
