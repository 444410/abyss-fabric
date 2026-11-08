package me.ciruu.abyss.modules.combat;

import java.util.Comparator;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.ciruu.abyss.util.RotationUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public class KillAura extends Module {
    public enum Switch {
        None, Auto
    }

    public enum Target {
        Closest, Health
    }

    private final Setting<Switch> switchMode = new Setting<>("Switch", "Switch to your best weapon", this, Switch.Auto);
    private final Setting<Float> range = new Setting<>("Range", "", this, 4.2f, 0.1f, 6f);
    private final Setting<Float> wallsRange = new Setting<>("Raytrace", "Range through walls", this, 3f, 0.1f, 6f);
    private final Setting<Target> target = new Setting<>("Target", "", this, Target.Closest);
    private final Setting<Boolean> delay = new Setting<>("Delay", "Wait for the attack cooldown", this, true);
    private final Setting<Integer> ticks = new Setting<>("Ticks", "Attack every N ticks instead", this, 10, 0, 20).visible(() -> !delay.on());
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate server side", this, true);
    private final Setting<Boolean> stopEating = new Setting<>("StopEating", "Don't attack while eating", this, false);
    private final Setting<Boolean> onlySword = new Setting<>("OnlySword", "", this, false);
    private final Setting<Boolean> players = new Setting<>("Players", "", this, true);
    private final Setting<Boolean> mobs = new Setting<>("Mobs", "", this, false);
    private final Setting<Boolean> animals = new Setting<>("Animals", "", this, false);
    private final Setting<Boolean> projectiles = new Setting<>("Projectiles", "Hit fireballs and shulker bullets", this, false);
    private final Setting<Boolean> toggleOnAutoCrystal = new Setting<>("ToggleOnAC", "Pause while AutoCrystal has a target", this, true);
    private int tickCounter;
    private Entity current;

    public KillAura() {
        super("KillAura", "Attack nearby entities.", Category.COMBAT);
    }

    @Override
    public String getHudInfo() {
        return current instanceof Player p ? ChatFormatting.WHITE + p.getGameProfile().name() : null;
    }

    private boolean valid(Entity entity) {
        if (entity == mc.player || !entity.isAlive() || entity.isSpectator()) {
            return false;
        }
        double dist = mc.player.distanceTo(entity);
        if (dist > range.f() || (dist > wallsRange.f() && !mc.player.hasLineOfSight(entity))) {
            return false;
        }
        if (entity instanceof Player p) {
            return players.on() && !Abyss.friendManager.isFriend(p) && !p.isCreative();
        }
        if (entity instanceof Projectile) {
            return projectiles.on() && (entity instanceof net.minecraft.world.entity.projectile.hurtingprojectile.Fireball
                    || entity instanceof net.minecraft.world.entity.projectile.ShulkerBullet);
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        return (mobs.on() && EntityUtil.isHostile(entity)) || (animals.on() && EntityUtil.isPassive(entity));
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (toggleOnAutoCrystal.on() && AutoCrystal.isActive() && Abyss.moduleManager.get(AutoCrystal.class).getTarget() != null) {
            current = null;
            return;
        }
        if (stopEating.on() && mc.player.isUsingItem()) {
            return;
        }
        Comparator<Entity> order = target.is(Target.Health)
                ? Comparator.comparingDouble(e -> e instanceof LivingEntity l ? l.getHealth() : 0)
                : Comparator.comparingDouble(mc.player::distanceTo);
        current = null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (valid(entity) && (current == null || order.compare(entity, current) < 0)) {
                current = entity;
            }
        }
        if (current == null) {
            return;
        }
        if (switchMode.is(Switch.Auto) && !mc.player.getMainHandItem().is(ItemTags.SWORDS)) {
            int slot = InventoryUtil.findHotbar(s -> s.is(ItemTags.SWORDS));
            if (slot == -1) {
                slot = InventoryUtil.findHotbar(s -> s.is(ItemTags.AXES));
            }
            if (slot != -1) {
                InventoryUtil.switchTo(slot);
            }
        }
        if (onlySword.on() && !mc.player.getMainHandItem().is(ItemTags.SWORDS) && !mc.player.getMainHandItem().is(ItemTags.AXES)) {
            return;
        }
        if (rotate.on()) {
            RotationUtil.lookAt(current.getBoundingBox().getCenter());
        }
        boolean ready = delay.on() ? mc.player.getAttackStrengthScale(0.5f) >= 1f : ++tickCounter >= ticks.i();
        if (ready) {
            mc.gameMode.attack(mc.player, current);
            PlayerUtil.swing(InteractionHand.MAIN_HAND);
            tickCounter = 0;
        }
    });
}
