package me.ciruu.abyss.modules.render;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.events.EventPlayerPushOutOfBlocks;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventWorldChange;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Freecam extends Module {
    private final Setting<Float> speed = new Setting<>("Speed", "", this, 1f, 0.1f, 5f);
    private RemotePlayer body;
    private Vec3 start;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", "Fly your camera around freely.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            setEnabled(false);
            return;
        }
        start = mc.player.position();
        yaw = mc.player.getYRot();
        pitch = mc.player.getXRot();
        body = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), mc.player.getGameProfile().name()));
        body.copyPosition(mc.player);
        body.yHeadRot = mc.player.yHeadRot;
        body.getInventory().replaceWith(mc.player.getInventory());
        body.setId(-101);
        mc.level.addEntity(body);
    }

    @Override
    protected void onDisable() {
        if (body != null && mc.level != null) {
            mc.level.removeEntity(body.getId(), Entity.RemovalReason.DISCARDED);
        }
        body = null;
        if (mc.player != null && start != null) {
            mc.player.noPhysics = false;
            mc.player.setPos(start.x, start.y, start.z);
            mc.player.setYRot(yaw);
            mc.player.setXRot(pitch);
            mc.player.setDeltaMovement(Vec3.ZERO);
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        mc.player.noPhysics = true;
        mc.player.setOnGround(false);
        mc.player.fallDistance = 0;
        double y = mc.options.keyJump.isDown() ? speed.f() : mc.options.keyShift.isDown() ? -speed.f() : 0;
        double[] dir = MovementUtil.isMoving() ? MovementUtil.directionSpeed(speed.f()) : new double[]{0, 0};
        mc.player.setDeltaMovement(dir[0], y, dir[1]);
    });

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packets = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (event.getPacket() instanceof ServerboundMovePlayerPacket || event.getPacket() instanceof ServerboundPlayerInputPacket) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerPushOutOfBlocks> push = new Listener<>(EventPlayerPushOutOfBlocks.class, e -> e.cancel());

    @EventHandler
    private final Listener<EventWorldChange> world = new Listener<>(EventWorldChange.class, e -> {
        body = null;
        start = null;
        setEnabled(false);
    });
}
