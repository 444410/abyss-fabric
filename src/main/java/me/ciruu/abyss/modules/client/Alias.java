package me.ciruu.abyss.modules.client;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;

public class Alias extends Module {
    private final Setting<String> alias1 = new Setting<>("Alias1", "name=command, e.g. s=toggle Surround", this, "");
    private final Setting<String> alias2 = new Setting<>("Alias2", "", this, "");
    private final Setting<String> alias3 = new Setting<>("Alias3", "", this, "");

    public Alias() {
        super("Alias", "Short names for client commands", Category.CLIENT);
    }

    public boolean run(String name) {
        if (!isEnabled()) {
            return false;
        }
        for (Setting<String> alias : java.util.List.of(alias1, alias2, alias3)) {
            String value = alias.getValue();
            int eq = value.indexOf('=');
            if (eq > 0 && value.substring(0, eq).trim().equalsIgnoreCase(name)) {
                Abyss.commandManager.execute(value.substring(eq + 1).trim());
                return true;
            }
        }
        return false;
    }
}
