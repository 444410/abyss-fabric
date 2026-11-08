package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventClientTick;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.screens.DeathScreen;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns", Category.MISC);
    }

    @EventHandler
    private final Listener<EventClientTick> tick = new Listener<>(EventClientTick.class, event -> {
        if (mc.player != null && mc.gui.screen() instanceof DeathScreen) {
            mc.player.respawn();
            mc.gui.setScreen(null);
        }
    });
}
