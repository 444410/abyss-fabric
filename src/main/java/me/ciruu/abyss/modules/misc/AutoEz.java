package me.ciruu.abyss.modules.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.events.EventPlayerDeath;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class AutoEz extends Module {
    private final Setting<String> message = new Setting<>("Message", "Message", this, "GG Abyss on top!");
    private final Setting<Boolean> playerName = new Setting<>("PlayerName", "Add the player's name", this, false);
    private final Setting<Boolean> randomNumber = new Setting<>("RandomNumber", "Add a random number (anti spam)", this, false);
    private final Map<String, Long> attacked = new HashMap<>();

    public AutoEz() {
        super("AutoEz", "Sends a custom message if you kill a player.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packets = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (event.getPacket() instanceof ServerboundAttackPacket packet && mc.level != null) {
            Entity entity = mc.level.getEntity(packet.entityId());
            if (entity instanceof Player player) {
                attacked.put(player.getGameProfile().name(), System.currentTimeMillis());
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        for (Player player : mc.level.players()) {
            if (player != mc.player && mc.player.distanceTo(player) < 10 && player.hurtTime > 0) {
                attacked.putIfAbsent(player.getGameProfile().name(), System.currentTimeMillis());
            }
        }
        attacked.values().removeIf(time -> System.currentTimeMillis() - time > 15000);
    });

    @EventHandler
    private final Listener<EventPlayerDeath> death = new Listener<>(EventPlayerDeath.class, event -> {
        String name = event.player().getGameProfile().name();
        if (event.player() == mc.player || attacked.remove(name) == null) {
            return;
        }
        String text = message.getValue();
        if (playerName.on()) {
            text = name + " " + text;
        }
        if (randomNumber.on()) {
            text += " " + ThreadLocalRandom.current().nextInt(1000);
        }
        ChatUtil.sendServer(text);
    });
}
