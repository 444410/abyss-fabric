package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockHighlight extends Module {
    private final Setting<Color> fill = new Setting<>("Fill", "", this, new Color(142, 0, 255, 40));
    private final Setting<Color> outline = new Setting<>("Outline", "", this, new Color(142, 0, 255, 255));
    private final Setting<Float> width = new Setting<>("LineWidth", "", this, 1.5f, 0.1f, 5f);

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block you are looking at.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = hit.getBlockPos();
        VoxelShape shape = mc.level.getBlockState(pos).getShape(mc.level, pos);
        AABB box = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
        RenderUtil.box(box, fill.argb(), outline.argb(), width.f());
    });
}
