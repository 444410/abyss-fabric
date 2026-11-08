package me.ciruu.abyss.modules.combat;

import java.awt.Color;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AutoBed extends Module {
    private final Setting<Float> range = new Setting<>("Range", "", this, 5f, 0f, 6f);
    private final Setting<Integer> delay = new Setting<>("Delay", "Ticks", this, 0, 0, 10);
    private final Setting<Float> maxSelfDamage = new Setting<>("MaxSelfDMG", "", this, 8f, 0f, 36f);
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(255, 0, 0, 125));
    private int ticks;
    private BlockPos renderPos;

    public AutoBed() {
        super("AutoBed", "Place and break beds in the nether automatically.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        renderPos = null;
        if (mc.level.dimension() == Level.OVERWORLD || ++ticks <= delay.i()) {
            return;
        }
        ticks = 0;

        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), range.f(), false)) {
            if (BlockUtil.block(pos) instanceof BedBlock && BlockUtil.distance(pos) <= range.f()
                    && DamageUtil.explosionDamage(mc.player, Vec3.atCenterOf(pos), 5f) <= maxSelfDamage.f()) {
                mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                return;
            }
        }
        Player target = EntityUtil.closestTarget(range.f() + 2);
        int slot = InventoryUtil.findHotbar(s -> s.getItem() instanceof BlockItem bi && bi.getBlock() instanceof BedBlock);
        if (target == null || slot == -1) {
            return;
        }
        BlockPos head = CombatUtil.feet(target).above();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos foot = head.relative(dir);
            if (!BlockUtil.isReplaceable(head) || !BlockUtil.isReplaceable(foot) || BlockUtil.isReplaceable(foot.below()) || BlockUtil.distance(foot) > range.f()) {
                continue;
            }
            if (DamageUtil.explosionDamage(mc.player, Vec3.atCenterOf(head), 5f) > maxSelfDamage.f()) {
                return;
            }

            float yaw = dir.getOpposite().toYRot();
            PacketUtil.send(new ServerboundMovePlayerPacket.Rot(yaw, 90f, mc.player.onGround(), mc.player.horizontalCollision));
            int old = InventoryUtil.selected();
            InventoryUtil.switchTo(slot);
            BlockPos support = foot.below();
            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(support).add(0, 0.5, 0), Direction.UP, support, false));
            PlayerUtil.swing(InteractionHand.MAIN_HAND);
            InventoryUtil.switchTo(old);
            renderPos = foot;
            return;
        }
    });

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        if (renderPos != null) {
            RenderUtil.block(renderPos, color.argb(), ColorUtil.withAlpha(color.argb(), 255), 1.5f);
        }
    });
}
