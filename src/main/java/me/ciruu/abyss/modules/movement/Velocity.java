package me.ciruu.abyss.modules.movement;

import java.util.Optional;
import me.ciruu.abyss.events.EventEntityPush;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventPlayerPushOutOfBlocks;
import me.ciruu.abyss.events.EventPlayerPushedByWater;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public class Velocity extends Module {
    public final Setting<Integer> horizontal = new Setting<>("Horizontal", "The horizontal velocity you will take.", this, 0, 0, 100);
    public final Setting<Integer> vertical = new Setting<>("Vertical", "The vertical velocity you will take.", this, 0, 0, 100);
    public final Setting<Boolean> explosions = new Setting<>("Explosions", "Apply velocity modifier on explosion velocity.", this, true);
    public final Setting<Boolean> bobbers = new Setting<>("Bobbers", "Apply velocity modifier on fishing bobber velocity.", this, true);
    public final Setting<Boolean> noPush = new Setting<>("NoPush", "Disable collision with entities, blocks and water", this, true);

    public Velocity() {
        super("Velocity", "Modify the velocity you take.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + String.format("H:%s%%|V:%s%%", horizontal.getValue(), vertical.getValue());
    }

    private Vec3 scale(Vec3 motion) {
        return new Vec3(motion.x * horizontal.i() / 100.0, motion.y * vertical.i() / 100.0, motion.z * horizontal.i() / 100.0);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof ClientboundSetEntityMotionPacket packet && packet.id() == mc.player.getId()) {
            if (horizontal.i() == 0 && vertical.i() == 0) {
                event.cancel();
            } else {
                event.setPacket(new ClientboundSetEntityMotionPacket(packet.id(), scale(packet.movement())));
            }
        } else if (event.getPacket() instanceof ClientboundExplodePacket packet && explosions.on() && packet.playerKnockback().isPresent()) {
            event.setPacket(new ClientboundExplodePacket(packet.center(), packet.radius(), packet.blockCount(), Optional.of(scale(packet.playerKnockback().get())),
                    packet.explosionParticle(), packet.explosionSound(), packet.blockParticles(), packet.playSound()));
        } else if (event.getPacket() instanceof ClientboundEntityEventPacket packet && bobbers.on() && packet.getEventId() == 31 && mc.level != null
                && packet.getEntity(mc.level) instanceof FishingHook hook && hook.getHookedIn() == mc.player) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerPushOutOfBlocks> blocks = new Listener<>(EventPlayerPushOutOfBlocks.class, event -> {
        if (noPush.on()) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerPushedByWater> water = new Listener<>(EventPlayerPushedByWater.class, event -> {
        if (noPush.on()) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventEntityPush> entities = new Listener<>(EventEntityPush.class, event -> {
        if (noPush.on()) {
            event.cancel();
        }
    });
}
