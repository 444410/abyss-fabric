package me.ciruu.abyss.settings;

import java.awt.Color;
import java.util.function.BooleanSupplier;
import me.ciruu.abyss.modules.Module;

public class Setting<T> {
    private final String name;
    private final String description;
    private final Module module;
    private final T defaultValue;
    private T value;
    private T min;
    private T max;
    private Setting<SubMenu> parent;
    private BooleanSupplier visibility;

    public Setting(String name, String description, Module module, T value) {
        this.name = name;
        this.description = description;
        this.module = module;
        this.defaultValue = value;
        this.value = value;
        if (module != null) {
            module.addSetting(this);
        }
    }

    public Setting(String name, String description, Module module, T value, T min, T max) {
        this(name, description, module, value);
        this.min = min;
        this.max = max;
    }

    public Setting<T> parent(Setting<SubMenu> parent) {
        this.parent = parent;
        return this;
    }

    public Setting<T> visible(BooleanSupplier visibility) {
        this.visibility = visibility;
        return this;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        if (min != null && max != null && value instanceof Number n) {
            value = clamp(n);
        }
        this.value = (T) value;
    }

    @SuppressWarnings("unchecked")
    private Object clamp(Number n) {
        if (value instanceof Integer) {
            return Math.max(((Number) min).intValue(), Math.min(((Number) max).intValue(), n.intValue()));
        }
        if (value instanceof Float) {
            return Math.max(((Number) min).floatValue(), Math.min(((Number) max).floatValue(), n.floatValue()));
        }
        if (value instanceof Double) {
            return Math.max(((Number) min).doubleValue(), Math.min(((Number) max).doubleValue(), n.doubleValue()));
        }
        return n;
    }

    public T getDefault() {
        return defaultValue;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Module getModule() {
        return module;
    }

    public Setting<SubMenu> getParent() {
        return parent;
    }

    public boolean isVisible() {
        return visibility == null || visibility.getAsBoolean();
    }

    public boolean isNumber() {
        return value instanceof Number && min != null;
    }

    public boolean isEnum() {
        return value instanceof Enum<?>;
    }

    public void cycle(boolean backwards) {
        if (!(value instanceof Enum<?> current)) {
            return;
        }
        Object[] constants = current.getDeclaringClass().getEnumConstants();
        int i = current.ordinal() + (backwards ? -1 : 1);
        setValue(constants[(i + constants.length) % constants.length]);
    }

    public boolean setEnumByName(String name) {
        if (!(value instanceof Enum<?> current)) {
            return false;
        }
        for (Object constant : current.getDeclaringClass().getEnumConstants()) {
            if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
                setValue(constant);
                return true;
            }
        }
        return false;
    }

    public boolean on() {
        return (Boolean) value;
    }

    public int i() {
        return ((Number) value).intValue();
    }

    public float f() {
        return ((Number) value).floatValue();
    }

    public double d() {
        return ((Number) value).doubleValue();
    }

    public Color color() {
        return (Color) value;
    }

    public int argb() {
        return ((Color) value).getRGB();
    }

    public boolean is(Enum<?> constant) {
        return value == constant;
    }
}
