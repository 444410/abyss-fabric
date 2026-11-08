package me.ciruu.abyss.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.events.EventClientKeyDown;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

public class ModuleManager implements Listenable, Globals {
    private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();

    @EventHandler
    private final Listener<EventClientKeyDown> keyListener = new Listener<>(EventClientKeyDown.class, event -> {
        if (event.key() < 0) {
            return;
        }
        for (Module module : modules.values()) {
            if (module.getKey() == event.key()) {
                module.toggle();
            }
        }
    });

    public ModuleManager() {
        for (Module module : Modules.create()) {
            modules.put(module.getClass(), module);
        }
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> type) {
        return (T) modules.get(type);
    }

    public Module getModuleByName(String name) {
        for (Module module : modules.values()) {
            if (module.getName().equalsIgnoreCase(name) || module.getDisplayName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public boolean isModuleEnabled(Class<? extends Module> type) {
        Module module = modules.get(type);
        return module != null && module.isEnabled();
    }

    public List<Module> getModulesInCategory(Category category) {
        List<Module> out = new ArrayList<>();
        for (Module module : modules.values()) {
            if (module.getCategory() == category) {
                out.add(module);
            }
        }
        return out;
    }
}
