package me.ciruu.abyss.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class AutoWeb extends Module {
    private final Setting<Float> placeRange = new Setting<>("PlaceRange", "Place Range", this, 5f, 0f, 6f);
    private final Setting<Integer> placeTicks = new Setting<>("PlaceTicks", "Place Delay", this, 0, 0, 10);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Boolean> feet = new Setting<>("Feet", "", this, true);
    private final Setting<Boolean> head = new Setting<>("Head", "", this, false);
    private final Setting<Boolean> acTarget = new Setting<>("ACTarget", "Use AutoCrystal's target", this, true);
    private final Setting<CombatUtil.SwitchMode> switchMode = new Setting<>("Switch", "", this, CombatUtil.SwitchMode.Silent);
    private int ticks;

    public AutoWeb() {
        super("AutoWeb", "Automatically place cobwebs to nearby players.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (++ticks <= placeTicks.i()) {
            return;
        }
        ticks = 0;
        Player target = acTarget.on() && AutoCrystal.isActive() ? Abyss.moduleManager.get(AutoCrystal.class).getTarget() : null;
        if (target == null) {
            target = EntityUtil.closestTarget(placeRange.f() + 1);
        }
        if (target == null) {
            return;
        }
        BlockPos pos = CombatUtil.feet(target);
        List<BlockPos> todo = new ArrayList<>();
        if (feet.on()) {
            todo.add(pos);
        }
        if (head.on()) {
            todo.add(pos.above());
        }
        todo.removeIf(p -> BlockUtil.distance(p) > placeRange.f() || !BlockUtil.isReplaceable(p));
        CombatUtil.placeAll(todo, CombatUtil.blockSlot(Blocks.COBWEB), 2, rotate.on(), switchMode.getValue());
    });
}
