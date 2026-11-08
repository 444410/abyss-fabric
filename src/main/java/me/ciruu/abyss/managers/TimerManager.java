package me.ciruu.abyss.managers;

public final class TimerManager {
    private static float speed = 1f;
    private static Object owner;

    private TimerManager() {
    }

    public static float get() {
        return speed;
    }

    public static void set(Object who, float value) {
        owner = who;
        speed = value;
    }

    public static void reset(Object who) {
        if (owner == who || who == null) {
            speed = 1f;
            owner = null;
        }
    }
}
