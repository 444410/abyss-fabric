package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Walks automatically.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> mc.options.keyUp.setDown(true));

    @Override
    protected void onDisable() {
        mc.options.keyUp.setDown(false);
    }
}
