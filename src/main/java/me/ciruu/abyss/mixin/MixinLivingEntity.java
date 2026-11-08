package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerJump;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void abyss$jump(CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player && Abyss.EVENT_BUS.post(new EventPlayerJump()).isCancelled()) {
            ci.cancel();
        }
    }
}
