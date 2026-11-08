package me.ciruu.abyss.modules.client;

import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;

public class Client extends Module {
    public final Setting<Boolean> friendMessage = new Setting<>("FriendMessage", "Sends a DM to the person that you friended", this, false);
    public final Setting<String> prefix = new Setting<>("CMDPrefix", "", this, "*");

    public Client() {
        super("Client", "Client options", Category.CLIENT, true);
    }
}
