package me.ciruu.abyss.mixin.accessor;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {
    @Accessor("destroyProgress")
    float abyss$getDestroyProgress();

    @Accessor("destroyProgress")
    void abyss$setDestroyProgress(float progress);

    @Accessor("destroyBlockPos")
    BlockPos abyss$getDestroyPos();

    @Accessor("destroyDelay")
    void abyss$setDestroyDelay(int delay);
}
