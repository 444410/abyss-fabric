package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventChatSend;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class ChatSuffix extends Module {
    private static final String DEFAULT = "ᴀʙʏꜱꜱ";
    private final Setting<Boolean> custom = new Setting<>("Custom", "Use your own suffix", this, false);
    private final Setting<String> suffix = new Setting<>("Suffix", "Suffix", this, "Abyss").visible(() -> custom.on());

    public ChatSuffix() {
        super("ChatSuffix", "Display a custom message after the player sends a message.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventChatSend> chat = new Listener<>(EventChatSend.class, event -> {
        if (event.message.startsWith("/") || event.message.startsWith(Abyss.commandManager.getPrefix())) {
            return;
        }
        String add = " | " + (custom.on() ? suffix.getValue() : DEFAULT);
        if (event.message.length() + add.length() <= 256) {
            event.message += add;
        }
    });
}
