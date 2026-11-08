package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec2;

public final class MovementUtil implements Globals {
    private MovementUtil() {
    }

    public static boolean isMoving() {
        Vec2 move = mc.player.input.getMoveVector();
        return move.x != 0 || move.y != 0;
    }

    public static double baseSpeed() {
        double speed = 0.2873;
        if (mc.player.hasEffect(MobEffects.SPEED)) {
            speed *= 1.0 + 0.2 * (mc.player.getEffect(MobEffects.SPEED).getAmplifier() + 1);
        }
        if (mc.player.hasEffect(MobEffects.SLOWNESS)) {
            speed /= 1.0 + 0.2 * (mc.player.getEffect(MobEffects.SLOWNESS).getAmplifier() + 1);
        }
        return speed;
    }

    public static double[] directionSpeed(double speed) {
        Vec2 move = mc.player.input.getMoveVector();
        float forward = move.y;
        float strafe = move.x;
        float yaw = mc.player.getYRot();
        if (forward == 0 && strafe == 0) {
            return new double[]{0, 0};
        }
        if (forward != 0) {
            if (strafe > 0) {
                yaw += forward > 0 ? -45 : 45;
            } else if (strafe < 0) {
                yaw += forward > 0 ? 45 : -45;
            }
            strafe = 0;
            forward = forward > 0 ? 1 : -1;
        }
        double sin = Math.sin(Math.toRadians(yaw + 90));
        double cos = Math.cos(Math.toRadians(yaw + 90));
        double x = forward * speed * cos + Math.signum(strafe) * speed * sin;
        double z = forward * speed * sin - Math.signum(strafe) * speed * cos;
        return new double[]{x, z};
    }

    public static double speed() {
        return Math.hypot(mc.player.getDeltaMovement().x, mc.player.getDeltaMovement().z);
    }
}
