package me.ciruu.abyss.managers;

import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventWorldChange;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

public class TpsManager implements Listenable {
    private static final float[] samples = new float[20];
    private static int index;
    private static int count;
    private static long last = -1;

    public static float tps() {
        if (count == 0) {
            return 20f;
        }
        float sum = 0;
        for (int i = 0; i < count; i++) {
            sum += samples[i];
        }
        return Math.min(20f, sum / count);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientboundSetTimePacket) {
            long now = System.currentTimeMillis();
            if (last != -1) {
                float seconds = (now - last) / 1000f;
                samples[index] = Math.max(0f, Math.min(20f, 20f / Math.max(seconds, 0.05f)));
                index = (index + 1) % samples.length;
                count = Math.min(count + 1, samples.length);
            }
            last = now;
        }
    });

    @EventHandler
    private final Listener<EventWorldChange> world = new Listener<>(EventWorldChange.class, event -> {
        last = -1;
        count = 0;
    });
}
