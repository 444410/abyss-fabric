package me.ciruu.abyss.modules.misc;

import java.util.concurrent.ThreadLocalRandom;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.PlayerUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.InteractionHand;

public class AntiAFK extends Module {
    private final Setting<Integer> interval = new Setting<>("Interval", "Seconds between actions", this, 10, 1, 120);
    private final Setting<Boolean> jump = new Setting<>("Jump", "", this, true);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, true);
    private final Setting<Boolean> swing = new Setting<>("Swing", "", this, true);
    private final Setting<Boolean> shift = new Setting<>("Shift", "", this, true);
    private int ticks;

    public AntiAFK() {
        super("AntiAFK", "Keeps you from being kicked for idling.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (shift.on()) {
            mc.options.keyShift.setDown(ticks % 40 < 5 && ticks > 0);
        }
        if (++ticks < interval.i() * 20) {
            return;
        }
        ticks = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (jump.on() && mc.player.onGround()) {
            mc.player.jumpFromGround();
        }
        if (rotate.on()) {
            mc.player.setYRot(mc.player.getYRot() + random.nextFloat(-90f, 90f));
        }
        if (swing.on()) {
            PlayerUtil.swing(InteractionHand.MAIN_HAND);
        }
    });

    @Override
    protected void onDisable() {
        mc.options.keyShift.setDown(false);
    }
}
