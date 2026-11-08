package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.managers.TimerManager;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.level.block.Blocks;

public class WebBypass extends Module {
    public enum Mode {
        Motion, Timer
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Motion);
    private final Setting<Boolean> sneakEnable = new Setting<>("SneakEnable", "Only while sneaking", this, false);
    private final Setting<Float> motionY = new Setting<>("MotionY", "", this, 10f, 0f, 20f).visible(() -> mode.is(Mode.Motion));
    private final Setting<Float> timer = new Setting<>("Timer", "", this, 20f, 1f, 100f).visible(() -> mode.is(Mode.Timer));

    public WebBypass() {
        super("WebBypass", "Prevents slowing while in contact to a web.", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        boolean inWeb = BlockUtil.block(mc.player.blockPosition()) == Blocks.COBWEB || BlockUtil.block(mc.player.blockPosition().above()) == Blocks.COBWEB;
        if (!inWeb || (sneakEnable.on() && !mc.options.keyShift.isDown()) || mc.player.onGround()) {
            TimerManager.reset(this);
            return;
        }
        if (mode.is(Mode.Motion)) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, -motionY.f() / 10.0, mc.player.getDeltaMovement().z);
        } else {
            TimerManager.set(this, timer.f() / 4f);
        }
    });
}
