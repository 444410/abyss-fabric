package me.ciruu.abyss.modules.misc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ChatUtil;
import me.ciruu.abyss.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class Spammer extends Module {
    private final Setting<Float> delay = new Setting<>("Delay", "Seconds between messages", this, 5f, 0.2f, 200f);
    private final Setting<String> fileName = new Setting<>("Filename", "", this, "spammer");
    private final Setting<Boolean> randomOrder = new Setting<>("RandomOrder", "", this, false);
    private final Setting<Boolean> randomLetters = new Setting<>("RandomLetters", "Random letters in the sentences", this, true);
    private final Setting<Integer> numberRandomLetters = new Setting<>("NumberRandomLetters", "", this, 3, 0, 10).visible(() -> randomLetters.on());
    private final List<String> lines = new ArrayList<>();
    private final Timer timer = new Timer();
    private int index;

    public Spammer() {
        super("Spammer", "A random spammer to bypass 2b2t automute", Category.MISC);
    }

    @Override
    protected void onEnable() {
        lines.clear();
        index = 0;
        Path file = Abyss.directory().resolve(fileName.getValue() + ".txt");
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file, "Abyss on top!\nGet Abyss today!\n");
            }
            for (String line : Files.readAllLines(file)) {
                if (!line.isBlank()) {
                    lines.add(line.trim());
                }
            }
        } catch (Exception e) {
            ChatUtil.error("Could not read " + file);
        }
        if (lines.isEmpty()) {
            ChatUtil.error(file + " is empty.");
            setEnabled(false);
        }
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (lines.isEmpty() || !timer.passed((long) (delay.f() * 1000))) {
            return;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String line = randomOrder.on() ? lines.get(random.nextInt(lines.size())) : lines.get(index++ % lines.size());
        if (randomLetters.on() && numberRandomLetters.i() > 0) {
            StringBuilder extra = new StringBuilder(" ");
            for (int i = 0; i < numberRandomLetters.i(); i++) {
                extra.append((char) ('a' + random.nextInt(26)));
            }
            line += extra;
        }
        ChatUtil.sendServer(line);
        timer.reset();
    });
}
