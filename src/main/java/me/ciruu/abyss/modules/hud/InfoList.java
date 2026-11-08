package me.ciruu.abyss.modules.hud;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.managers.TpsManager;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.MovementUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.effect.MobEffectInstance;

public class InfoList extends HudModule {
    private final Setting<Boolean> fps = new Setting<>("FPS", "", this, true);
    private final Setting<Boolean> ping = new Setting<>("Ping", "", this, true);
    private final Setting<Boolean> tps = new Setting<>("TPS", "", this, true);
    private final Setting<Boolean> speed = new Setting<>("Speed", "", this, true);
    private final Setting<Boolean> potions = new Setting<>("Potions", "", this, true);
    private final Setting<Boolean> durability = new Setting<>("Durability", "Held item durability", this, false);

    public InfoList() {
        super("InfoList", "FPS, ping, TPS, speed and potions.", -1, -1);
    }

    @Override
    public void draw(GuiGraphicsExtractor g, float partialTicks) {
        List<String> lines = new ArrayList<>();
        if (potions.on()) {
            for (MobEffectInstance effect : mc.player.getActiveEffects()) {
                int secs = effect.getDuration() / 20;
                lines.add(effect.getEffect().value().getDisplayName().getString() + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "")
                        + ChatFormatting.WHITE + " " + (effect.isInfiniteDuration() ? "**:**" : String.format("%d:%02d", secs / 60, secs % 60)));
            }
        }
        if (durability.on() && mc.player.getMainHandItem().isDamageableItem()) {
            var stack = mc.player.getMainHandItem();
            lines.add("Durability " + ChatFormatting.WHITE + (stack.getMaxDamage() - stack.getDamageValue()));
        }
        if (speed.on()) {
            lines.add("Speed " + ChatFormatting.WHITE + String.format("%.1f", MovementUtil.speed() * 20 * 3.6) + "km/h");
        }
        if (tps.on()) {
            lines.add("TPS " + ChatFormatting.WHITE + String.format("%.1f", TpsManager.tps()));
        }
        if (ping.on() && mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            lines.add("Ping " + ChatFormatting.WHITE + (info == null ? 0 : info.getLatency()) + "ms");
        }
        if (fps.on()) {
            lines.add("FPS " + ChatFormatting.WHITE + mc.getFps());
        }
        boolean right = rightSide(g);
        int w = 30;
        for (String s : lines) {
            w = Math.max(w, mc.font.width(s));
        }
        width = w;
        height = Math.max(9, lines.size() * 10);
        int yy = 0;
        for (String s : lines) {
            g.text(mc.font, s, right ? w - mc.font.width(s) : 0, yy, accent(), true);
            yy += 10;
        }
    }
}
