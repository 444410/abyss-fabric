package me.ciruu.abyss.mixin;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventClientKeyDown;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class MixinKeyboardHandler {
    @Unique
    private boolean abyss$noScreen;

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void abyss$preKey(long handle, int action, KeyEvent event, CallbackInfo ci) {
        abyss$noScreen = Minecraft.getInstance().gui.screen() == null;
    }

    @Inject(method = "keyPress", at = @At("RETURN"))
    private void abyss$key(long handle, int action, KeyEvent event, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (handle != mc.getWindow().handle() || !abyss$noScreen || action != 1) {
            return;
        }
        Abyss.EVENT_BUS.post(new EventClientKeyDown(event.key()));
    }
}
