package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerDeath;
import me.ciruu.abyss.events.EventTotemPop;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.hud.Notifications;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;

public class TotemPopCount extends Module {
    private final Setting<Boolean> sendChatMsg = new Setting<>("SendChatMsg", "Sends to you a message when somebody pops a totem", this, true);

    public TotemPopCount() {
        super("TotemPopCount", "Counts totem pops from players.", Category.MISC);
    }

    private String color(String name) {
        return (Abyss.friendManager.isFriend(name) ? ChatFormatting.AQUA : ChatFormatting.RED) + name + ChatFormatting.RESET;
    }

    @EventHandler
    private final Listener<EventTotemPop> pop = new Listener<>(EventTotemPop.class, event -> {
        String name = event.player().getGameProfile().name();
        String text = color(name) + " popped " + ChatFormatting.WHITE + event.pops() + ChatFormatting.RESET + (event.pops() == 1 ? " totem!" : " totems!");
        if (sendChatMsg.on()) {
            ChatUtil.send(text);
        }
        Notifications n = Abyss.moduleManager.get(Notifications.class);
        if (n.isEnabled() && n.totemPops.on()) {
            Abyss.notificationManager.push(text);
        }
    });

    @EventHandler
    private final Listener<EventPlayerDeath> death = new Listener<>(EventPlayerDeath.class, event -> {
        String name = event.player().getGameProfile().name();
        int pops = Abyss.totemPopManager.getPops(name);
        if (sendChatMsg.on()) {
            ChatUtil.send(color(name) + " died after popping " + ChatFormatting.WHITE + pops + ChatFormatting.RESET + (pops == 1 ? " totem!" : " totems!"));
        }
    });
}
