package me.ciruu.abyss.util;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class BlockUtil implements Globals {
    private BlockUtil() {
    }

    public static BlockState state(BlockPos pos) {
        return mc.level.getBlockState(pos);
    }

    public static Block block(BlockPos pos) {
        return state(pos).getBlock();
    }

    public static boolean isAir(BlockPos pos) {
        return state(pos).isAir();
    }

    public static boolean isReplaceable(BlockPos pos) {
        return state(pos).canBeReplaced();
    }

    public static boolean isBlastResistant(BlockPos pos) {
        Block block = block(pos);
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.ENDER_CHEST
                || block == Blocks.CRYING_OBSIDIAN || block == Blocks.RESPAWN_ANCHOR || block == Blocks.NETHERITE_BLOCK
                || block == Blocks.ANCIENT_DEBRIS || block == Blocks.REINFORCED_DEEPSLATE;
    }

    public static boolean isEntityFree(BlockPos pos, boolean ignoreItems) {
        for (Entity entity : mc.level.getEntities(null, new AABB(pos))) {
            if (ignoreItems && (entity instanceof ItemEntity || entity instanceof ExperienceOrb)) {
                continue;
            }
            if (entity.isSpectator() || !entity.isAlive()) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static Direction placeSide(BlockPos pos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbour = pos.relative(side);
            BlockState state = state(neighbour);
            if (!state.isAir() && !state.canBeReplaced() && state.getFluidState().isEmpty()) {
                return side;
            }
        }
        return null;
    }

    public static boolean canPlace(BlockPos pos) {
        return isReplaceable(pos) && isEntityFree(pos, true) && placeSide(pos) != null;
    }

    public static boolean place(BlockPos pos, InteractionHand hand, boolean rotate, boolean swing) {
        Direction side = placeSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.relative(side);
        Direction face = side.getOpposite();
        Vec3 hit = Vec3.atCenterOf(neighbour).add(face.getUnitVec3().scale(0.5));
        if (rotate) {
            float[] rot = RotationUtil.rotations(hit);
            PacketUtil.send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot(rot[0], rot[1], mc.player.onGround(), mc.player.horizontalCollision));
        }
        boolean sneak = needsSneak(neighbour) && !mc.player.isShiftKeyDown();
        if (sneak) {
            sneak(true);
        }
        mc.gameMode.useItemOn(mc.player, hand, new BlockHitResult(hit, face, neighbour, false));
        if (swing) {
            PlayerUtil.swing(hand);
        }
        if (sneak) {
            sneak(false);
        }
        return true;
    }

    public static void sneak(boolean shift) {
        Input k = mc.player.input.keyPresses;
        PacketUtil.send(new ServerboundPlayerInputPacket(new Input(k.forward(), k.backward(), k.left(), k.right(), k.jump(), shift, k.sprint())));
    }

    private static boolean needsSneak(BlockPos pos) {
        Block block = block(pos);
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.ENDER_CHEST || block == Blocks.CRAFTING_TABLE
                || block == Blocks.FURNACE || block == Blocks.ANVIL || block == Blocks.ENCHANTING_TABLE || block == Blocks.HOPPER
                || block == Blocks.DISPENSER || block == Blocks.DROPPER || block == Blocks.BREWING_STAND || block == Blocks.BEACON
                || block instanceof net.minecraft.world.level.block.BedBlock || block instanceof net.minecraft.world.level.block.ShulkerBoxBlock
                || block instanceof net.minecraft.world.level.block.DoorBlock || block instanceof net.minecraft.world.level.block.TrapDoorBlock
                || block instanceof net.minecraft.world.level.block.FenceGateBlock || block instanceof net.minecraft.world.level.block.ButtonBlock
                || block == Blocks.LEVER || block == Blocks.RESPAWN_ANCHOR;
    }

    public static List<BlockPos> sphere(BlockPos center, double range, boolean hollow) {
        List<BlockPos> out = new ArrayList<>();
        int r = (int) Math.ceil(range);
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    double dist = x * x + y * y + z * z;
                    if (dist <= range * range && (!hollow || dist >= (range - 1) * (range - 1))) {
                        out.add(center.offset(x, y, z));
                    }
                }
            }
        }
        out.sort((a, b) -> Double.compare(a.distSqr(center), b.distSqr(center)));
        return out;
    }

    public static BlockPos playerPos() {
        return BlockPos.containing(mc.player.getX(), mc.player.getY() + 0.2, mc.player.getZ());
    }

    public static double distance(BlockPos pos) {
        return Math.sqrt(mc.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)));
    }
}
