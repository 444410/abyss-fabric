package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class AutoDisconnect extends Module {
    private final Setting<Boolean> totemCheck = new Setting<>("TotemCheck", "Only when you have no totems", this, true);
    private final Setting<Float> health = new Setting<>("Health", "", this, 10f, 0f, 36f);
    private final Setting<Boolean> disableOnLog = new Setting<>("Disable on Log", "", this, false);

    public AutoDisconnect() {
        super("AutoDisconnect", "Disconnects automatically if your health is below the limit.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (DamageUtil.health(mc.player) > health.f() || (totemCheck.on() && InventoryUtil.count(Items.TOTEM_OF_UNDYING) > 0)) {
            return;
        }
        if (disableOnLog.on()) {
            setEnabled(false);
        }
        mc.getConnection().getConnection().disconnect(Component.literal("[Abyss] AutoDisconnect: health was " + (int) DamageUtil.health(mc.player)));
    });
}
