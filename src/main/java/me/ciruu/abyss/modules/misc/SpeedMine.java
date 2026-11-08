package me.ciruu.abyss.modules.misc;

import java.awt.Color;
import me.ciruu.abyss.events.EventPlayerClickBlock;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.mixin.accessor.MultiPlayerGameModeAccessor;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SpeedMine extends Module {
    public enum Mode {
        Packet, Damage
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Mode", this, Mode.Packet);
    private final Setting<Float> finalDamage = new Setting<>("FinalDamage", "Final damage", this, 0.7f, 0f, 1f).visible(() -> mode.is(Mode.Damage));
    private final Setting<Boolean> reset = new Setting<>("Reset", "Don't lose progress when you look away", this, true);
    private final Setting<Boolean> render = new Setting<>("Render", "", this, true);
    private final Setting<Color> startColor = new Setting<>("StartColor", "", this, new Color(255, 0, 0, 120)).visible(() -> render.on());
    private final Setting<Color> endColor = new Setting<>("EndColor", "", this, new Color(0, 255, 0, 120)).visible(() -> render.on());
    private final Setting<Float> range = new Setting<>("Range", "", this, 6f, 0f, 50f);
    private final Setting<Boolean> silentSwitch = new Setting<>("SilentSwitch", "Switch to the best tool only for the break packet", this, false);
    private final Setting<Float> lineWidth = new Setting<>("LineWidth", "", this, 1.5f, 0.1f, 10f);

    private BlockPos pos;
    private Direction face;
    private float progress;
    private boolean sentStop;

    public SpeedMine() {
        super("SpeedMine", "Mines blocks faster.", Category.MISC);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        pos = null;
    }

    private int bestTool(BlockState state) {
        int best = -1;
        float bestSpeed = 1f;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            float speed = stack.getDestroySpeed(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                best = i;
            }
        }
        return best;
    }

    private float breakDelta(BlockState state) {
        float hardness = state.getDestroySpeed(mc.level, pos);
        if (hardness < 0) {
            return 0;
        }
        int tool = bestTool(state);
        int old = InventoryUtil.selected();
        if (tool != -1) {
            mc.player.getInventory().setSelectedSlot(tool);
        }
        float delta = state.getDestroyProgress(mc.player, mc.level, pos);
        mc.player.getInventory().setSelectedSlot(old);
        return delta;
    }

    @EventHandler
    private final Listener<EventPlayerClickBlock> click = new Listener<>(EventPlayerClickBlock.class, event -> {
        if (!mode.is(Mode.Packet) || mc.player.isCreative() || BlockUtil.block(event.pos) == Blocks.BEDROCK) {
            return;
        }
        if (event.pos.equals(pos)) {
            event.cancel();
            return;
        }
        pos = event.pos;
        face = event.direction;
        progress = 0;
        sentStop = false;
        PlayerUtil.swing(InteractionHand.MAIN_HAND);
        PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, face));
        event.cancel();
    });

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        MultiPlayerGameModeAccessor gameMode = (MultiPlayerGameModeAccessor) mc.gameMode;
        if (mode.is(Mode.Damage)) {
            if (gameMode.abyss$getDestroyProgress() >= finalDamage.f()) {
                gameMode.abyss$setDestroyProgress(1f);
            }
            return;
        }
        if (pos == null) {
            return;
        }
        BlockState state = mc.level.getBlockState(pos);
        if (state.isAir() || BlockUtil.distance(pos) > range.f()) {
            if (state.isAir() && sentStop && !reset.on()) {
                return;
            }
            pos = state.isAir() && reset.on() ? pos : null;
            if (pos != null && state.isAir()) {
                progress = 0;
                sentStop = false;
            }
            return;
        }
        progress += breakDelta(state);
        if (progress >= 1f && !sentStop) {
            int tool = bestTool(state);
            int old = InventoryUtil.selected();
            if (silentSwitch.on() && tool != -1) {
                InventoryUtil.switchSilent(tool);
            } else if (tool != -1) {
                InventoryUtil.switchTo(tool);
            }
            PacketUtil.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, face));
            if (silentSwitch.on() && tool != -1) {
                InventoryUtil.switchSilent(old);
            }
            sentStop = true;
        }
    });

    @EventHandler
    private final Listener<EventRenderWorld> draw = new Listener<>(EventRenderWorld.class, event -> {
        if (!render.on() || pos == null || !mode.is(Mode.Packet)) {
            return;
        }
        float p = Math.min(1f, progress);
        Color a = startColor.color();
        Color b = endColor.color();
        int color = ColorUtil.argb((int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * p), (int) (a.getRed() + (b.getRed() - a.getRed()) * p),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * p), (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * p));
        double s = 0.5 * p;
        AABB box = new AABB(pos.getX() + 0.5 - s, pos.getY() + 0.5 - s, pos.getZ() + 0.5 - s, pos.getX() + 0.5 + s, pos.getY() + 0.5 + s, pos.getZ() + 0.5 + s);
        RenderUtil.box(box, color, ColorUtil.withAlpha(color, 255), lineWidth.f());
    });
}
