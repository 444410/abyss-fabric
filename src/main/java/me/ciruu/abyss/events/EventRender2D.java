package me.ciruu.abyss.events;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public record EventRender2D(GuiGraphicsExtractor graphics, float partialTicks) {
}
