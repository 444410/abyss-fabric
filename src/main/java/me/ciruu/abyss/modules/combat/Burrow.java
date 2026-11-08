package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Burrow extends Module {
    public enum Item {
        Obby, EChest
    }

    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Float> offset = new Setting<>("Offset", "Rubberband offset", this, 2f, -20f, 20f);
    private final Setting<Item> itemMode = new Setting<>("ItemMode", "", this, Item.Obby);

    public Burrow() {
        super("Burrow", "Glitch a player into a block at its feet.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            setEnabled(false);
            return;
        }
        BlockPos feet = CombatUtil.feet(mc.player);
        int slot = itemMode.is(Item.Obby) ? InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) : InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);
        if (slot == -1 || !mc.player.onGround() || !BlockUtil.isReplaceable(feet) || BlockUtil.isReplaceable(feet.below())) {
            setEnabled(false);
            return;
        }
        double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
        boolean hc = mc.player.horizontalCollision;
        PacketUtil.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.41999998688698, z, true, hc));
        PacketUtil.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.7531999805211997, z, true, hc));
        PacketUtil.send(new ServerboundMovePlayerPacket.Pos(x, y + 1.00133597911214, z, true, hc));
        PacketUtil.send(new ServerboundMovePlayerPacket.Pos(x, y + 1.16610926093821, z, true, hc));
        int old = InventoryUtil.selected();
        InventoryUtil.switchSilent(slot);
        if (rotate.on()) {
            PacketUtil.send(new ServerboundMovePlayerPacket.Rot(mc.player.getYRot(), 90f, true, hc));
        }
        BlockPos below = feet.below();
        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(below).add(0, 0.5, 0), Direction.UP, below, false));
        PlayerUtil.swing(InteractionHand.MAIN_HAND);
        InventoryUtil.switchSilent(old);
        PacketUtil.send(new ServerboundMovePlayerPacket.Pos(x, y + offset.f(), z, false, hc));
        setEnabled(false);
    }
}
