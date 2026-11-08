package me.ciruu.abyss.modules;

public enum Category {
    COMBAT("Combat"),
    EXPLOIT("Exploit"),
    MISC("Misc"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    HUD("Hud"),
    CLIENT("Client");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
