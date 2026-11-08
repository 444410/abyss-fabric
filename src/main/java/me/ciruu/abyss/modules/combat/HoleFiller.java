package me.ciruu.abyss.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.HoleUtil;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class HoleFiller extends Module {
    public enum Mode {
        Smart, All
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Smart fills holes near targets only", this, Mode.Smart);
    private final Setting<Float> placeRange = new Setting<>("Place Range", "Place Range", this, 5f, 0f, 6f);
    private final Setting<Float> targetRange = new Setting<>("TargetRange", "How close to a target a hole must be", this, 3f, 1f, 6f).visible(() -> mode.is(Mode.Smart));
    private final Setting<Integer> placeDelay = new Setting<>("Place Delay", "Place Delay", this, 0, 0, 250);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Integer> blocksPerTick = new Setting<>("BlocksPerTick", "Blocks per tick", this, 8, 1, 20);
    private final Setting<Boolean> disable = new Setting<>("Disable", "Turn off after DisableTime ticks", this, true);
    private final Setting<Integer> disableTime = new Setting<>("DisableTime", "Disable time", this, 100, 1, 500).visible(() -> disable.on());
    private final Setting<Boolean> doubles = new Setting<>("Doubles", "Fill 2x1 holes too", this, true);
    private final Timer timer = new Timer();
    private int ticks;

    public HoleFiller() {
        super("HoleFiller", "Fills nearby holes with blocks.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (disable.on() && ++ticks > disableTime.i()) {
            setEnabled(false);
            return;
        }
        if (!timer.passed(placeDelay.i())) {
            return;
        }
        List<Player> targets = EntityUtil.targets(placeRange.f() + targetRange.f());
        BlockPos self = CombatUtil.feet(mc.player);
        List<BlockPos> holes = new ArrayList<>();
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), placeRange.f(), false)) {
            if (pos.equals(self) || BlockUtil.distance(pos) > placeRange.f()) {
                continue;
            }
            boolean hole = HoleUtil.hole(pos) != HoleUtil.Type.NONE;
            BlockPos other = !hole && doubles.on() ? HoleUtil.doubleHole(pos) : null;
            if (!hole && other == null) {
                continue;
            }
            if (mode.is(Mode.Smart)) {
                boolean near = false;
                for (Player p : targets) {
                    if (p.position().distanceTo(net.minecraft.world.phys.Vec3.atBottomCenterOf(pos)) <= targetRange.f() && !CombatUtil.feet(p).equals(pos)) {
                        near = true;
                        break;
                    }
                }
                if (!near) {
                    continue;
                }
            }
            holes.add(pos);
        }
        if (CombatUtil.placeAll(holes, CombatUtil.obsidianSlot(), blocksPerTick.i(), rotate.on(), CombatUtil.SwitchMode.Silent) > 0) {
            timer.reset();
        }
    });
}
