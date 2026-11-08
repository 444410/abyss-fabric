package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;

public class Sprint extends Module {
    public enum Mode {
        Legit, Rage
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Legit sprints forward only, Rage in every direction", this, Mode.Legit);

    public Sprint() {
        super("Sprint", "Holds down your sprint key.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.getFoodData().getFoodLevel() <= 6 || mc.player.isShiftKeyDown() || mc.player.horizontalCollision) {
            return;
        }
        if (mode.is(Mode.Legit)) {
            if (mc.player.input.getMoveVector().y > 0 && !mc.player.isUsingItem()) {
                mc.player.setSprinting(true);
            }
        } else if (MovementUtil.isMoving()) {
            mc.player.setSprinting(true);
        }
    });
}
