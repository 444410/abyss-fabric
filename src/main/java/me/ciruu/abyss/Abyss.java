package me.ciruu.abyss;

import java.nio.file.Path;
import me.ciruu.abyss.managers.CommandManager;
import me.ciruu.abyss.managers.ConfigManager;
import me.ciruu.abyss.managers.FriendManager;
import me.ciruu.abyss.managers.HudManager;
import me.ciruu.abyss.managers.TpsManager;
import me.ciruu.abyss.managers.ModuleManager;
import me.ciruu.abyss.managers.NotificationManager;
import me.ciruu.abyss.managers.RotationManager;
import me.ciruu.abyss.managers.TotemPopManager;
import me.zero.alpine.bus.EventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abyss implements ClientModInitializer {
    public static final String MOD_ID = "abyss";
    public static final String NAME = "Abyss";
    public static final String VERSION = "1.6.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
    public static final EventBus EVENT_BUS = new EventBus();

    public static ModuleManager moduleManager;
    public static FriendManager friendManager;
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static NotificationManager notificationManager;
    public static TotemPopManager totemPopManager;
    public static HudManager hudManager;

    public static Path directory() {
        return FabricLoader.getInstance().getGameDir().resolve("Abyss");
    }

    @Override
    public void onInitializeClient() {
    }

    private static boolean started;

    public static void start() {
        if (started) {
            return;
        }
        started = true;
        notificationManager = new NotificationManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        totemPopManager = new TotemPopManager();
        configManager = new ConfigManager();
        EVENT_BUS.subscribe(moduleManager);
        EVENT_BUS.subscribe(totemPopManager);
        EVENT_BUS.subscribe(notificationManager);
        EVENT_BUS.subscribe(new RotationManager());
        EVENT_BUS.subscribe(new TpsManager());
        hudManager = new HudManager();
        EVENT_BUS.subscribe(hudManager);
        configManager.load();
        Runtime.getRuntime().addShutdownHook(new Thread(configManager::save, "Abyss config save"));
        LOGGER.info("Abyss {} loaded {} modules", VERSION, moduleManager.getModules().size());
        if (Boolean.getBoolean("abyss.audit")) {
            org.spongepowered.asm.mixin.MixinEnvironment.getCurrentEnvironment().audit();
            LOGGER.info("Abyss mixin audit passed");
        }
    }
}
