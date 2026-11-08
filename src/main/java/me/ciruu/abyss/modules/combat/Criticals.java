package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.PacketUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class Criticals extends Module {
    public enum Mode {
        Packet, Jump, MiniJump
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Packet);
    private final Setting<Integer> packets = new Setting<>("Packets", "", this, 2, 1, 5).visible(() -> mode.is(Mode.Packet));
    private final Setting<Float> offsetY = new Setting<>("OffsetY", "", this, 0.1f, 0f, 1f).visible(() -> mode.is(Mode.MiniJump));

    public Criticals() {
        super("Criticals", "Deal a critical hit on ground.", Category.COMBAT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packetsListener = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (!(event.getPacket() instanceof ServerboundAttackPacket attack) || !mc.player.onGround() || mc.player.isInWater() || mc.player.isInLava()) {
            return;
        }
        Entity target = mc.level.getEntity(attack.entityId());
        if (!(target instanceof LivingEntity) || target instanceof EndCrystal) {
            return;
        }
        double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
        switch (mode.getValue()) {
            case Packet -> {
                double[] offsets = {0.0625, 0.0, 0.0433, 0.0, 0.0125};
                for (int i = 0; i < Math.min(packets.i() * 2, offsets.length); i++) {
                    PacketUtil.sendSilent(new ServerboundMovePlayerPacket.Pos(x, y + offsets[i], z, false, mc.player.horizontalCollision));
                }
            }
            case Jump -> mc.player.jumpFromGround();
            case MiniJump -> {
                var m = mc.player.getDeltaMovement();
                mc.player.setDeltaMovement(m.x, offsetY.f(), m.z);
            }
        }
    });
}
