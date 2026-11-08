package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventMouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void abyss$button(long handle, MouseButtonInfo info, int action, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (action == 1 && mc.gui.screen() == null && mc.player != null) {
            Abyss.EVENT_BUS.post(new EventMouseButton(info.button()));
        }
    }
}
