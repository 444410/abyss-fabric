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
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.MoverType;

public class Speed extends Module {
    public enum Mode {
        Strafe, OnGround, YPort
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "The mode of speed to use", this, Mode.Strafe);
    private final Setting<Boolean> useTimer = new Setting<>("UseTimer", "Uses timer to go faster", this, false);
    private final Setting<Boolean> autoSprint = new Setting<>("AutoSprint", "Automatically sprints for you", this, true);
    private final Setting<Boolean> speedInWater = new Setting<>("SpeedInWater", "Speeds in water", this, false);
    private final Setting<Boolean> autoJump = new Setting<>("AutoJump", "Automatically jumps", this, true).visible(() -> mode.is(Mode.Strafe));
    private final Setting<Boolean> strict = new Setting<>("Strict", "Caps speed for strict anticheats", this, false).visible(() -> mode.is(Mode.Strafe));
    private final Setting<Float> strictSpeed = new Setting<>("StrictSpeed", "", this, 0.257f, 0f, 0.272f).visible(() -> strict.on());
    private final Setting<Float> yPortSpeed = new Setting<>("YPortSpeed", "", this, 0.6f, 0.1f, 2f).visible(() -> mode.is(Mode.YPort));

    private int stage = 1;
    private double moveSpeed;
    private double lastDist;

    public Speed() {
        super("Speed", "Allows you go fast.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @Override
    protected void onEnable() {
        stage = 1;
        moveSpeed = MovementUtil.baseSpeed();
        lastDist = 0;
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        lastDist = MovementUtil.speed();
        if (useTimer.on() && MovementUtil.isMoving()) {
            TimerManager.set(this, 1.088f);
        } else {
            TimerManager.reset(this);
        }
        if (autoSprint.on() && MovementUtil.isMoving()) {
            mc.player.setSprinting(true);
        }
    });

    @EventHandler
    private final Listener<EventPlayerMove> move = new Listener<>(EventPlayerMove.class, event -> {
        if (event.type != MoverType.SELF || mc.player.isShiftKeyDown() || mc.player.getAbilities().flying || mc.player.isFallFlying()
                || ((mc.player.isInWater() || mc.player.isInLava()) && !speedInWater.on())) {
            return;
        }
        if (!MovementUtil.isMoving()) {
            event.x = 0;
            event.z = 0;
            return;
        }
        switch (mode.getValue()) {
            case Strafe -> strafe(event);
            case OnGround -> {
                if (mc.player.onGround()) {
                    double[] dir = MovementUtil.directionSpeed(MovementUtil.baseSpeed() * 1.4);
                    event.x = dir[0];
                    event.z = dir[1];
                }
            }
            case YPort -> {
                double[] dir = MovementUtil.directionSpeed(MovementUtil.baseSpeed() * (1 + yPortSpeed.f()));
                event.x = dir[0];
                event.z = dir[1];
                if (mc.player.onGround()) {
                    event.y = 0.4;
                    mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.4, mc.player.getDeltaMovement().z);
                } else if (mc.player.getDeltaMovement().y > 0) {
                    mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, -1, mc.player.getDeltaMovement().z);
                }
            }
        }
    });

    private void strafe(EventPlayerMove event) {
        double base = MovementUtil.baseSpeed();
        if (stage == 1 && mc.player.onGround() && (autoJump.on() || mc.options.keyJump.isDown())) {
            moveSpeed = 1.35 * base - 0.01;
        } else if (stage == 2 && mc.player.onGround() && (autoJump.on() || mc.options.keyJump.isDown())) {
            double jump = 0.3999;
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, jump, mc.player.getDeltaMovement().z);
            event.y = jump;
            moveSpeed *= 2.149;
        } else if (stage == 3) {
            moveSpeed = lastDist - 0.66 * (lastDist - base);
        } else {
            if (mc.player.onGround() || mc.player.verticalCollision) {
                stage = 1;
            }
            moveSpeed = lastDist - lastDist / 159.0;
        }
        moveSpeed = Math.max(moveSpeed, base);
        if (strict.on()) {
            moveSpeed = Math.min(moveSpeed, strictSpeed.f() * 2.3);
        }
        double[] dir = MovementUtil.directionSpeed(moveSpeed);
        event.x = dir[0];
        event.z = dir[1];
        stage++;
    }
}
