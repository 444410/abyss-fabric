package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.managers.TimerManager;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;

public class Timer extends Module {
    public enum Mode {
        NORMAL, SWITCH
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "SWITCH alternates fast and normal speed", this, Mode.NORMAL);
    private final Setting<Float> speed = new Setting<>("Speed", "", this, 4f, 0.1f, 20f).visible(() -> mode.is(Mode.NORMAL));
    private final Setting<Float> fast = new Setting<>("Fast", "", this, 10f, 0.1f, 100f).visible(() -> mode.is(Mode.SWITCH));
    private final Setting<Integer> fastTime = new Setting<>("FastTime", "Ticks", this, 20, 1, 500).visible(() -> mode.is(Mode.SWITCH));
    private final Setting<Integer> slowTime = new Setting<>("SlowTime", "Ticks", this, 20, 1, 500).visible(() -> mode.is(Mode.SWITCH));
    private final Setting<Boolean> autoDisable = new Setting<>("AutoDisable", "", this, false);
    private final Setting<Integer> timer = new Setting<>("Timer(ms)", "Turn off after this long", this, 250, 1, 2500).visible(() -> autoDisable.on());
    private int ticks;
    private long enabledAt;

    public Timer() {
        super("Timer", "Speed up the game.", Category.MISC);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + String.format("%.1f", TimerManager.get());
    }

    @Override
    protected void onEnable() {
        ticks = 0;
        enabledAt = System.currentTimeMillis();
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (autoDisable.on() && System.currentTimeMillis() - enabledAt > timer.i()) {
            setEnabled(false);
            return;
        }
        if (mode.is(Mode.NORMAL)) {
            TimerManager.set(this, speed.f());
            return;
        }
        ticks = (ticks + 1) % (fastTime.i() + slowTime.i());
        TimerManager.set(this, ticks < fastTime.i() ? fast.f() : 1f);
    });
}
