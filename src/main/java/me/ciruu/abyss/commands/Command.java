package me.ciruu.abyss.commands;

import me.ciruu.abyss.Globals;

public abstract class Command implements Globals {
    private final String[] names;
    private final String syntax;
    private final String description;

    protected Command(String syntax, String description, String... names) {
        this.names = names;
        this.syntax = syntax;
        this.description = description;
    }

    public abstract void run(String[] args);

    public String[] getNames() {
        return names;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }
}
