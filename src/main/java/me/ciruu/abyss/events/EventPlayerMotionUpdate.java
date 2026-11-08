package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;
import me.zero.alpine.event.EventStage;

public final class EventPlayerMotionUpdate extends Cancellable {
    private final EventStage stage;
    public float yaw;
    public float pitch;
    public boolean onGround;
    private boolean rotated;

    public EventPlayerMotionUpdate(EventStage stage, float yaw, float pitch, boolean onGround) {
        this.stage = stage;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public EventStage getStage() {
        return stage;
    }

    public boolean isPre() {
        return stage == EventStage.PRE;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotated = true;
    }

    public boolean isRotated() {
        return rotated;
    }
}
