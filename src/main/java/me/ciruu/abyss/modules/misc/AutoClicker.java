package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.mixin.accessor.KeyMappingAccessor;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.KeyMapping;

public class AutoClicker extends Module {
    public enum Click {
        Left, Right
    }

    private final Setting<Click> click = new Setting<>("Click", "", this, Click.Left);
    private final Setting<Float> delay = new Setting<>("Delay", "Seconds between clicks", this, 0.1f, 0f, 10f);
    private final Setting<Integer> clicksPerTick = new Setting<>("ClicksPerTick", "", this, 1, 1, 20);
    private final Setting<Boolean> onlyHeld = new Setting<>("OnlyHeld", "Only while the button is held", this, false);
    private final Timer timer = new Timer();

    public AutoClicker() {
        super("AutoClicker", "Clicks for you.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mc.gui.screen() != null || !timer.passed((long) (delay.f() * 1000))) {
            return;
        }
        KeyMapping mapping = click.is(Click.Left) ? mc.options.keyAttack : mc.options.keyUse;
        if (onlyHeld.on() && !mapping.isDown()) {
            return;
        }
        for (int i = 0; i < clicksPerTick.i(); i++) {
            KeyMapping.click(((KeyMappingAccessor) mapping).abyss$getKey());
        }
        timer.reset();
    });
}
