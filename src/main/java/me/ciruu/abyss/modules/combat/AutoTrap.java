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
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class AutoTrap extends Module {
    public enum Mode {
        Normal, Full, Feet
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Normal: head and top. Full: feet too. Feet: feet only", this, Mode.Normal);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Boolean> smartDisable = new Setting<>("SmartDisable", "Turn off once the target is trapped", this, true);
    private final Setting<Float> range = new Setting<>("Range", "", this, 5f, 0f, 6f);
    private final Setting<Integer> delay = new Setting<>("Delay", "Delay in ticks", this, 0, 0, 10);
    private final Setting<Integer> blocksPerTick = new Setting<>("BlocksPerTick", "Blocks per tick", this, 4, 1, 8);
    private final Setting<CombatUtil.SwitchMode> switchMode = new Setting<>("Switch", "", this, CombatUtil.SwitchMode.Silent);
    private int ticks;
    private Player target;

    public AutoTrap() {
        super("AutoTrap", "Trap nearby players in obsidian.", Category.COMBAT);
    }

    @Override
    public String getHudInfo() {
        return target == null ? null : ChatFormatting.WHITE + target.getGameProfile().name();
    }

    private List<BlockPos> positions(Player player) {
        BlockPos feet = CombatUtil.feet(player);
        List<BlockPos> list = new ArrayList<>();
        if (!mode.is(Mode.Normal)) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                list.add(feet.relative(dir));
            }
        }
        if (!mode.is(Mode.Feet)) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = feet.relative(dir);
                if (BlockUtil.placeSide(side.above()) == null) {
                    list.add(side.below());
                    list.add(side);
                }
                list.add(side.above());
            }
            BlockPos top = feet.above(2);
            if (BlockUtil.placeSide(top) == null) {
                list.add(feet.relative(Direction.NORTH).above(2));
            }
            list.add(top);
        }
        return list;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (++ticks <= delay.i()) {
            return;
        }
        ticks = 0;
        target = EntityUtil.closestTarget(range.f() + 2);
        if (target == null) {
            return;
        }
        List<BlockPos> todo = positions(target);
        todo.removeIf(pos -> !BlockUtil.isReplaceable(pos) || BlockUtil.distance(pos) > range.f());
        if (todo.isEmpty()) {
            if (smartDisable.on()) {
                setEnabled(false);
            }
            return;
        }
        CombatUtil.placeAll(todo, CombatUtil.obsidianSlot(), blocksPerTick.i(), rotate.on(), switchMode.getValue());
    });
}
