package me.ciruu.abyss.modules.render;

import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class NoRender extends Module {
    public final Setting<Boolean> fire = new Setting<>("Fire", "", this, true);
    public final Setting<Boolean> portal = new Setting<>("Portal", "", this, true);
    public final Setting<Boolean> overlays = new Setting<>("Pumpkin", "Pumpkin and powder snow overlays", this, true);
    public final Setting<Boolean> items = new Setting<>("Items", "Removes dropped items client side", this, false);
    public final Setting<Boolean> nausea = new Setting<>("Nausea", "", this, true);
    public final Setting<Boolean> hurtCam = new Setting<>("HurtCam", "", this, true);
    public final Setting<Boolean> weather = new Setting<>("Weather", "", this, true);
    public final Setting<Boolean> bossBar = new Setting<>("BossBars", "", this, true);
    public final Setting<Boolean> vignette = new Setting<>("Vignette", "", this, true);
    public final Setting<Boolean> potionIcons = new Setting<>("PotionIcons", "", this, false);
    public final Setting<Boolean> explosions = new Setting<>("Explosion", "Explosion particles", this, true);

    public NoRender() {
        super("NoRender", "Prevents to render certain things.", Category.RENDER);
    }

    public static NoRender get() {
        return Abyss.moduleManager == null ? null : Abyss.moduleManager.get(NoRender.class);
    }

    public static boolean on(java.util.function.Function<NoRender, Setting<Boolean>> setting) {
        NoRender module = get();
        return module != null && module.isEnabled() && setting.apply(module).on();
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (items.on()) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof ItemEntity) {
                    entity.discard();
                }
            }
        }
    });
}
