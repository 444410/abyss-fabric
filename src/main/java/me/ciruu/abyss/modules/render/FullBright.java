package me.ciruu.abyss.modules.render;

import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import org.joml.Vector3f;

public class FullBright extends Module {
    public enum Mode {
        Gamma, Potion
    }

    public final Setting<Mode> mode = new Setting<>("Mode", "", this, Mode.Gamma);
    private MobEffectInstance given;

    public FullBright() {
        super("FullBright", "Makes everything bright.", Category.RENDER);
    }

    public void apply(LightmapRenderState state) {
        if (mode.getValue() == Mode.Gamma) {
            state.ambientColor = new Vector3f(1f, 1f, 1f);
            state.brightness = Math.max(state.brightness, 100f);
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (mode.getValue() != Mode.Potion) {
            removeEffect();
            return;
        }
        if (mc.player.getEffect(MobEffects.NIGHT_VISION) == null) {
            given = new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0, false, false, false);
            mc.player.addEffect(given);
        }
    });

    @Override
    protected void onDisable() {
        removeEffect();
    }

    private void removeEffect() {
        if (given != null && mc.player != null && mc.player.getEffect(MobEffects.NIGHT_VISION) == given) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
        given = null;
    }
}
