package me.ciruu.abyss.modules;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.modules.client.ChatNotifier;
import me.ciruu.abyss.modules.hud.Notifications;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.zero.alpine.listener.Listenable;
import net.minecraft.ChatFormatting;

public class Module implements Listenable, Globals {
    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private String displayName;
    private boolean enabled;
    private boolean hidden;
    private int key = -1;

    public float animation;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.displayName = name;
        this.description = description;
        this.category = category;
    }

    public Module(String name, String description, Category category, boolean hidden) {
        this(name, description, category);
        this.hidden = hidden;
    }

    public void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public Setting<?> getSetting(String name) {
        for (Setting<?> setting : settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            animation = 0f;
            Abyss.EVENT_BUS.subscribe(this);
            onEnable();
            notifyToggle(true);
        } else {
            Abyss.EVENT_BUS.unsubscribe(this);
            onDisable();
            notifyToggle(false);
        }
    }

    public void setEnabledSilently(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            Abyss.EVENT_BUS.subscribe(this);
            if (!Globals.nullCheck()) {
                onEnable();
            }
        } else {
            Abyss.EVENT_BUS.unsubscribe(this);
            if (!Globals.nullCheck()) {
                onDisable();
            }
        }
    }

    private void notifyToggle(boolean on) {
        if (Abyss.moduleManager == null || this instanceof ChatNotifier) {
            return;
        }
        String text = ChatFormatting.WHITE + displayName + (on ? ChatFormatting.GREEN + " ON" : ChatFormatting.RED + " OFF");
        if (Abyss.moduleManager.isModuleEnabled(ChatNotifier.class)) {
            ChatUtil.send(text);
        }
        Notifications notifications = Abyss.moduleManager.get(Notifications.class);
        if (notifications != null && notifications.isEnabled() && notifications.toggleFeature.on()) {
            Abyss.notificationManager.push(ChatFormatting.WHITE + displayName + (on ? ChatFormatting.GREEN + " enabled!" : ChatFormatting.RED + " disabled!"));
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public String getHudInfo() {
        return null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    protected static boolean nullCheck() {
        return Globals.nullCheck();
    }
}
