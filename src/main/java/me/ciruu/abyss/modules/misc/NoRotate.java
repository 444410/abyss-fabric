package me.ciruu.abyss.modules.misc;

import java.util.EnumSet;
import java.util.Set;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", "Prevents server-side rotations. This may desync you from the server.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientboundPlayerPositionPacket packet) {
            Set<Relative> relatives = EnumSet.noneOf(Relative.class);
            relatives.addAll(packet.relatives());
            relatives.add(Relative.X_ROT);
            relatives.add(Relative.Y_ROT);
            PositionMoveRotation change = packet.change();
            event.setPacket(new ClientboundPlayerPositionPacket(packet.id(), new PositionMoveRotation(change.position(), change.deltaMovement(), 0f, 0f), relatives));
        }
    });
}
