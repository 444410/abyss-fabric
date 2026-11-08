package me.ciruu.abyss.mixin;

import me.ciruu.abyss.modules.exploit.Jesus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class MixinLiquidBlock {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void abyss$jesus(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() == mc.player && Jesus.solid()
                && mc.player.getY() >= pos.getY() + 0.85) {
            cir.setReturnValue(Shapes.box(0, 0, 0, 1, 0.9, 1));
        }
    }
}
