package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class HighJump extends Module {
    public final Setting<Float> boost = new Setting<>("Boost", "Jump strength multiplier", this, 1.3f, 0.1f, 10f);
    private boolean wasOnGround;

    public HighJump() {
        super("HighJump", "Jump higher.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        var motion = mc.player.getDeltaMovement();
        if (wasOnGround && !mc.player.onGround() && motion.y > 0.3) {
            mc.player.setDeltaMovement(motion.x, motion.y * boost.f(), motion.z);
        }
        wasOnGround = mc.player.onGround();
    });
}
