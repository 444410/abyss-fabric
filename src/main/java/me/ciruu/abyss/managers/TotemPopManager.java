package me.ciruu.abyss.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventPlayerDeath;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventTotemPop;
import me.ciruu.abyss.events.EventWorldChange;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class TotemPopManager implements Listenable, Globals {
    private final Map<String, Integer> pops = new ConcurrentHashMap<>();

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (!(event.getPacket() instanceof ClientboundEntityEventPacket packet) || packet.getEventId() != 35) {
            return;
        }
        mc.execute(() -> {
            if (mc.level == null) {
                return;
            }
            Entity entity = packet.getEntity(mc.level);
            if (entity instanceof Player player) {
                int count = pops.merge(player.getGameProfile().name(), 1, Integer::sum);
                Abyss.EVENT_BUS.post(new EventTotemPop(player, count));
            }
        });
    });

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        for (Player player : mc.level.players()) {
            if ((player.isDeadOrDying() || player.getHealth() <= 0) && pops.containsKey(player.getGameProfile().name())) {
                Abyss.EVENT_BUS.post(new EventPlayerDeath(player));
                pops.remove(player.getGameProfile().name());
            }
        }
    });

    @EventHandler
    private final Listener<EventWorldChange> world = new Listener<>(EventWorldChange.class, event -> pops.clear());

    public int getPops(String name) {
        return pops.getOrDefault(name, 0);
    }

    public void reset(String name) {
        pops.remove(name);
    }
}
