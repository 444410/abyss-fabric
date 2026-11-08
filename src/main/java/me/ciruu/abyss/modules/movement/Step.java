package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.PacketUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Step extends Module {
    public enum Mode {
        Vanilla, NCP
    }

    public final Setting<Mode> mode = new Setting<>("Mode", "Mode", this, Mode.Vanilla);
    private final Setting<Float> height = new Setting<>("Height", "", this, 2f, 1f, 10f).visible(() -> mode.is(Mode.Vanilla));
    private final Setting<Integer> heightNCP = new Setting<>("HeightNCP", "", this, 2, 1, 2).visible(() -> mode.is(Mode.NCP));
    private final Setting<Boolean> disable = new Setting<>("Disable", "Turn off after one step", this, false).visible(() -> mode.is(Mode.NCP));

    private static final double[] ONE = {0.42, 0.75};
    private static final double[] ONE_HALF = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
    private static final double[] TWO = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};

    public Step() {
        super("Step", "Step up blocks.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    private AttributeInstance attribute() {
        return mc.player.getAttribute(Attributes.STEP_HEIGHT);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && attribute() != null) {
            attribute().setBaseValue(0.6);
        }
    }

    private double stepHeight() {
        AABB box = mc.player.getBoundingBox().move(0, 0.05, 0).inflate(0.05);
        box = box.setMaxY(box.maxY + heightNCP.i());
        double top = -1;
        for (VoxelShape shape : mc.level.getBlockCollisions(mc.player, box)) {
            if (!shape.isEmpty() && shape.max(net.minecraft.core.Direction.Axis.Y) > top) {
                top = shape.max(net.minecraft.core.Direction.Axis.Y);
            }
        }
        top -= mc.player.getY();
        return top > 0 && top <= heightNCP.i() ? top : 0;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mode.is(Mode.Vanilla)) {
            attribute().setBaseValue(height.f());
            return;
        }
        attribute().setBaseValue(0.6);
        if (!mc.player.horizontalCollision || !mc.player.onGround() || mc.player.fallDistance != 0 || mc.player.onClimbable()
                || mc.player.isInWater() || mc.player.isInLava() || mc.options.keyJump.isDown()) {
            return;
        }
        double h = stepHeight();
        if (h == 0) {
            return;
        }
        double[] offsets = h <= 1.0 ? ONE : h <= 1.5 ? ONE_HALF : TWO;
        for (double offset : offsets) {
            PacketUtil.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY() + offset, mc.player.getZ(), mc.player.onGround(), false));
        }
        mc.player.setPos(mc.player.getX(), mc.player.getY() + h, mc.player.getZ());
        if (disable.on()) {
            setEnabled(false);
        }
    });
}
