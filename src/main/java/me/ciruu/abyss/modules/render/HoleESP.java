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
import me.ciruu.abyss.util.HoleUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class HoleESP extends Module {
    public enum Mode {
        Full, Flat, Outline
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Full);
    private final Setting<Integer> range = new Setting<>("Range", "", this, 8, 1, 20);
    private final Setting<Float> height = new Setting<>("Height", "", this, 0.2f, 0f, 1f).visible(() -> mode.is(Mode.Full));
    private final Setting<Boolean> doubles = new Setting<>("Doubles", "Show 2x1 holes", this, true);
    private final Setting<Color> bedrock = new Setting<>("BedrockColor", "", this, new Color(0, 255, 0, 60));
    private final Setting<Color> obsidian = new Setting<>("ObsidianColor", "", this, new Color(255, 0, 0, 60));
    private final Setting<Color> doubleColor = new Setting<>("DoubleColor", "", this, new Color(255, 255, 0, 60)).visible(() -> doubles.on());
    private final Setting<Float> width = new Setting<>("LineWidth", "", this, 1.5f, 0.1f, 5f);

    private record Hole(AABB box, int color) {
    }

    private volatile List<Hole> holes = List.of();

    public HoleESP() {
        super("HoleESP", "Highlights safe holes.", Category.RENDER);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.tickCount % 4 != 0) {
            return;
        }
        List<Hole> found = new ArrayList<>();
        double h = mode.is(Mode.Flat) ? 0.002 : mode.is(Mode.Full) ? height.f() : 1.0;
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), range.i(), false)) {
            HoleUtil.Type type = HoleUtil.hole(pos);
            if (type != HoleUtil.Type.NONE) {
                found.add(new Hole(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + h, pos.getZ() + 1),
                        type == HoleUtil.Type.BEDROCK ? bedrock.argb() : obsidian.argb()));
            } else if (doubles.on()) {
                BlockPos other = HoleUtil.doubleHole(pos);
                if (other != null && (other.getX() > pos.getX() || other.getZ() > pos.getZ())) {
                    found.add(new Hole(new AABB(pos.getX(), pos.getY(), pos.getZ(), other.getX() + 1, pos.getY() + h, other.getZ() + 1), doubleColor.argb()));
                }
            }
        }
        holes = found;
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        for (Hole hole : holes) {
            int fill = mode.is(Mode.Outline) ? 0 : hole.color();
            RenderUtil.box(hole.box(), fill, ColorUtil.withAlpha(hole.color(), 255), width.f());
        }
    });
}
