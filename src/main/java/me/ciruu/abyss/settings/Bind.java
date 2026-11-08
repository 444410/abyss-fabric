package me.ciruu.abyss.settings;

import me.ciruu.abyss.util.KeyUtil;

public final class Bind {
    private int key;

    public Bind(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return KeyUtil.name(key);
    }
}
