package me.ciruu.abyss.modules.combat;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.PlayerUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Trigger extends Module {
    private final Setting<Boolean> hitDelay = new Setting<>("Hit Delay", "Use vanilla hit delay", this, true);
    private final Setting<Boolean> onlySword = new Setting<>("OnlySword", "", this, false);
    private final Setting<Boolean> onlyPlayers = new Setting<>("OnlyPlayers", "", this, true);

    public Trigger() {
        super("Trigger", "Attacks the entity in the crosshair.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        Entity target = mc.crosshairPickEntity;
        if (!(target instanceof LivingEntity living) || !living.isAlive() || mc.gui.screen() != null) {
            return;
        }
        if (onlyPlayers.on() && !(target instanceof Player)) {
            return;
        }
        if (target instanceof Player p && Abyss.friendManager.isFriend(p)) {
            return;
        }
        if (onlySword.on() && !mc.player.getMainHandItem().is(ItemTags.SWORDS)) {
            return;
        }
        if (hitDelay.on() && mc.player.getAttackStrengthScale(0.5f) < 1f) {
            return;
        }
        mc.gameMode.attack(mc.player, target);
        PlayerUtil.swing(InteractionHand.MAIN_HAND);
    });
}
