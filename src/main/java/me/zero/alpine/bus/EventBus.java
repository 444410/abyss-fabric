package me.zero.alpine.bus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

public class EventBus {
    private final Map<Listenable, List<Listener<?>>> cache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Listener<?>>> map = new ConcurrentHashMap<>();

    public void subscribe(Listenable listenable) {
        List<Listener<?>> listeners = cache.computeIfAbsent(listenable, EventBus::collect);
        for (Listener<?> listener : listeners) {
            subscribe(listener);
        }
    }

    public void subscribe(Listener<?> listener) {
        List<Listener<?>> list = map.computeIfAbsent(listener.getTarget(), t -> new CopyOnWriteArrayList<>());
        if (list.contains(listener)) {
            return;
        }
        int index = 0;
        while (index < list.size() && listener.getPriority() <= list.get(index).getPriority()) {
            index++;
        }
        list.add(index, listener);
    }

    public void unsubscribe(Listenable listenable) {
        List<Listener<?>> listeners = cache.get(listenable);
        if (listeners == null) {
            return;
        }
        for (List<Listener<?>> list : map.values()) {
            list.removeAll(listeners);
        }
    }

    public void unsubscribe(Listener<?> listener) {
        List<Listener<?>> list = map.get(listener.getTarget());
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T post(T event) {
        List<Listener<?>> list = map.get(event.getClass());
        if (list != null) {
            for (Listener listener : list) {
                listener.invoke(event);
            }
        }
        return event;
    }

    private static List<Listener<?>> collect(Listenable listenable) {
        List<Listener<?>> out = new ArrayList<>();
        Class<?> type = listenable.getClass();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAnnotationPresent(EventHandler.class) || !Listener.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object value = field.get(listenable);
                    if (value != null) {
                        out.add((Listener<?>) value);
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            type = type.getSuperclass();
        }
        return out;
    }
}
