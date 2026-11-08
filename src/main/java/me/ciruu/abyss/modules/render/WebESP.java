package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class WebESP extends Module {
    private final Setting<Integer> range = new Setting<>("Range", "", this, 8, 1, 20);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 255, 255, 40));
    private volatile List<BlockPos> webs = List.of();

    public WebESP() {
        super("WebESP", "Highlights cobwebs.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.tickCount % 5 != 0) {
            return;
        }
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), range.i(), false)) {
            if (BlockUtil.block(pos) == Blocks.COBWEB) {
                found.add(pos);
            }
        }
        webs = found;
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (BlockPos pos : webs) {
            RenderUtil.block(pos, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1f);
        }
    });
}
