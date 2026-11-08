package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.item.Items;

public class BowSpam extends Module {
    private final Setting<Integer> ticks = new Setting<>("Ticks", "Charge time before releasing", this, 3, 3, 20);

    public BowSpam() {
        super("BowSpam", "Spam arrows with a bow.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.player.isUsingItem() && mc.player.getUseItem().is(Items.BOW) && mc.player.getTicksUsingItem() >= ticks.i()) {
            mc.gameMode.releaseUsingItem(mc.player);
        }
    });
}
