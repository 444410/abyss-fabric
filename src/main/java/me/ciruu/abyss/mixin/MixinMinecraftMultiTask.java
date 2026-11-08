package me.ciruu.abyss.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.ciruu.abyss.modules.combat.MultiTask;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftMultiTask {
    @ModifyExpressionValue(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean abyss$multiTaskMine(boolean original) {
        return original && !MultiTask.active();
    }

    @ModifyExpressionValue(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private boolean abyss$multiTaskAttack(boolean original) {
        return original && !MultiTask.active();
    }
}
