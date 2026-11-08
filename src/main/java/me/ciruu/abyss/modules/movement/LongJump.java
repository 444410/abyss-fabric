package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerMove;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.managers.TimerManager;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class LongJump extends Module {
    private final Setting<Float> boost = new Setting<>("Boost", "", this, 4.48f, 1f, 20f);
    private final Setting<Float> timer = new Setting<>("Timer", "", this, 1f, 1f, 5f);
    private final Setting<Boolean> autoDisable = new Setting<>("AutoDisable", "Turn off after landing", this, true);
    private int stage;
    private double moveSpeed;
    private double lastDist;
    private boolean jumped;

    public LongJump() {
        super("LongJump", "Jump further.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        stage = 0;
        jumped = false;
        lastDist = 0;
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        lastDist = MovementUtil.speed();
        TimerManager.set(this, timer.f());
        if (jumped && mc.player.onGround() && autoDisable.on() && stage > 3) {
            setEnabled(false);
        }
    });

    @EventHandler
    private final Listener<EventPlayerMove> move = new Listener<>(EventPlayerMove.class, event -> {
        if (!MovementUtil.isMoving()) {
            return;
        }
        double base = MovementUtil.baseSpeed();
        if (stage == 0 && mc.player.onGround()) {
            moveSpeed = boost.f() * base;
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.424, mc.player.getDeltaMovement().z);
            event.y = 0.424;
            jumped = true;
            stage = 1;
        } else if (stage == 1) {
            moveSpeed = lastDist - 0.66 * (lastDist - base);
            stage = 2;
        } else {
            moveSpeed = lastDist - lastDist / 159.0;
            stage++;
        }
        moveSpeed = Math.max(moveSpeed, base);
        double[] dir = MovementUtil.directionSpeed(moveSpeed);
        event.x = dir[0];
        event.z = dir[1];
    });
}
