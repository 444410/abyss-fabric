package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.PacketUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class AntiVoid extends Module {
    private final Setting<Boolean> onGround = new Setting<>("Onground", "", this, true);
    private final Setting<Boolean> packet = new Setting<>("Packet", "", this, true);
    private final Setting<Boolean> pos = new Setting<>("Pos", "", this, true);

    public AntiVoid() {
        super("AntiVoid", "Prevents to fall into the void.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.getY() >= mc.level.getMinY()) {
            return;
        }
        if (packet.on()) {
            PacketUtil.send(new ServerboundMovePlayerPacket.Pos(mc.player.xo, mc.player.yo, mc.player.zo, onGround.on(), false));
        }
        if (pos.on()) {
            mc.player.setPos(mc.player.xo, mc.player.yo, mc.player.zo);
            mc.player.setDeltaMovement(0, 0, 0);
        }
    });
}
