package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class AutoFish extends Module {
    private final Setting<Integer> castingDelay = new Setting<>("CastingDelay", "Ticks to wait before recasting", this, 20, 0, 40);
    private final Setting<Float> maxSoundDistance = new Setting<>("MaxSoundDistance", "Max distance between the splash and your hook (0 disables)", this, 2f, 0f, 5f);
    private final Setting<Integer> failSafeTime = new Setting<>("FailSafeTime", "Reel in after this many ticks (0 disables)", this, 600, 0, 3000);
    private int recastIn = -1;
    private int castTicks;
    private volatile boolean bite;

    public AutoFish() {
        super("AutoFish", "Fish automatically.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientboundSoundPacket packet && packet.getSound().value() == SoundEvents.FISHING_BOBBER_SPLASH
                && mc.player != null && mc.player.fishing != null) {
            double dist = mc.player.fishing.position().distanceTo(new net.minecraft.world.phys.Vec3(packet.getX(), packet.getY(), packet.getZ()));
            if (maxSoundDistance.f() == 0 || dist <= maxSoundDistance.f()) {
                bite = true;
            }
        }
    });

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        InteractionHand hand = mc.player.getMainHandItem().is(Items.FISHING_ROD) ? InteractionHand.MAIN_HAND
                : mc.player.getOffhandItem().is(Items.FISHING_ROD) ? InteractionHand.OFF_HAND : null;
        if (hand == null) {
            return;
        }
        if (mc.player.fishing != null) {
            castTicks++;
            if (bite || (failSafeTime.i() > 0 && castTicks > failSafeTime.i())) {
                mc.gameMode.useItem(mc.player, hand);
                bite = false;
                recastIn = castingDelay.i();
            }
            return;
        }
        castTicks = 0;
        if (recastIn > 0) {
            recastIn--;
        } else if (recastIn == 0) {
            mc.gameMode.useItem(mc.player, hand);
            recastIn = -1;
        }
    });
}
