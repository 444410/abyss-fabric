package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.managers.TimerManager;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;

public class BoatFly extends Module {
    private final Setting<Float> speed = new Setting<>("Speed", "", this, 2f, 0f, 40f);
    private final Setting<Float> ySpeed = new Setting<>("YSpeed", "", this, 1f, 0f, 10f);
    private final Setting<Float> glideSpeed = new Setting<>("GlideSpeed", "", this, 0.04f, 0f, 10f);
    private final Setting<Float> timer = new Setting<>("Timer", "", this, 1f, 0.1f, 5f);
    private final Setting<Boolean> cancelRotations = new Setting<>("CancelRotations", "Drop rotation packets while riding", this, true);
    private final Setting<Boolean> yLimit = new Setting<>("yLimit", "", this, false);
    private final Setting<Float> height = new Setting<>("Height", "", this, 127f, -64f, 320f).visible(() -> yLimit.on());

    public BoatFly() {
        super("BoatFly", "Allows you to fly with a boat.", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        Entity vehicle = mc.player.getVehicle();
        if (!(vehicle instanceof AbstractBoat)) {
            TimerManager.reset(this);
            return;
        }
        TimerManager.set(this, timer.f());
        vehicle.setYRot(mc.player.getYRot());
        double y = -glideSpeed.f();
        if (mc.options.keyJump.isDown() && (!yLimit.on() || vehicle.getY() < height.f())) {
            y = ySpeed.f();
        } else if (mc.options.keySprint.isDown()) {
            y = -ySpeed.f();
        }
        double[] dir = MovementUtil.isMoving() ? MovementUtil.directionSpeed(speed.f()) : new double[]{0, 0};
        vehicle.setDeltaMovement(new Vec3(dir[0], y, dir[1]));
    });

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packets = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (cancelRotations.on() && mc.player != null && mc.player.getVehicle() instanceof AbstractBoat
                && (event.getPacket() instanceof ServerboundMovePlayerPacket.Rot || event.getPacket() instanceof ServerboundPlayerInputPacket)) {
            event.cancel();
        }
    });
}
