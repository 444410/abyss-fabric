package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventEntityPush;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void abyss$push(Entity other, CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player && Abyss.EVENT_BUS.post(new EventEntityPush()).isCancelled()) {
            ci.cancel();
        }
    }
}
