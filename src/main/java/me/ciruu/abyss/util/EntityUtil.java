package me.ciruu.abyss.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class EntityUtil implements Globals {
    private EntityUtil() {
    }

    public static boolean isValidPlayer(Player player, double range) {
        return player != mc.player && player.isAlive() && !player.isSpectator() && !player.isDeadOrDying()
                && !Abyss.friendManager.isFriend(player) && mc.player.distanceTo(player) <= range;
    }

    public static List<Player> targets(double range) {
        List<Player> out = new ArrayList<>();
        for (Player player : mc.level.players()) {
            if (isValidPlayer(player, range)) {
                out.add(player);
            }
        }
        out.sort(Comparator.comparingDouble(mc.player::distanceTo));
        return out;
    }

    public static Player closestTarget(double range) {
        List<Player> targets = targets(range);
        return targets.isEmpty() ? null : targets.get(0);
    }

    public static boolean isHostile(Entity entity) {
        return entity instanceof Enemy;
    }

    public static boolean isPassive(Entity entity) {
        return entity instanceof Animal;
    }

    public static Vec3 interpolated(Entity entity, float partialTicks) {
        return entity.getPosition(partialTicks);
    }

    public static boolean canSee(LivingEntity entity) {
        return mc.player.hasLineOfSight(entity);
    }
}
