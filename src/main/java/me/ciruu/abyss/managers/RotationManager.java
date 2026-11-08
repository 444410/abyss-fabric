package me.ciruu.abyss.managers;

import me.ciruu.abyss.events.EventPlayerMotionUpdate;
import me.ciruu.abyss.util.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

public class RotationManager implements Listenable {
    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motion = new Listener<>(EventPlayerMotionUpdate.class, event -> {
        if (!event.isPre() || event.isRotated()) {
            return;
        }
        float[] rot = RotationUtil.take();
        if (rot != null) {
            event.setRotation(rot[0], rot[1]);
        }
    }, -100);
}
