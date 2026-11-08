package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class YawLock extends Module {
    private final Setting<Boolean> snap = new Setting<>("Snap", "Snap to the nearest 45 degrees instead of a fixed yaw", this, true);
    private final Setting<Integer> yaw = new Setting<>("Yaw", "", this, 0, 0, 360).visible(() -> !snap.on());

    public YawLock() {
        super("YawLock", "Locks yaw in a certain direction.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        mc.player.setYRot(snap.on() ? Math.round(mc.player.getYRot() / 45f) * 45f : yaw.i());
    });
}
