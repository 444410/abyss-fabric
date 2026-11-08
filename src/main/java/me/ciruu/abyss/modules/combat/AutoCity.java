package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.ciruu.abyss.util.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AutoCity extends Module {
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, false);
    private final Setting<Float> range = new Setting<>("Range", "", this, 5f, 0f, 6f);
    private final Setting<Boolean> burrow = new Setting<>("Burrow", "Mine the target's burrow block first", this, false);
    private final Setting<Boolean> oneThirteen = new Setting<>("1.13Mode", "Crystals need only one air block above", this, true);

    public AutoCity() {
        super("AutoCity", "City near players.", Category.COMBAT);
    }

    private boolean cityable(BlockPos pos) {
        if (BlockUtil.block(pos) != Blocks.OBSIDIAN && BlockUtil.block(pos) != Blocks.ENDER_CHEST) {
            return false;
        }
        return BlockUtil.distance(pos) <= range.f();
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        Player target = EntityUtil.closestTarget(range.f() + 2);
        if (target == null) {
            return;
        }
        BlockPos feet = CombatUtil.feet(target);
        BlockPos mine = null;
        if (burrow.on() && cityable(feet)) {
            mine = feet;
        }
        if (mine == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = feet.relative(dir);
                BlockPos beyond = side.relative(dir);
                boolean crystalSpot = (BlockUtil.isAir(beyond) && (BlockUtil.block(beyond.below()) == Blocks.OBSIDIAN || BlockUtil.block(beyond.below()) == Blocks.BEDROCK))
                        || BlockUtil.block(side.below()) == Blocks.OBSIDIAN || BlockUtil.block(side.below()) == Blocks.BEDROCK;
                if (cityable(side) && crystalSpot && (oneThirteen.on() || BlockUtil.isAir(side.above()))) {
                    mine = side;
                    break;
                }
            }
        }
        if (mine == null) {
            setEnabled(false);
            return;
        }
        int pick = InventoryUtil.findHotbar(s -> s.is(ItemTags.PICKAXES));
        if (pick != -1) {
            InventoryUtil.switchTo(pick);
        }
        if (rotate.on()) {
            RotationUtil.lookAt(Vec3.atCenterOf(mine));
        }
        PlayerUtil.swing(InteractionHand.MAIN_HAND);
        PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, mine, Direction.UP));
        PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, mine, Direction.UP));
        setEnabled(false);
    });
}
