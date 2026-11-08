package me.ciruu.abyss.settings;

public final class SubMenu {
    private final String name;
    private boolean open;

    public SubMenu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return open;
    }

    public void toggle() {
        open = !open;
    }

    @Override
    public String toString() {
        return name;
    }
}
