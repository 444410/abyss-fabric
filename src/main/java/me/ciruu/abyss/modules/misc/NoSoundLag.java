package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class NoSoundLag extends Module {
    public NoSoundLag() {
        super("NoSoundLag", "Prevents lag from sound packets.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> packets = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (event.getPacket() instanceof ClientboundSoundPacket packet) {
            SoundEvent sound = packet.getSound().value();
            if (sound == SoundEvents.ARMOR_EQUIP_GENERIC.value() || sound == SoundEvents.ARMOR_EQUIP_NETHERITE.value()
                    || sound == SoundEvents.ARMOR_EQUIP_DIAMOND.value() || sound == SoundEvents.ARMOR_EQUIP_ELYTRA.value()
                    || sound == SoundEvents.ARMOR_EQUIP_CHAIN.value() || sound == SoundEvents.ARMOR_EQUIP_IRON.value()
                    || sound == SoundEvents.ARMOR_EQUIP_GOLD.value() || sound == SoundEvents.ARMOR_EQUIP_LEATHER.value()) {
                event.cancel();
            }
        }
    });
}
