package me.ciruu.abyss.modules.misc;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventMouseButton;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.util.ChatUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;

public class MCFriends extends Module {
    public MCFriends() {
        super("MCFriends", "Add friends by clicking the wheel button in mouse.", Category.MISC);
    }

    @EventHandler
    private final Listener<EventMouseButton> mouse = new Listener<>(EventMouseButton.class, event -> {
        if (event.button() != 2 || !(mc.crosshairPickEntity instanceof Player player)) {
            return;
        }
        String name = player.getGameProfile().name();
        if (Abyss.friendManager.isFriend(name)) {
            Abyss.friendManager.remove(name);
            ChatUtil.send(ChatFormatting.RED + name + ChatFormatting.RESET + " removed from friends.");
        } else {
            Abyss.friendManager.add(name);
            ChatUtil.send(ChatFormatting.AQUA + name + ChatFormatting.RESET + " is now your friend.");
        }
    });
}
