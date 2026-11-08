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
import net.minecraft.world.phys.AABB;

public class Surround2 extends Module {
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, false);
    private final Setting<Integer> blocksPerTick = new Setting<>("BlocksPerTick", "", this, 8, 1, 20);
    private final Setting<CombatUtil.SwitchMode> switchMode = new Setting<>("Switch", "", this, CombatUtil.SwitchMode.Silent);

    public Surround2() {
        super("Surround2", "Surround for every block you are standing in.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (!mc.player.onGround()) {
            return;
        }
        AABB box = mc.player.getBoundingBox();
        List<BlockPos> occupied = new ArrayList<>();
        int y = (int) Math.floor(mc.player.getY() + 0.2);
        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX - 1e-4); x++) {
            for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ - 1e-4); z++) {
                occupied.add(new BlockPos(x, y, z));
            }
        }
        List<BlockPos> todo = new ArrayList<>();
        for (BlockPos pos : occupied) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = pos.relative(dir);
                if (!occupied.contains(side) && BlockUtil.isReplaceable(side)) {
                    if (BlockUtil.placeSide(side) == null) {
                        todo.add(side.below());
                    }
                    todo.add(side);
                }
            }
        }
        CombatUtil.placeAll(todo, CombatUtil.obsidianSlot(), blocksPerTick.i(), rotate.on(), switchMode.getValue());
    });
}
