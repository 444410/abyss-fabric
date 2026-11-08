package me.ciruu.abyss.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.modules.misc.DisableFriends;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class FriendManager {
    private final List<String> friends = new ArrayList<>();

    public boolean isFriend(String name) {
        if (Abyss.moduleManager != null && Abyss.moduleManager.isModuleEnabled(DisableFriends.class)) {
            return false;
        }
        return friends.contains(name.toLowerCase(Locale.ROOT));
    }

    public boolean isFriend(Entity entity) {
        return entity instanceof Player player && isFriend(player.getGameProfile().name());
    }

    public void add(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (!friends.contains(lower)) {
            friends.add(lower);
        }
    }

    public boolean remove(String name) {
        return friends.remove(name.toLowerCase(Locale.ROOT));
    }

    public List<String> getFriends() {
        return friends;
    }
}
