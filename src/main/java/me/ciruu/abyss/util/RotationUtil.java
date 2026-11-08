package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class RotationUtil implements Globals {
    private RotationUtil() {
    }

    public static float[] rotations(Vec3 to) {
        Vec3 eyes = mc.player.getEyePosition();
        double dx = to.x - eyes.x;
        double dy = to.y - eyes.y;
        double dz = to.z - eyes.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new float[]{Mth.wrapDegrees(yaw), Mth.clamp(pitch, -90f, 90f)};
    }

    private static float[] pending;

    public static void lookAt(Vec3 to) {
        pending = rotations(to);
    }

    public static void set(float yaw, float pitch) {
        pending = new float[]{yaw, pitch};
    }

    public static float[] take() {
        float[] out = pending;
        pending = null;
        return out;
    }
}
