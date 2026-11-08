package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventPlayerMotionUpdate;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

public class Flight extends Module {
    public enum Mode {
        Vanilla, Creative
    }

    private final Setting<Mode> mode = new Setting<>("Mode", "Modes of the flight to use", this, Mode.Vanilla);
    private final Setting<Float> speed = new Setting<>("Speed", "Speed to use", this, 1f, 0f, 20f);
    private final Setting<Boolean> glide = new Setting<>("Glide", "Slowly sink while flying", this, false);
    private final Setting<Float> glideSpeed = new Setting<>("GlideSpeed", "Glide speed of going down", this, 0.05f, 0f, 10f).visible(() -> glide.on());
    private final Setting<Boolean> onlyElytra = new Setting<>("OnlyElytra", "Only functions while on an elytra.", this, false);
    private final Setting<Boolean> antiKick = new Setting<>("AntiKick", "Prevents you from getting kicked while flying by vanilla anticheat", this, true);
    private int ticks;

    public Flight() {
        super("Flight", "Allows you to fly.", Category.MOVEMENT);
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && mode.is(Mode.Creative) && !mc.player.isCreative() && !mc.player.isSpectator()) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().mayfly = false;
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (onlyElytra.on() && !mc.player.isFallFlying()) {
            return;
        }
        ticks++;
        if (mode.is(Mode.Creative)) {
            mc.player.getAbilities().mayfly = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlyingSpeed(speed.f() / 20f);
            return;
        }
        double y = 0;
        if (mc.options.keyJump.isDown()) {
            y += speed.f();
        }
        if (mc.options.keyShift.isDown()) {
            y -= speed.f();
        }
        if (y == 0 && glide.on()) {
            y = -glideSpeed.f();
        }
        double[] dir = MovementUtil.directionSpeed(speed.f());
        mc.player.setDeltaMovement(new Vec3(dir[0], y, dir[1]));
    });

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motion = new Listener<>(EventPlayerMotionUpdate.class, event -> {
        if (event.isPre() && antiKick.on() && ticks % 40 == 0 && !mc.player.onGround()) {
            mc.player.setPos(mc.player.getX(), mc.player.getY() - 0.04, mc.player.getZ());
        }
    });
}
