package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.item.Items;

public class AutoGap extends Module {
    private final Setting<Float> health = new Setting<>("Health", "", this, 14f, 0f, 36f);
    private final Setting<Float> hunger = new Setting<>("Hunger", "Also eat below this hunger", this, 0f, 0f, 20f);
    private final Setting<Boolean> autoSwitch = new Setting<>("AutoSwitch", "", this, true);
    private boolean eating;
    private int oldSlot = -1;

    public AutoGap() {
        super("AutoGap", "Automatically eats golden apples.", Category.MISC);
    }

    private boolean holdingGap() {
        return mc.player.getMainHandItem().is(Items.ENCHANTED_GOLDEN_APPLE) || mc.player.getMainHandItem().is(Items.GOLDEN_APPLE)
                || mc.player.getOffhandItem().is(Items.ENCHANTED_GOLDEN_APPLE) || mc.player.getOffhandItem().is(Items.GOLDEN_APPLE);
    }

    private void stop() {
        if (eating) {
            mc.options.keyUse.setDown(false);
            if (oldSlot != -1) {
                InventoryUtil.switchTo(oldSlot);
            }
        }
        eating = false;
        oldSlot = -1;
    }

    @Override
    protected void onDisable() {
        stop();
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        boolean need = DamageUtil.health(mc.player) <= health.f() || mc.player.getFoodData().getFoodLevel() < hunger.f();
        if (!need) {
            stop();
            return;
        }
        if (!holdingGap() && autoSwitch.on()) {
            int slot = InventoryUtil.findHotbar(s -> s.is(Items.ENCHANTED_GOLDEN_APPLE) || s.is(Items.GOLDEN_APPLE));
            if (slot == -1) {
                return;
            }
            if (oldSlot == -1) {
                oldSlot = InventoryUtil.selected();
            }
            InventoryUtil.switchTo(slot);
        }
        if (holdingGap()) {
            mc.options.keyUse.setDown(true);
            eating = true;
        }
    });
}
