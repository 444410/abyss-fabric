package me.ciruu.abyss.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventSettingsLoaded;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Bind;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.settings.SubMenu;
import me.ciruu.abyss.util.KeyUtil;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String profile = "default";

    public String getProfile() {
        return profile;
    }

    private Path features(String name) {
        return Abyss.directory().resolve("Features").resolve(name);
    }

    public List<String> profiles() {
        List<String> out = new ArrayList<>();
        try (var stream = Files.list(Abyss.directory().resolve("Features"))) {
            stream.filter(Files::isDirectory).forEach(p -> out.add(p.getFileName().toString()));
        } catch (IOException ignored) {
        }
        return out;
    }

    public void load() {
        try {
            Path file = Abyss.directory().resolve("profile.txt");
            if (Files.exists(file)) {
                profile = Files.readString(file).trim();
            }
        } catch (IOException ignored) {
        }
        loadFriends();
        loadProfile(profile);
    }

    public void loadProfile(String name) {
        profile = name;
        for (Module module : Abyss.moduleManager.getModules()) {
            Path file = features(name).resolve(module.getName() + ".json");
            if (!Files.exists(file)) {
                continue;
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                fromJson(module, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (Exception e) {
                Abyss.LOGGER.warn("Could not load {}", file, e);
            }
        }
        Abyss.EVENT_BUS.post(new EventSettingsLoaded());
    }

    public void save() {
        saveProfile(profile);
        saveFriends();
        try {
            Files.writeString(Abyss.directory().resolve("profile.txt"), profile);
        } catch (IOException ignored) {
        }
    }

    public void saveProfile(String name) {
        profile = name;
        try {
            Files.createDirectories(features(name));
        } catch (IOException e) {
            Abyss.LOGGER.warn("Could not create config folder", e);
            return;
        }
        for (Module module : Abyss.moduleManager.getModules()) {
            try (Writer writer = Files.newBufferedWriter(features(name).resolve(module.getName() + ".json"))) {
                GSON.toJson(toJson(module), writer);
            } catch (IOException e) {
                Abyss.LOGGER.warn("Could not save {}", module.getName(), e);
            }
        }
    }

    private void loadFriends() {
        Path file = Abyss.directory().resolve("friends.json");
        if (!Files.exists(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            for (JsonElement element : JsonParser.parseReader(reader).getAsJsonArray()) {
                Abyss.friendManager.add(element.getAsString());
            }
        } catch (Exception e) {
            Abyss.LOGGER.warn("Could not load friends", e);
        }
    }

    private void saveFriends() {
        JsonArray array = new JsonArray();
        Abyss.friendManager.getFriends().forEach(array::add);
        try {
            Files.createDirectories(Abyss.directory());
            Files.writeString(Abyss.directory().resolve("friends.json"), GSON.toJson(array));
        } catch (IOException e) {
            Abyss.LOGGER.warn("Could not save friends", e);
        }
    }

    private static JsonObject toJson(Module module) {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", !module.getName().equals("ClickGUI") && module.isEnabled());
        json.addProperty("display", module.getDisplayName());
        json.addProperty("keybind", KeyUtil.name(module.getKey()));
        json.addProperty("hidden", module.isHidden());
        JsonObject settings = new JsonObject();
        for (Setting<?> setting : module.getSettings()) {
            Object value = setting.getValue();
            if (value instanceof SubMenu) {
                continue;
            }
            JsonObject entry = new JsonObject();
            String type;
            JsonElement element;
            if (value instanceof Enum<?> e) {
                type = "Enum";
                element = new JsonPrimitive(e.name());
            } else if (value instanceof Color c) {
                type = "Color";
                JsonArray array = new JsonArray();
                array.add(c.getRed());
                array.add(c.getGreen());
                array.add(c.getBlue());
                array.add(c.getAlpha());
                element = array;
            } else if (value instanceof Bind b) {
                type = "Bind";
                element = new JsonPrimitive(b.getKey());
            } else if (value instanceof Number n) {
                type = value.getClass().getSimpleName();
                element = new JsonPrimitive(n);
            } else if (value instanceof Boolean b) {
                type = "Boolean";
                element = new JsonPrimitive(b);
            } else {
                type = "String";
                element = new JsonPrimitive(String.valueOf(value));
            }
            entry.addProperty("type", type);
            entry.add("value", element);
            settings.add(setting.getName(), entry);
        }
        json.add("settings", settings);
        return json;
    }

    private static void fromJson(Module module, JsonObject json) {
        if (json.has("display")) {
            module.setDisplayName(json.get("display").getAsString());
        }
        if (json.has("keybind")) {
            module.setKey(KeyUtil.parse(json.get("keybind").getAsString()));
        }
        if (json.has("hidden")) {
            module.setHidden(json.get("hidden").getAsBoolean());
        }
        if (json.has("settings")) {
            JsonObject settings = json.getAsJsonObject("settings");
            for (Setting<?> setting : module.getSettings()) {
                if (!settings.has(setting.getName())) {
                    continue;
                }
                try {
                    JsonElement value = settings.getAsJsonObject(setting.getName()).get("value");
                    Object current = setting.getValue();
                    if (current instanceof Enum<?>) {
                        if (value.getAsJsonPrimitive().isNumber()) {
                            Object[] constants = ((Enum<?>) current).getDeclaringClass().getEnumConstants();
                            setting.setValue(constants[value.getAsInt()]);
                        } else {
                            setting.setEnumByName(value.getAsString());
                        }
                    } else if (current instanceof Color) {
                        JsonArray a = value.getAsJsonArray();
                        setting.setValue(new Color(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt(), a.get(3).getAsInt()));
                    } else if (current instanceof Bind) {
                        setting.setValue(new Bind(value.getAsInt()));
                    } else if (current instanceof Integer) {
                        setting.setValue(value.getAsInt());
                    } else if (current instanceof Float) {
                        setting.setValue(value.getAsFloat());
                    } else if (current instanceof Double) {
                        setting.setValue(value.getAsDouble());
                    } else if (current instanceof Boolean) {
                        setting.setValue(value.getAsBoolean());
                    } else if (current instanceof String) {
                        setting.setValue(value.getAsString());
                    }
                } catch (Exception e) {
                    setting.setValue(setting.getDefault());
                }
            }
        }
        if (json.has("enabled")) {
            module.setEnabledSilently(json.get("enabled").getAsBoolean());
        }
    }
}
