package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FallingBlock;

public class Scaffold extends Module {
    public enum Mode {
        Tower, Normal
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Tower jumps you up while holding space", this, Mode.Tower);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, true);
    private final Setting<Boolean> stopMotion = new Setting<>("StopMotion", "Stop sideways motion while towering", this, true);
    private final Setting<Integer> delay = new Setting<>("Delay(ms)", "", this, 0, 0, 1000);
    private final Timer timer = new Timer();

    public Scaffold() {
        super("Scaffold", "Places blocks on your feet around you.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        BlockPos below = BlockPos.containing(mc.player.getX(), mc.player.getY() - 1, mc.player.getZ());
        if (!BlockUtil.isReplaceable(below) || !timer.passed(delay.i())) {
            return;
        }
        int slot = InventoryUtil.findHotbar(stack -> stack.getItem() instanceof BlockItem bi && !(bi.getBlock() instanceof FallingBlock));
        if (slot == -1) {
            return;
        }
        int old = InventoryUtil.selected();
        InventoryUtil.switchTo(slot);
        if (BlockUtil.place(below, InteractionHand.MAIN_HAND, rotate.on(), true)) {
            timer.reset();
            if (mode.is(Mode.Tower) && mc.options.keyJump.isDown()) {
                var m = mc.player.getDeltaMovement();
                mc.player.setDeltaMovement(stopMotion.on() ? 0 : m.x, 0.42, stopMotion.on() ? 0 : m.z);
            }
        }
        InventoryUtil.switchTo(old);
    });
}
