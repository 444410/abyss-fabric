package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.exploit.NoSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntitySwing {
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("HEAD"), cancellable = true)
    private void abyss$noSwing(InteractionHand hand, SwingAnimation animation, boolean send, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Minecraft.getInstance().player && NoSwing.hideClient()) {
            cir.setReturnValue(false);
        }
    }
}
