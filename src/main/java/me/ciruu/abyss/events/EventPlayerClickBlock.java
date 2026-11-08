package me.ciruu.abyss.events;

import me.zero.alpine.event.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class EventPlayerClickBlock extends Cancellable {
    public final BlockPos pos;
    public final Direction direction;

    public EventPlayerClickBlock(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }
}
