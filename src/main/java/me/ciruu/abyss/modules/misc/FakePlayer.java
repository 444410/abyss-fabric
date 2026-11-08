package me.ciruu.abyss.modules.misc;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

public class FakePlayer extends Module {
    private final Setting<String> name = new Setting<>("Name", "Name of the fake player", this, "jared2013");
    private RemotePlayer fake;

    public FakePlayer() {
        super("FakePlayer", "Summon a client-side fake player to test modules.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (nullCheck()) {
            setEnabled(false);
            return;
        }
        fake = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), name.getValue()));
        fake.copyPosition(mc.player);
        fake.yHeadRot = mc.player.yHeadRot;
        fake.getInventory().replaceWith(mc.player.getInventory());
        fake.setHealth(mc.player.getHealth());
        fake.setId(-100);
        mc.level.addEntity(fake);
    }

    @Override
    protected void onDisable() {
        if (fake != null && mc.level != null) {
            mc.level.removeEntity(fake.getId(), Entity.RemovalReason.DISCARDED);
        }
        fake = null;
    }
}
