package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventChatSend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen {
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void abyss$command(String msg, boolean addToRecent, CallbackInfo ci) {
        String trimmed = msg.trim();
        if (Abyss.commandManager != null && trimmed.startsWith(Abyss.commandManager.getPrefix()) && !trimmed.isEmpty()) {
            if (addToRecent) {
                Minecraft.getInstance().gui.hud.getChat().addRecentChat(trimmed);
            }
            Abyss.commandManager.execute(trimmed.substring(Abyss.commandManager.getPrefix().length()));
            ci.cancel();
        }
    }

    @ModifyVariable(method = "handleChatInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;sendChat(Ljava/lang/String;)V"), argsOnly = true)
    private String abyss$suffix(String msg) {
        return Abyss.EVENT_BUS.post(new EventChatSend(msg)).message;
    }
}
