package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.render.TabFriends;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public abstract class MixinPlayerTabOverlay {
    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void abyss$friends(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        if (TabFriends.active() && Abyss.friendManager.isFriend(info.getProfile().name())) {
            cir.setReturnValue(cir.getReturnValue().copy().withStyle(ChatFormatting.AQUA));
        }
    }
}
