package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public final class EventPlayerMove extends Cancellable {
    public final MoverType type;
    public double x;
    public double y;
    public double z;

    public EventPlayerMove(MoverType type, Vec3 motion) {
        this.type = type;
        this.x = motion.x;
        this.y = motion.y;
        this.z = motion.z;
    }

    public Vec3 motion() {
        return new Vec3(x, y, z);
    }
}
