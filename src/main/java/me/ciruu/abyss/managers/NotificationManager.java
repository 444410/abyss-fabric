package me.ciruu.abyss.managers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.modules.hud.Notifications;
import me.ciruu.abyss.util.ColorUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class NotificationManager implements Listenable, Globals {
    private record Entry(String text, long start) {
    }

    private final List<Entry> entries = new CopyOnWriteArrayList<>();

    public void push(String text) {
        entries.add(new Entry(text, System.currentTimeMillis()));
        while (entries.size() > 8) {
            entries.remove(0);
        }
    }

    @EventHandler
    private final Listener<EventRender2D> render = new Listener<>(EventRender2D.class, event -> {
        Notifications module = Abyss.moduleManager.get(Notifications.class);
        if (module == null || !module.isEnabled() || entries.isEmpty()) {
            return;
        }
        GuiGraphicsExtractor g = event.graphics();
        long now = System.currentTimeMillis();
        long life = (long) (module.duration.f() * 1000f);
        int y = g.guiHeight() - 4;
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry entry = entries.get(i);
            long age = now - entry.start();
            if (age > life) {
                entries.remove(entry);
                continue;
            }
            int w = mc.font.width(entry.text()) + 10;
            float slide = Math.min(1f, age / 150f) * Math.min(1f, (life - age) / 150f);
            int x = g.guiWidth() - (int) (w * slide) - 2;
            y -= 16;
            g.fill(x, y, x + w, y + 14, ColorUtil.argb(170, 15, 15, 15));
            g.fill(x, y + 13, x + (int) (w * (1f - age / (float) life)), y + 14, ColorUtil.accent());
            g.text(mc.font, entry.text(), x + 5, y + 3, 0xFFFFFFFF, true);
        }
    });
}
