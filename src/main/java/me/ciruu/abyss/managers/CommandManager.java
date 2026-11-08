package me.ciruu.abyss.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.commands.Command;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.modules.client.Alias;
import me.ciruu.abyss.modules.client.Client;
import me.ciruu.abyss.settings.Bind;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.settings.SubMenu;
import me.ciruu.abyss.util.ChatUtil;
import me.ciruu.abyss.util.KeyUtil;
import net.minecraft.ChatFormatting;

public class CommandManager implements Globals {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new Command("", "Lists commands", "help", "commands") {
            @Override
            public void run(String[] args) {
                for (Command command : commands) {
                    ChatUtil.send(ChatFormatting.WHITE + getPrefix() + command.getNames()[0] + " " + ChatFormatting.GRAY + command.getSyntax() + ChatFormatting.DARK_GRAY + " - " + command.getDescription());
                }
            }
        });
        commands.add(new Command("<module>", "Toggles a module", "toggle", "t") {
            @Override
            public void run(String[] args) {
                Module module = args.length > 0 ? Abyss.moduleManager.getModuleByName(args[0]) : null;
                if (module == null) {
                    ChatUtil.error("Unknown module.");
                    return;
                }
                module.toggle();
            }
        });
        commands.add(new Command("<module> <key|none>", "Binds a module to a key", "bind", "b") {
            @Override
            public void run(String[] args) {
                if (args.length < 2) {
                    ChatUtil.error("Usage: bind <module> <key>");
                    return;
                }
                Module module = Abyss.moduleManager.getModuleByName(args[0]);
                if (module == null) {
                    ChatUtil.error("Unknown module.");
                    return;
                }
                module.setKey(KeyUtil.parse(args[1]));
                ChatUtil.send(module.getName() + " bound to " + ChatFormatting.WHITE + KeyUtil.name(module.getKey()));
            }
        });
        commands.add(new Command("<module> <setting> <value>", "Changes a setting", "set", "s") {
            @Override
            public void run(String[] args) {
                if (args.length < 2) {
                    ChatUtil.error("Usage: set <module> <setting> <value>");
                    return;
                }
                Module module = Abyss.moduleManager.getModuleByName(args[0]);
                if (module == null) {
                    ChatUtil.error("Unknown module.");
                    return;
                }
                Setting<?> setting = module.getSetting(args[1]);
                if (setting == null || setting.getValue() instanceof SubMenu) {
                    ChatUtil.error("Settings: " + String.join(", ", module.getSettings().stream().filter(s -> !(s.getValue() instanceof SubMenu)).map(Setting::getName).toList()));
                    return;
                }
                if (args.length < 3) {
                    ChatUtil.send(setting.getName() + " = " + ChatFormatting.WHITE + setting.getValue());
                    return;
                }
                String value = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                if (!apply(setting, value)) {
                    ChatUtil.error("Invalid value.");
                    return;
                }
                ChatUtil.send(module.getName() + " " + setting.getName() + " set to " + ChatFormatting.WHITE + setting.getValue());
            }
        });
        commands.add(new Command("<add|del|list> [name]", "Manages friends", "friend", "f") {
            @Override
            public void run(String[] args) {
                if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
                    ChatUtil.send("Friends: " + ChatFormatting.WHITE + String.join(", ", Abyss.friendManager.getFriends()));
                    return;
                }
                if (args.length < 2) {
                    ChatUtil.error("Usage: friend <add|del> <name>");
                    return;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    Abyss.friendManager.add(args[1]);
                    ChatUtil.send(ChatFormatting.AQUA + args[1] + ChatFormatting.RESET + " is now your friend.");
                    Client client = Abyss.moduleManager.get(Client.class);
                    if (client != null && client.friendMessage.on()) {
                        ChatUtil.sendServer("/msg " + args[1] + " I just friended you on Abyss!");
                    }
                } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
                    ChatUtil.send(Abyss.friendManager.remove(args[1]) ? args[1] + " removed from friends." : args[1] + " is not a friend.");
                }
            }
        });
        commands.add(new Command("<char>", "Changes the command prefix", "prefix") {
            @Override
            public void run(String[] args) {
                if (args.length == 0) {
                    ChatUtil.send("Prefix is " + ChatFormatting.WHITE + getPrefix());
                    return;
                }
                Abyss.moduleManager.get(Client.class).prefix.setValue(args[0]);
                ChatUtil.send("Prefix set to " + ChatFormatting.WHITE + args[0]);
            }
        });
        commands.add(new Command("<save|load|list> [name]", "Saves or loads a config profile", "config", "cfg") {
            @Override
            public void run(String[] args) {
                if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
                    ChatUtil.send("Profiles: " + ChatFormatting.WHITE + String.join(", ", Abyss.configManager.profiles()) + ChatFormatting.GRAY + " (current " + Abyss.configManager.getProfile() + ")");
                    return;
                }
                String name = args.length > 1 ? args[1] : Abyss.configManager.getProfile();
                if (args[0].equalsIgnoreCase("save")) {
                    Abyss.configManager.saveProfile(name);
                    ChatUtil.send("Saved profile " + ChatFormatting.WHITE + name);
                } else if (args[0].equalsIgnoreCase("load")) {
                    Abyss.configManager.loadProfile(name);
                    ChatUtil.send("Loaded profile " + ChatFormatting.WHITE + name);
                }
            }
        });
        commands.add(new Command("<module>", "Hides or shows a module in the ArrayList", "drawn", "hide") {
            @Override
            public void run(String[] args) {
                Module module = args.length > 0 ? Abyss.moduleManager.getModuleByName(args[0]) : null;
                if (module == null) {
                    ChatUtil.error("Unknown module.");
                    return;
                }
                module.setHidden(!module.isHidden());
                ChatUtil.send(module.getName() + (module.isHidden() ? " hidden." : " shown."));
            }
        });
        commands.add(new Command("<module> <name>", "Renames a module in the ArrayList", "rename") {
            @Override
            public void run(String[] args) {
                Module module = args.length > 1 ? Abyss.moduleManager.getModuleByName(args[0]) : null;
                if (module == null) {
                    ChatUtil.error("Usage: rename <module> <name>");
                    return;
                }
                module.setDisplayName(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            }
        });
        commands.add(new Command("[player]", "Resets totem pop counts", "resetpops") {
            @Override
            public void run(String[] args) {
                if (args.length > 0) {
                    Abyss.totemPopManager.reset(args[0]);
                }
            }
        });
        commands.add(new Command("<x> <y> <z>", "Teleports you client side (VClip/HClip)", "vclip") {
            @Override
            public void run(String[] args) {
                if (mc.player == null || args.length == 0) {
                    return;
                }
                try {
                    double dy = Double.parseDouble(args[0]);
                    mc.player.setPos(mc.player.getX(), mc.player.getY() + dy, mc.player.getZ());
                } catch (NumberFormatException e) {
                    ChatUtil.error("Not a number.");
                }
            }
        });
    }

    public String getPrefix() {
        Client client = Abyss.moduleManager == null ? null : Abyss.moduleManager.get(Client.class);
        return client == null ? "*" : (String) client.prefix.getValue();
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void execute(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        for (Command command : commands) {
            for (String name : command.getNames()) {
                if (name.equalsIgnoreCase(parts[0])) {
                    try {
                        command.run(args);
                    } catch (Exception e) {
                        ChatUtil.error("Command failed: " + e.getMessage());
                    }
                    return;
                }
            }
        }
        Alias alias = Abyss.moduleManager.get(Alias.class);
        if (alias != null && alias.run(parts[0])) {
            return;
        }

        Module module = Abyss.moduleManager.getModuleByName(parts[0]);
        if (module != null) {
            module.toggle();
            return;
        }
        ChatUtil.error("Unknown command. Try " + getPrefix() + "help");
    }

    public static boolean apply(Setting<?> setting, String value) {
        Object current = setting.getValue();
        try {
            if (current instanceof Boolean) {
                setting.setValue(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on"));
            } else if (current instanceof Integer) {
                setting.setValue(Integer.parseInt(value));
            } else if (current instanceof Float) {
                setting.setValue(Float.parseFloat(value));
            } else if (current instanceof Double) {
                setting.setValue(Double.parseDouble(value));
            } else if (current instanceof Enum<?>) {
                return setting.setEnumByName(value);
            } else if (current instanceof Bind) {
                setting.setValue(new Bind(KeyUtil.parse(value)));
            } else if (current instanceof Color) {
                String[] p = value.split("[ ,]+");
                setting.setValue(new Color(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), p.length > 3 ? Integer.parseInt(p[3]) : 255));
            } else if (current instanceof String) {
                setting.setValue(value);
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
