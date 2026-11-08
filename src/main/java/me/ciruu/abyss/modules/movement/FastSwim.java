package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class FastSwim extends Module {
    public enum Mode {
        Pulse, Constant
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Pulse);
    private final Setting<Float> factor = new Setting<>("Factor", "", this, 1.2f, 1f, 3f);

    public FastSwim() {
        super("FastSwim", "Swim faster.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (!(mc.player.isInWater() || mc.player.isInLava()) || !MovementUtil.isMoving()) {
            return;
        }
        if (mode.is(Mode.Pulse) && mc.player.tickCount % 3 != 0) {
            return;
        }
        var m = mc.player.getDeltaMovement();
        mc.player.setDeltaMovement(m.x * factor.f(), m.y, m.z * factor.f());
    });
}
