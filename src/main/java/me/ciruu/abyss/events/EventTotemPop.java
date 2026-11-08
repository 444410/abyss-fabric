package me.ciruu.abyss.events;

import net.minecraft.world.entity.player.Player;

public record EventTotemPop(Player player, int pops) {
}
