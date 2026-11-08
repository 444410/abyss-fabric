package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerMove;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.HoleUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

public class Anchor extends Module {
    private final Setting<Integer> activatePitch = new Setting<>("ActivatePitch", "", this, 70, -90, 90);
    private final Setting<Integer> range = new Setting<>("BlockDistance", "How far down to look for holes", this, 4, 1, 12);
    private boolean active;

    public Anchor() {
        super("Anchor", "Stop movement over a hole.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return active ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";
    }

    @EventHandler
    private final Listener<EventPlayerMove> move = new Listener<>(EventPlayerMove.class, event -> {
        active = false;
        if (mc.player.getXRot() < activatePitch.i() || mc.player.onGround() && HoleUtil.isInHole(mc.player)) {
            return;
        }
        BlockPos feet = BlockPos.containing(mc.player.position());
        for (int i = 0; i <= range.i(); i++) {
            BlockPos pos = feet.below(i);
            if (!mc.level.getBlockState(pos).isAir()) {
                return;
            }
            if (HoleUtil.hole(pos) != HoleUtil.Type.NONE) {
                double cx = pos.getX() + 0.5;
                double cz = pos.getZ() + 0.5;
                double dx = cx - mc.player.getX();
                double dz = cz - mc.player.getZ();
                if (Math.abs(dx) < 0.25 && Math.abs(dz) < 0.25) {
                    event.x = 0;
                    event.z = 0;
                    mc.player.setPos(cx, mc.player.getY(), cz);
                } else {
                    event.x = dx / 2;
                    event.z = dz / 2;
                }
                if (event.y > 0) {
                    event.y = 0;
                }
                active = true;
                return;
            }
        }
    });
}
