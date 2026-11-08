package me.ciruu.abyss.modules.movement;

import me.ciruu.abyss.events.EventNetworkPostPacketEvent;
import me.ciruu.abyss.events.EventPlayerSlowdown;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.KeyState;
import me.ciruu.abyss.util.PacketUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

public class NoSlowDown extends Module {
    public final Setting<Boolean> guiMove = new Setting<>("GuiMove", "Walk with screens open", this, true);
    public final Setting<Boolean> items = new Setting<>("Items", "No slowdown while using items", this, true);
    public final Setting<Boolean> strict = new Setting<>("Strict", "NCP strict bypass", this, false);

    public NoSlowDown() {
        super("NoSlowDown", "Prevents slowing when eating or keeping inventory open.", Category.MOVEMENT);
    }

    @EventHandler
    private final Listener<EventPlayerSlowdown> slowdown = new Listener<>(EventPlayerSlowdown.class, event -> {
        if (items.on() && !mc.player.isPassenger()) {
            event.multiplier = 1f;
        }
    });

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (guiMove.on() && mc.gui.screen() != null && !(mc.gui.screen() instanceof ChatScreen) && !(mc.gui.screen() instanceof AbstractSignEditScreen)) {
            KeyState.updateMovementKeys();
        }
    });

    @EventHandler
    private final Listener<EventNetworkPostPacketEvent> post = new Listener<>(EventNetworkPostPacketEvent.class, event -> {
        if (event.getPacket() instanceof ServerboundMovePlayerPacket && strict.on() && items.on() && mc.player.isUsingItem() && !mc.player.isPassenger()) {
            PacketUtil.sendSilent(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, BlockPos.containing(mc.player.position()), Direction.DOWN));
        }
    });
}
