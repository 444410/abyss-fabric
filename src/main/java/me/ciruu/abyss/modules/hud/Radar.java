package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.settings.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class Radar extends HudModule {
    private final Setting<Integer> size = new Setting<>("Size", "", this, 80, 40, 200);
    private final Setting<Float> range = new Setting<>("Range", "Blocks to the edge", this, 64f, 8f, 256f);

    public Radar() {
        super("Radar", "Shows nearby players from above.", 2, 120);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        int s = size.i();
        width = s;
        height = s;
        g.fill(0, 0, s, s, 0x90000000);
        g.outline(0, 0, s, s, accent());
        int c = s / 2;
        g.fill(c - 1, c - 1, c + 1, c + 1, 0xFFFFFFFF);
        double yaw = Math.toRadians(mc.player.getYRot());
        double cos = Math.cos(yaw), sin = Math.sin(yaw);
        for (Player p : mc.level.players()) {
            if (p == mc.player) {
                continue;
            }
            double dx = p.getX() - mc.player.getX();
            double dz = p.getZ() - mc.player.getZ();
            double rx = dx * cos + dz * sin;
            double rz = dz * cos - dx * sin;
            int px = c + (int) (-rx / range.f() * c);
            int pz = c + (int) (-rz / range.f() * c);
            if (px < 1 || pz < 1 || px > s - 2 || pz > s - 2) {
                continue;
            }
            int color = Abyss.friendManager.isFriend(p) ? 0xFF55FFFF : 0xFFFF5555;
            g.fill(px - 1, pz - 1, px + 2, pz + 2, color);
        }
    }
}
