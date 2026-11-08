package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class EntitySpeed extends Module {
    private final Setting<Float> speed = new Setting<>("Speed", "", this, 0.5f, 0f, 10f);

    public EntitySpeed() {
        super("EntitySpeed", "Increases the speed of rideable entities.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        Entity vehicle = mc.player.getVehicle();
        if (vehicle == null || vehicle instanceof AbstractBoat) {
            return;
        }
        vehicle.setYRot(mc.player.getYRot());
        double[] dir = MovementUtil.isMoving() ? MovementUtil.directionSpeed(speed.f()) : new double[]{0, 0};
        vehicle.setDeltaMovement(dir[0], vehicle.getDeltaMovement().y, dir[1]);
    });
}
