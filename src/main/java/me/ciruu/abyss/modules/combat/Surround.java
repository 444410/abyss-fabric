package me.ciruu.abyss.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class Surround extends Module {
    public enum Center {
        None, Motion, Teleport
    }

    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Center> centerMode = new Setting<>("CenterMode", "Center mode", this, Center.Teleport);
    private final Setting<Integer> blocksPerTick = new Setting<>("BlocksPerTick", "Blocks per tick", this, 8, 1, 20);
    private final Setting<Boolean> toggleOffGround = new Setting<>("ToggleOffGround", "Turn off when you jump or fall", this, true);
    private final Setting<Boolean> keepEnabled = new Setting<>("KeepEnabled", "Keep enabled after finish", this, true);
    private final Setting<Boolean> antiFaceplace = new Setting<>("AntiFaceplace", "Also protect your head level", this, false);
    private final Setting<CombatUtil.SwitchMode> switchMode = new Setting<>("Switch", "", this, CombatUtil.SwitchMode.Silent);
    private double startY;

    public Surround() {
        super("Surround", "Place obsidian around you to prevent getting damage from nearby end crystals.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            return;
        }
        startY = mc.player.getY();
        Vec3 center = Vec3.atBottomCenterOf(mc.player.blockPosition());
        if (centerMode.is(Center.Teleport)) {
            mc.player.setPos(center.x, mc.player.getY(), center.z);
            mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
        } else if (centerMode.is(Center.Motion)) {
            mc.player.setDeltaMovement((center.x - mc.player.getX()) / 2, mc.player.getDeltaMovement().y, (center.z - mc.player.getZ()) / 2);
        }
    }

    public List<BlockPos> positions() {
        BlockPos feet = CombatUtil.feet(mc.player);
        List<BlockPos> list = new ArrayList<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = feet.relative(dir);
            if (BlockUtil.placeSide(side) == null) {
                list.add(side.below());
            }
            list.add(side);
        }
        if (antiFaceplace.on()) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                list.add(feet.above().relative(dir));
            }
        }
        if (BlockUtil.isReplaceable(feet.below())) {
            list.add(0, feet.below());
        }
        return list;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (toggleOffGround.on() && (Math.abs(mc.player.getY() - startY) > 0.5 || (!mc.player.onGround() && mc.player.getDeltaMovement().y > 0.1))) {
            setEnabled(false);
            return;
        }
        List<BlockPos> todo = positions();
        todo.removeIf(pos -> !BlockUtil.isReplaceable(pos));
        if (todo.isEmpty()) {
            if (!keepEnabled.on()) {
                setEnabled(false);
            }
            return;
        }
        CombatUtil.placeAll(todo, CombatUtil.obsidianSlot(), blocksPerTick.i(), rotate.on(), switchMode.getValue());
    });
}
