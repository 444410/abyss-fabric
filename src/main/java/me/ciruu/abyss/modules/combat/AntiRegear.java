package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class AntiRegear extends Module {
    private final Setting<Float> range = new Setting<>("Range", "", this, 5f, 0f, 6f);

    public AntiRegear() {
        super("AntiRegear", "Prevent other players to place shulkers and regear near you", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), range.f(), false)) {
            if (!(BlockUtil.block(pos) instanceof ShulkerBoxBlock) || BlockUtil.distance(pos) > range.f()) {
                continue;
            }
            boolean enemyNear = false;
            for (Player p : mc.level.players()) {
                if (p != mc.player && !Abyss.friendManager.isFriend(p) && p.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos)) < 36) {
                    enemyNear = true;
                    break;
                }
            }
            if (!enemyNear) {
                continue;
            }
            PlayerUtil.swing(InteractionHand.MAIN_HAND);
            PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
            PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
            return;
        }
    });
}
