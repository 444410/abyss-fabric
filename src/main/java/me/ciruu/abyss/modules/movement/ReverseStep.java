package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.managers.TimerManager;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.exploit.PacketFly;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class ReverseStep extends Module {
    public enum Mode {
        Motion, Timer
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Motion);
    private final Setting<Float> timer = new Setting<>("Timer", "", this, 3f, 1f, 10f).visible(() -> mode.is(Mode.Timer));
    private final Setting<Boolean> anyBlock = new Setting<>("AnyBlock", "Not only above obsidian/bedrock", this, false);
    private boolean timerOn;
    private boolean wasOnGround;

    public ReverseStep() {
        super("ReverseStep", "Makes you fall faster.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        TimerManager.reset(this);
        timerOn = false;
    }

    private boolean blocked() {
        return mc.player.isInLava() || mc.player.isInWater() || mc.player.isFallFlying() || mc.player.getAbilities().flying
                || BlockUtil.block(mc.player.blockPosition()) == Blocks.COBWEB;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (Abyss.moduleManager.isModuleEnabled(PacketFly.class)) {
            return;
        }
        BlockPos pos = mc.player.blockPosition();
        boolean hole = false;
        for (int i = 2; i <= 4; i++) {
            var block = BlockUtil.block(pos.below(i));
            if (anyBlock.on() ? !BlockUtil.isAir(pos.below(i)) : block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
                hole = true;
                break;
            }
        }
        if (hole && !blocked()) {
            if (mc.player.onGround() && mode.is(Mode.Motion)) {
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0, -1, 0));
            }
            if (mode.is(Mode.Timer) && wasOnGround && !mc.player.onGround() && mc.player.getDeltaMovement().y < -0.1 && !timerOn) {
                TimerManager.set(this, timer.f());
                timerOn = true;
            }
        }
        if (timerOn && (mc.player.onGround() || blocked())) {
            timerOn = false;
            TimerManager.reset(this);
        }
        wasOnGround = mc.player.onGround();
    });
}
