package me.ciruu.abyss.modules.client;

import java.awt.Color;
import me.ciruu.abyss.gui.ClickGuiScreen;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.settings.SubMenu;
import me.ciruu.abyss.util.ColorUtil;
import com.mojang.blaze3d.platform.InputConstants;

public class ClickGUI extends Module {
    private final Setting<SubMenu> main = new Setting<>("Main", "", this, new SubMenu("Main Settings"));
    public final Setting<Color> frameColor = new Setting<>("FrameColor", "", this, new Color(40, 40, 50, 190)).parent(main);
    public final Setting<Color> enabledColor = new Setting<>("EnabledColor", "", this, new Color(106, 0, 193, 255)).parent(main);
    public final Setting<Color> fontColor = new Setting<>("FontColor", "", this, new Color(255, 255, 255, 255)).parent(main);
    public final Setting<Color> lineColor = new Setting<>("LineColor", "", this, new Color(142, 0, 255, 255)).parent(main);
    public final Setting<Color> typingSliderColor = new Setting<>("TypingSliderColor", "", this, new Color(160, 0, 160, 255)).parent(main);
    public final Setting<Boolean> saveOnClose = new Setting<>("SaveOnClose", "", this, true).parent(main);
    public final Setting<Boolean> descriptions = new Setting<>("Descriptions", "Show module descriptions on hover", this, true).parent(main);
    public final Setting<Integer> width = new Setting<>("Width", "", this, 94, 70, 170).parent(main);
    private final Setting<SubMenu> rainbow = new Setting<>("Rainbow", "", this, new SubMenu("Rainbow Settings"));
    public final Setting<Boolean> rainbowEnabled = new Setting<>("REnabled", "Rainbow colours for enabled modules", this, true).parent(rainbow);
    public final Setting<Float> rainbowSpeed = new Setting<>("RSpeedEnabled", "", this, 7f, 0.001f, 10f).parent(rainbow).visible(() -> rainbowEnabled.on());
    public final Setting<Float> rainbowSaturation = new Setting<>("RSatEnabled", "", this, 0.8f, 0f, 1f).parent(rainbow).visible(() -> rainbowEnabled.on());
    public final Setting<Float> rainbowBrightness = new Setting<>("RBrightEnabled", "", this, 0.6f, 0f, 1f).parent(rainbow).visible(() -> rainbowEnabled.on());
    public final Setting<Boolean> gradientEnabled = new Setting<>("GradientEnabled", "Each enabled module gets the next colour", this, true).parent(rainbow).visible(() -> rainbowEnabled.on());
    public final Setting<Float> enabledOffset = new Setting<>("EnabledOffset", "Colour step between modules", this, 7f, 2f, 20f).parent(rainbow).visible(() -> rainbowEnabled.on() && gradientEnabled.on());
    private final Setting<SubMenu> backgroundMenu = new Setting<>("BackgroundMenu", "", this, new SubMenu("Background Settings"));
    public final Setting<Boolean> background = new Setting<>("Background", "", this, false).parent(backgroundMenu);
    public final Setting<Boolean> backgroundGradient = new Setting<>("Gradient", "", this, true).parent(backgroundMenu).visible(() -> background.on());
    public final Setting<Color> bgColor = new Setting<>("BackgroundColor", "", this, new Color(0, 0, 0, 180)).parent(backgroundMenu).visible(() -> background.on() && !backgroundGradient.on());
    public final Setting<Color> bgTop = new Setting<>("BgColorTop", "", this, new Color(0, 0, 0, 0)).parent(backgroundMenu).visible(() -> background.on() && backgroundGradient.on());
    public final Setting<Color> bgBottom = new Setting<>("BgColorBottom", "", this, new Color(142, 0, 255, 180)).parent(backgroundMenu).visible(() -> background.on() && backgroundGradient.on());

    private ClickGuiScreen screen;

    public ClickGUI() {
        super("ClickGUI", "Opens the click GUI", Category.CLIENT);
        setKey(InputConstants.KEY_P);
    }

    public int enabledColor() {
        return enabledColor(0);
    }

    public int enabledColor(int index) {
        if (!rainbowEnabled.on()) {
            return enabledColor.argb();
        }
        long offset = gradientEnabled.on() ? (long) (index * enabledOffset.f() * 60) : 0;
        return ColorUtil.withAlpha(ColorUtil.rainbow(offset, rainbowSpeed.f() / 7f, rainbowSaturation.f(), rainbowBrightness.f()), 230);
    }

    @Override
    protected void onEnable() {
        if (screen == null) {
            screen = new ClickGuiScreen();
        }
        mc.gui.setScreen(screen);
    }
}
