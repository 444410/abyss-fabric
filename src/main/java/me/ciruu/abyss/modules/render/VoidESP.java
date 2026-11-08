package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class VoidESP extends Module {
    private final Setting<Integer> range = new Setting<>("Range", "", this, 8, 1, 32);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 0, 0, 60));
    private volatile List<BlockPos> holes = List.of();

    public VoidESP() {
        super("VoidESP", "Shows holes to the void.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.tickCount % 10 != 0) {
            return;
        }
        int minY = mc.level.getMinY();
        if (mc.player.getY() > minY + 40) {
            holes = List.of();
            return;
        }
        List<BlockPos> found = new ArrayList<>();
        BlockPos center = mc.player.blockPosition();
        for (int x = -range.i(); x <= range.i(); x++) {
            for (int z = -range.i(); z <= range.i(); z++) {
                BlockPos pos = new BlockPos(center.getX() + x, minY, center.getZ() + z);
                if (mc.level.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                    found.add(pos);
                }
            }
        }
        holes = found;
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (BlockPos pos : holes) {
            RenderUtil.flat(pos, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
        }
    });
}
