package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventMouseButton;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class MCPearl extends Module {
    private final Setting<Float> delay = new Setting<>("Delay", "Delay (MS)", this, 100f, 0f, 1500f);
    private final Timer timer = new Timer();

    public MCPearl() {
        super("MCPearl", "Throw an ender pearl by clicking the wheel button in mouse.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventMouseButton> mouse = new Listener<>(EventMouseButton.class, event -> {
        if (event.button() != 2 || mc.crosshairPickEntity instanceof Player || !timer.passed((long) delay.f())) {
            return;
        }
        int slot = InventoryUtil.findHotbar(Items.ENDER_PEARL);
        if (slot == -1) {
            return;
        }
        int old = InventoryUtil.selected();
        InventoryUtil.switchTo(slot);
        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
        InventoryUtil.switchTo(old);
        timer.reset();
    });
}
