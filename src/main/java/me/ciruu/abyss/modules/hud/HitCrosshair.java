package me.ciruu.abyss.modules.hud;

import me.ciruu.abyss.events.EventNetworkPrePacketEvent;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.util.ColorUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;

public class HitCrosshair extends Module {
    private long lastHit;

    public HitCrosshair() {
        super("HitCrosshair", "Shows a hitmarker when you hit something.", Category.HUD);
    }

    @EventHandler
    private final Listener<EventNetworkPrePacketEvent> packets = new Listener<>(EventNetworkPrePacketEvent.class, event -> {
        if (event.getPacket() instanceof ServerboundAttackPacket) {
            lastHit = System.currentTimeMillis();
        }
    });

    @EventHandler
    private final Listener<EventRender2D> render = new Listener<>(EventRender2D.class, event -> {
        long age = System.currentTimeMillis() - lastHit;
        if (age > 300) {
            return;
        }
        GuiGraphicsExtractor g = event.graphics();
        int cx = g.guiWidth() / 2;
        int cy = g.guiHeight() / 2;
        int color = ColorUtil.withAlpha(0xFFFFFF, (int) (255 * (1 - age / 300f)));
        for (int i = 3; i < 7; i++) {
            g.fill(cx - i - 1, cy - i - 1, cx - i, cy - i, color);
            g.fill(cx + i, cy - i - 1, cx + i + 1, cy - i, color);
            g.fill(cx - i - 1, cy + i, cx - i, cy + i + 1, color);
            g.fill(cx + i, cy + i, cx + i + 1, cy + i + 1, color);
        }
    });
}
