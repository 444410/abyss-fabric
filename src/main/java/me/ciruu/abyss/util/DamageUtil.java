package me.ciruu.abyss.util;

import me.ciruu.abyss.Globals;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;

public final class DamageUtil implements Globals {
    private static final EquipmentSlot[] ARMOR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private DamageUtil() {
    }

    public static float crystalDamage(LivingEntity target, Vec3 pos) {
        return explosionDamage(target, pos, 6f);
    }

    public static float explosionDamage(LivingEntity target, Vec3 pos, float power) {
        if (target == null || target.isInvulnerable() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            return 0f;
        }
        double size = power * 2.0;
        double dist = Math.sqrt(target.position().distanceToSqr(pos)) / size;
        if (dist > 1.0) {
            return 0f;
        }
        double exposure = ServerExplosion.getSeenPercent(pos, target);
        double impact = (1.0 - dist) * exposure;
        float damage = (float) ((impact * impact + impact) / 2.0 * 7.0 * size + 1.0);
        if (target instanceof Player) {
            Difficulty difficulty = mc.level.getDifficulty();
            if (difficulty == Difficulty.PEACEFUL) {
                damage = 0f;
            } else if (difficulty == Difficulty.EASY) {
                damage = Math.min(damage / 2f + 1f, damage);
            } else if (difficulty == Difficulty.HARD) {
                damage = damage * 3f / 2f;
            }
        }
        damage = CombatRules.getDamageAfterAbsorb(target, damage, mc.level.damageSources().generic(), target.getArmorValue(),
                (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        if (target.hasEffect(MobEffects.RESISTANCE)) {
            int amp = target.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1;
            damage *= Math.max(0f, 1f - amp * 0.2f);
        }
        int epf = 0;
        for (EquipmentSlot slot : ARMOR) {
            ItemStack stack = target.getItemBySlot(slot);
            epf += level(stack, Enchantments.PROTECTION);
            epf += level(stack, Enchantments.BLAST_PROTECTION) * 2;
        }
        damage = CombatRules.getDamageAfterMagicAbsorb(damage, Math.min(20, epf));
        return Math.max(0f, damage);
    }

    public static int level(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack.isEmpty()) {
            return 0;
        }
        for (var entry : stack.getEnchantments().entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (holder.is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static float health(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }
}
