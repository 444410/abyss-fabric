package me.ciruu.abyss.modules.misc;

import java.util.Comparator;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AimBot extends Module {
    public enum Mode {
        Closest, Health, Angle
    }

    public enum Point {
        Head, Body, Feet
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "How the target is chosen", this, Mode.Closest);
    private final Setting<Point> point = new Setting<>("Point", "", this, Point.Head);
    private final Setting<Float> range = new Setting<>("Range", "", this, 64f, 1f, 350f);
    private final Setting<Boolean> invisibles = new Setting<>("Invisibles", "", this, false);
    private final Setting<Boolean> rayTrace = new Setting<>("RayTrace", "Only visible targets", this, true);
    private final Setting<Boolean> packet = new Setting<>("Packet", "Rotate server side only", this, false);
    private final Setting<Boolean> players = new Setting<>("Players", "", this, true);
    private final Setting<Boolean> mobs = new Setting<>("Mobs", "", this, false);
    private final Setting<Boolean> animals = new Setting<>("Animals", "", this, false);

    public AimBot() {
        super("AimBot", "Automatically look at entities.", Category.MISC);
    }

    private boolean valid(Entity entity) {
        if (!(entity instanceof LivingEntity living) || entity == mc.player || !living.isAlive() || mc.player.distanceTo(entity) > range.f()) {
            return false;
        }
        if (entity.isInvisible() && !invisibles.on()) {
            return false;
        }
        if (rayTrace.on() && !mc.player.hasLineOfSight(entity)) {
            return false;
        }
        if (entity instanceof Player p) {
            return players.on() && !Abyss.friendManager.isFriend(p) && !p.isSpectator();
        }
        return (mobs.on() && EntityUtil.isHostile(entity)) || (animals.on() && EntityUtil.isPassive(entity));
    }

    private Vec3 aimPoint(Entity entity) {
        return switch (point.getValue()) {
            case Head -> entity.getEyePosition();
            case Body -> entity.position().add(0, entity.getBbHeight() / 2, 0);
            case Feet -> entity.position();
        };
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        Comparator<Entity> order = switch (mode.getValue()) {
            case Closest -> Comparator.comparingDouble(mc.player::distanceTo);
            case Health -> Comparator.comparingDouble(e -> ((LivingEntity) e).getHealth());
            case Angle -> Comparator.comparingDouble(e -> {
                float[] r = RotationUtil.rotations(aimPoint(e));
                return Math.abs(net.minecraft.util.Mth.wrapDegrees(r[0] - mc.player.getYRot())) + Math.abs(r[1] - mc.player.getXRot());
            });
        };
        Entity target = null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (valid(entity) && (target == null || order.compare(entity, target) < 0)) {
                target = entity;
            }
        }
        if (target == null) {
            return;
        }
        float[] rot = RotationUtil.rotations(aimPoint(target));
        if (packet.on()) {
            RotationUtil.set(rot[0], rot[1]);
        } else {
            mc.player.setYRot(rot[0]);
            mc.player.setXRot(rot[1]);
        }
    });
}
