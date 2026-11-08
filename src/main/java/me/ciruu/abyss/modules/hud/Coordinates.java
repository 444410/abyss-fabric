package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.settings.Setting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.level.Level;

public class Coordinates extends HudModule {
    private final Setting<Boolean> nether = new Setting<>("Nether", "Show the other dimension's coordinates", this, true);
    private final Setting<Boolean> direction = new Setting<>("Direction", "", this, true);

    public Coordinates() {
        super("Coordinates", "Shows your coordinates.", 2, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
        String s = String.format("XYZ %s%.1f, %.1f, %.1f", ChatFormatting.WHITE, x, y, z);
        if (nether.on()) {
            boolean inNether = mc.level.dimension() == Level.NETHER;
            double f = inNether ? 8 : 0.125;
            s += String.format(" %s[%.1f, %.1f]", ChatFormatting.GRAY, x * f, z * f);
        }
        int yy = 0;
        if (direction.on()) {
            String dir = mc.player.getDirection().getName();
            String axis = switch (mc.player.getDirection()) {
                case NORTH -> "-Z";
                case SOUTH -> "+Z";
                case EAST -> "+X";
                default -> "-X";
            };
            String d = Character.toUpperCase(dir.charAt(0)) + dir.substring(1) + ChatFormatting.GRAY + " [" + axis + "]";
            g.text(mc.font, d, 0, 0, accent(), true);
            yy = 10;
        }
        g.text(mc.font, s, 0, yy, accent(), true);
        width = mc.font.width(s);
        height = yy + 9;
    }
}
