package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.mixin.accessor.MinecraftAccessor;
import me.ciruu.abyss.mixin.accessor.MultiPlayerGameModeAccessor;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.item.Items;

public class FastPlace extends Module {
    private final Setting<Boolean> onlyXP = new Setting<>("OnlyXP", "Only for experience bottles", this, true);
    private final Setting<Boolean> breaking = new Setting<>("Break", "Also remove the break delay", this, true);
    private final Setting<Integer> placeDelay = new Setting<>("PlaceDelay", "", this, 0, 0, 4);

    public FastPlace() {
        super("FastPlace", "Prevents the click delay timer.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        boolean xp = mc.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE) || mc.player.getOffhandItem().is(Items.EXPERIENCE_BOTTLE);
        MinecraftAccessor accessor = (MinecraftAccessor) mc;
        if ((!onlyXP.on() || xp) && accessor.abyss$getRightClickDelay() > placeDelay.i()) {
            accessor.abyss$setRightClickDelay(placeDelay.i());
        }
        if (breaking.on()) {
            ((MultiPlayerGameModeAccessor) mc.gameMode).abyss$setDestroyDelay(0);
        }
    });
}
