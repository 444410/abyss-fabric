package me.ciruu.abyss.util;

public final class Timer {
    private long last = System.currentTimeMillis();

    public boolean passed(long ms) {
        return System.currentTimeMillis() - last >= ms;
    }

    public void reset() {
        last = System.currentTimeMillis();
    }

    public long elapsed() {
        return System.currentTimeMillis() - last;
    }
}
