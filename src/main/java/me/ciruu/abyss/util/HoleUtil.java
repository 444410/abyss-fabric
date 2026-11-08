package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;

public final class HoleUtil implements Globals {
    public enum Type {
        NONE, BEDROCK, OBSIDIAN, DOUBLE
    }

    private HoleUtil() {
    }

    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static Type hole(BlockPos pos) {
        if (!BlockUtil.isAir(pos) || !BlockUtil.isAir(pos.above()) || !BlockUtil.isAir(pos.above(2))) {
            return Type.NONE;
        }
        boolean allBedrock = true;
        BlockPos below = pos.below();
        if (!BlockUtil.isBlastResistant(below)) {
            return Type.NONE;
        }
        if (BlockUtil.block(below) != Blocks.BEDROCK) {
            allBedrock = false;
        }
        for (Direction dir : HORIZONTAL) {
            BlockPos side = pos.relative(dir);
            if (!BlockUtil.isBlastResistant(side)) {
                return Type.NONE;
            }
            if (BlockUtil.block(side) != Blocks.BEDROCK) {
                allBedrock = false;
            }
        }
        return allBedrock ? Type.BEDROCK : Type.OBSIDIAN;
    }

    public static BlockPos doubleHole(BlockPos pos) {
        if (!BlockUtil.isAir(pos) || !BlockUtil.isAir(pos.above()) || !BlockUtil.isBlastResistant(pos.below())) {
            return null;
        }
        for (Direction dir : HORIZONTAL) {
            BlockPos other = pos.relative(dir);
            if (!BlockUtil.isAir(other) || !BlockUtil.isAir(other.above()) || !BlockUtil.isBlastResistant(other.below())) {
                continue;
            }
            boolean ok = true;
            for (Direction d : HORIZONTAL) {
                if (d == dir) {
                    continue;
                }
                if (!BlockUtil.isBlastResistant(pos.relative(d))) {
                    ok = false;
                    break;
                }
            }
            if (!ok) {
                continue;
            }
            for (Direction d : HORIZONTAL) {
                if (d == dir.getOpposite()) {
                    continue;
                }
                if (!BlockUtil.isBlastResistant(other.relative(d))) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return other;
            }
        }
        return null;
    }

    public static boolean isInHole(net.minecraft.world.entity.Entity entity) {
        return hole(BlockPos.containing(entity.getX(), entity.getY() + 0.2, entity.getZ())) != Type.NONE;
    }
}
