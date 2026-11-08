package me.ciruu.abyss.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.client.ClickGUI;
import me.ciruu.abyss.modules.hud.HudModule;
import me.ciruu.abyss.settings.Bind;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.settings.SubMenu;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.GuiFont;
import me.ciruu.abyss.util.KeyUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;

public class ClickGuiScreen extends Screen implements Globals {
    private static final int HEADER = 15;
    private static final int ROW = 12;
    private static final int GAP = 1;

    private final List<Panel> panels = new ArrayList<>();
    private Panel dragging;
    private HudModule draggingHud;
    private int hudDragX;
    private int hudDragY;
    private int dragX;
    private int dragY;
    private Slider sliding;
    private Setting<?> listening;
    private Setting<?> typing;
    private Module bindingModule;
    private double clickY;
    private final List<Hit> hits = new ArrayList<>();

    private record Hit(int x, int y, int w, int h, Click action) {
        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    @FunctionalInterface
    private interface Click {
        void click(int button, double mx);
    }

    private record Slider(Setting<?> setting, int channel, int x, int w) {
    }

    private static final class Panel {
        final Category category;
        int x;
        int y;
        boolean open = true;
        final List<Module> expanded = new ArrayList<>();
        final List<Setting<?>> openColors = new ArrayList<>();

        Panel(Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
        }
    }

    public ClickGuiScreen() {
        super(Component.literal("Abyss ClickGUI"));
        int x = 4;
        for (Category category : Category.values()) {
            panels.add(new Panel(category, x, 4));
            x += 98;
        }
    }

    private ClickGUI module() {
        return Abyss.moduleManager.get(ClickGUI.class);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
        ClickGUI gui = module();
        if (gui.background.on()) {
            if (gui.backgroundGradient.on()) {
                g.fillGradient(0, 0, width, height, gui.bgTop.argb(), gui.bgBottom.argb());
            } else {
                g.fill(0, 0, width, height, gui.bgColor.argb());
            }
        }
    }

    private int panelWidth() {
        return module().width.i();
    }

    private static void label(GuiGraphicsExtractor g, String text, int x, int rowY, int color) {
        GuiFont.draw(g, text, x, rowY + (ROW - 8) / 2 + 1, color, true);
    }

    private static void labelRight(GuiGraphicsExtractor g, String text, int right, int rowY, int color) {
        label(g, text, right - GuiFont.width(text), rowY, color);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
        super.extractRenderState(g, mouseX, mouseY, a);
        hits.clear();
        if (mc.player != null) {
            if (draggingHud != null) {
                draggingHud.x.setValue(mouseX - hudDragX);
                draggingHud.y.setValue(mouseY - hudDragY);
            }
            Abyss.hudManager.renderAll(g, a);
            for (var m : Abyss.moduleManager.getModules()) {
                if (m instanceof HudModule hud && hud.isEnabled()) {
                    g.outline(hud.posX(g) - 1, hud.posY(g) - 1, hud.width + 2, hud.height + 2, draggingHud == hud ? 0xFFFFFFFF : 0x60FFFFFF);
                }
            }
        }
        ClickGUI gui = module();
        int frame = gui.frameColor.argb();
        int font = gui.fontColor.argb();
        int w = panelWidth();
        if (sliding != null) {
            applySlider(mouseX);
        }
        if (dragging != null) {
            dragging.x = mouseX - dragX;
            dragging.y = mouseY - dragY;
        }
        for (Panel panel : panels) {
            int x = panel.x;
            int y = panel.y;

            g.fill(x, y, x + w, y + HEADER, ColorUtil.withAlpha(frame, Math.min(255, (frame >>> 24) + 90)));
            GuiFont.centered(g, panel.category.toString().toUpperCase(java.util.Locale.ROOT), x + w / 2, y + (HEADER - 8) / 2 + 1, font);
            Panel p = panel;
            hits.add(new Hit(x, y, w, HEADER, (button, mx) -> {
                if (button == 0) {
                    dragging = p;
                    dragX = (int) mx - p.x;
                    dragY = (int) clickY - p.y;
                } else if (button == 1) {
                    p.open = !p.open;
                }
            }));
            if (!panel.open) {
                continue;
            }
            int cy = y + HEADER + GAP;
            int index = 0;
            for (Module mod : Abyss.moduleManager.getModulesInCategory(panel.category)) {
                boolean expanded = panel.expanded.contains(mod);
                int bg = mod.isEnabled() ? gui.enabledColor(panel.category.ordinal() * 3 + index) : frame;
                g.fill(x, cy, x + w, cy + ROW, bg);
                boolean hover = mouseX >= x && mouseX < x + w && mouseY >= cy && mouseY < cy + ROW;
                if (hover) {
                    g.fill(x, cy, x + w, cy + ROW, 0x25FFFFFF);
                    if (gui.descriptions.on() && !mod.getDescription().isEmpty()) {
                        g.setTooltipForNextFrame(Component.literal(mod.getDescription()), mouseX, mouseY);
                    }
                }
                label(g, bindingModule == mod ? "Press a key..." : mod.getName(), x + 4, cy, font);
                {
                    labelRight(g, expanded ? "v" : ">", x + w - 4, cy, font);
                }
                hits.add(new Hit(x, cy, w, ROW, (button, mx) -> {
                    if (button == 0) {
                        mod.toggle();
                    } else if (button == 1) {
                        if (!p.expanded.remove(mod)) {
                            p.expanded.add(mod);
                        }
                    } else if (button == 2) {
                        bindingModule = mod;
                    }
                }));
                cy += ROW + GAP;
                if (expanded) {
                    cy = settings(g, panel, mod, x, cy, w, mouseX, mouseY, index);
                }
                index++;
            }
        }
    }

    private int settings(GuiGraphicsExtractor g, Panel panel, Module mod, int x, int y, int w, int mouseX, int mouseY, int index) {
        ClickGUI gui = module();
        int frame = gui.frameColor.argb();
        int font = gui.fontColor.argb();
        int accent = gui.enabledColor(panel.category.ordinal() * 3 + index);
        int slider = ColorUtil.withAlpha(gui.typingSliderColor.argb(), 170);
        int gray = 0xFFAAAAAA;
        for (Setting<?> setting : mod.getSettings()) {
            if (!setting.isVisible()) {
                continue;
            }
            Setting<SubMenu> parent = setting.getParent();
            if (parent != null && !parent.getValue().isOpen()) {
                continue;
            }
            int tx = x + 6;
            Object value = setting.getValue();
            g.fill(x, y, x + w, y + ROW, frame);
            if (value instanceof SubMenu menu) {
                label(g, (menu.isOpen() ? "- " : "+ ") + menu.getName(), x + 4, y, font);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> menu.toggle()));
                y += ROW + GAP;
                continue;
            }
            if (value instanceof Boolean b) {
                if (b) {
                    g.fill(x, y, x + w, y + ROW, accent);
                }
                label(g, "> " + setting.getName(), tx, y, font);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> setting.setValue(!(Boolean) setting.getValue())));
            } else if (setting.isNumber()) {
                double min = ((Number) setting.getMin()).doubleValue();
                double max = ((Number) setting.getMax()).doubleValue();
                double v = ((Number) value).doubleValue();
                int fill = (int) (w * Math.max(0, Math.min(1, (v - min) / (max - min))));
                g.fill(x, y, x + fill, y + ROW, slider);
                String text = value instanceof Integer ? String.valueOf(value) : String.format("%.3f", v);
                label(g, "> " + setting.getName() + ": " + text, tx, y, font);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> {
                    sliding = new Slider(setting, -1, x, w);
                    applySlider(mx);
                }));
            } else if (value instanceof Enum<?> e) {
                label(g, "> " + setting.getName(), tx, y, font);
                labelRight(g, e.name(), x + w - 4, y, gray);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> setting.cycle(button == 1)));
            } else if (value instanceof Bind bind) {
                label(g, "> " + setting.getName() + ": " + (listening == setting ? "..." : bind.toString()), tx, y, font);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> listening = setting));
            } else if (value instanceof String s) {
                String shown = typing == setting ? s + (System.currentTimeMillis() / 500 % 2 == 0 ? "_" : "") : s;
                label(g, "> " + setting.getName() + ": " + shown, tx, y, font);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> typing = typing == setting ? null : setting));
            } else if (value instanceof Color c) {
                label(g, "> " + setting.getName(), tx, y, font);
                g.fill(x + w - 13, y + 2, x + w - 3, y + ROW - 2, c.getRGB() | 0xFF000000);
                g.outline(x + w - 13, y + 2, 10, ROW - 4, 0xFF000000);
                hits.add(new Hit(x, y, w, ROW, (button, mx) -> {
                    if (!panel.openColors.remove(setting)) {
                        panel.openColors.add(setting);
                    }
                }));
                if (panel.openColors.contains(setting)) {
                    String[] names = {"Red", "Green", "Blue", "Alpha"};
                    int[] parts = {c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()};
                    for (int i = 0; i < 4; i++) {
                        y += ROW + GAP;
                        int channel = i;
                        g.fill(x, y, x + w, y + ROW, frame);
                        g.fill(x, y, x + w * parts[i] / 255, y + ROW, slider);
                        label(g, "  " + names[i] + ": " + parts[i], tx, y, font);
                        hits.add(new Hit(x, y, w, ROW, (button, mx) -> {
                            sliding = new Slider(setting, channel, x, w);
                            applySlider(mx);
                        }));
                    }
                }
            }
            if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + ROW && gui.descriptions.on() && !setting.getDescription().isEmpty()) {
                g.setTooltipForNextFrame(Component.literal(setting.getDescription()), mouseX, mouseY);
            }
            y += ROW + GAP;
        }

        g.fill(x, y, x + w, y + ROW, frame);
        label(g, "> Key: " + (bindingModule == mod ? "..." : KeyUtil.name(mod.getKey())), x + 6, y, font);
        hits.add(new Hit(x, y, w, ROW, (button, mx) -> bindingModule = mod));
        return y + ROW + GAP;
    }

    private void applySlider(double mx) {
        Setting<?> setting = sliding.setting();
        double ratio = Math.max(0, Math.min(1, (mx - sliding.x()) / sliding.w()));
        if (sliding.channel() >= 0) {
            Color c = (Color) setting.getValue();
            int[] parts = {c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()};
            parts[sliding.channel()] = (int) Math.round(ratio * 255);
            setting.setValue(new Color(parts[0], parts[1], parts[2], parts[3]));
            return;
        }
        double min = ((Number) setting.getMin()).doubleValue();
        double max = ((Number) setting.getMax()).doubleValue();
        double v = min + (max - min) * ratio;
        Object current = setting.getValue();
        if (current instanceof Integer) {
            setting.setValue((int) Math.round(v));
        } else if (current instanceof Float) {
            setting.setValue((float) (Math.round(v * 100) / 100.0));
        } else {
            setting.setValue(Math.round(v * 100) / 100.0);
        }
    }

    private net.minecraft.client.gui.Font font() {
        return mc.font;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();
        clickY = my;
        if (bindingModule != null) {
            bindingModule = null;
        }
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit hit = hits.get(i);
            if (hit.contains(mx, my)) {
                hit.action().click(event.button(), mx);
                return true;
            }
        }
        typing = null;
        if (event.button() == 0 && mc.player != null) {
            var list = new java.util.ArrayList<>(Abyss.moduleManager.getModules());
            java.util.Collections.reverse(list);
            int gw = mc.getWindow().getGuiScaledWidth();
            int gh = mc.getWindow().getGuiScaledHeight();
            for (var m : list) {
                if (m instanceof HudModule hud && hud.isEnabled()) {
                    int hx = Math.max(0, Math.min(gw - hud.width, hud.x.i() < 0 ? gw - hud.width - 2 : hud.x.i()));
                    int hy = Math.max(0, Math.min(gh - hud.height, hud.y.i() < 0 ? gh - hud.height - 2 : hud.y.i()));
                    if (mx >= hx && mx <= hx + hud.width && my >= hy && my <= hy + hud.height) {
                        draggingHud = hud;
                        hud.x.setValue(hx);
                        hud.y.setValue(hy);
                        hudDragX = (int) mx - hx;
                        hudDragY = (int) my - hy;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = null;
        draggingHud = null;
        sliding = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        for (Panel panel : panels) {
            panel.y += (int) (scrollY * 12);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();
        if (bindingModule != null) {
            bindingModule.setKey(key == InputConstants.KEY_ESCAPE || key == InputConstants.KEY_DELETE || key == InputConstants.KEY_BACKSPACE ? -1 : key);
            bindingModule = null;
            return true;
        }
        if (listening != null) {
            listening.setValue(new Bind(key == InputConstants.KEY_ESCAPE || key == InputConstants.KEY_DELETE ? -1 : key));
            listening = null;
            return true;
        }
        if (typing != null) {
            String s = (String) typing.getValue();
            if (key == InputConstants.KEY_BACKSPACE && !s.isEmpty()) {
                typing.setValue(s.substring(0, s.length() - 1));
            } else if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_ESCAPE) {
                typing = null;
            }
            return true;
        }
        ClickGUI gui = module();
        if (key == gui.getKey() && key != -1) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (typing != null && event.isAllowedChatCharacter()) {
            typing.setValue(typing.getValue() + event.codepointAsString());
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        ClickGUI gui = module();
        if (gui.isEnabled()) {
            gui.setEnabled(false);
        }
        super.onClose();
    }

    @Override
    public void removed() {
        ClickGUI gui = module();
        if (gui.saveOnClose.on()) {
            Abyss.configManager.save();
        }
        if (gui.isEnabled()) {
            gui.setEnabled(false);
        }
    }
}
