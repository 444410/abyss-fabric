package me.ciruu.abyss.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.HoleUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SelfTrap extends Module {
    private final Setting<Boolean> toggle = new Setting<>("Toggle", "Toggles the features after being used", this, true);
    private final Setting<Boolean> checkHole = new Setting<>("CheckHole", "Only while in a hole", this, true);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, false);

    public SelfTrap() {
        super("SelfTrap", "Traps the player by placing obsidian over your head.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (checkHole.on() && !HoleUtil.isInHole(mc.player)) {
            return;
        }
        BlockPos head = CombatUtil.feet(mc.player).above();
        List<BlockPos> todo = new ArrayList<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            todo.add(head.relative(dir));
        }
        todo.add(head.above());
        todo.removeIf(pos -> !BlockUtil.isReplaceable(pos));
        if (todo.isEmpty()) {
            if (toggle.on()) {
                setEnabled(false);
            }
            return;
        }
        CombatUtil.placeAll(todo, CombatUtil.obsidianSlot(), 4, rotate.on(), CombatUtil.SwitchMode.Silent);
    });
}
