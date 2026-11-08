package me.zero.alpine.listener;

import java.util.function.Predicate;

public class Listener<T> implements EventHook<T> {
    private final Class<T> target;
    private final EventHook<T> hook;
    private final Predicate<T>[] filters;
    private final int priority;

    @SafeVarargs
    public Listener(Class<T> target, EventHook<T> hook, Predicate<T>... filters) {
        this(target, hook, 0, filters);
    }

    @SafeVarargs
    public Listener(Class<T> target, EventHook<T> hook, int priority, Predicate<T>... filters) {
        this.target = target;
        this.hook = hook;
        this.priority = priority;
        this.filters = filters;
    }

    public Class<T> getTarget() {
        return target;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void invoke(T event) {
        for (Predicate<T> filter : filters) {
            if (!filter.test(event)) {
                return;
            }
        }
        hook.invoke(event);
    }
}
