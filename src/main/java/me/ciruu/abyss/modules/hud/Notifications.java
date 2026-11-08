package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;

public class Notifications extends Module {
    public final Setting<Float> duration = new Setting<>("Timeout", "Seconds a notification stays", this, 3f, 0.5f, 10f);
    public final Setting<Boolean> toggleFeature = new Setting<>("ToggleFeature", "Notify on module toggles", this, false);
    public final Setting<Boolean> totemPops = new Setting<>("TotemPops", "Notify on totem pops", this, true);

    public Notifications() {
        super("Notifications", "Notification toasts in the bottom right", Category.HUD);
    }
}
