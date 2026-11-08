package me.ciruu.abyss.mixin.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("rightClickDelay")
    int abyss$getRightClickDelay();

    @Accessor("rightClickDelay")
    void abyss$setRightClickDelay(int delay);
}
