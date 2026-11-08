package me.ciruu.abyss.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.CombatUtil;
import me.ciruu.abyss.util.HoleUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class WebFill extends Module {
    private final Setting<Float> placeRange = new Setting<>("PlaceRange", "Place Range", this, 5f, 0f, 6f);
    private final Setting<Integer> placeTicks = new Setting<>("PlaceTicks", "Place Delay", this, 0, 0, 10);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "Rotate", this, false);
    private final Setting<Integer> blocksPerTick = new Setting<>("BlocksPerTick", "Blocks per tick", this, 1, 1, 20);
    private final Setting<Boolean> disableAfter = new Setting<>("DisableAfter", "Turn off after DisableTime", this, false);
    private final Setting<Integer> disableTime = new Setting<>("DisableTime", "Milliseconds", this, 5000, 1, 10000).visible(() -> disableAfter.on());
    private int ticks;
    private long enabledAt;

    public WebFill() {
        super("WebFill", "Fill nearby holes with cobwebs.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        enabledAt = System.currentTimeMillis();
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (disableAfter.on() && System.currentTimeMillis() - enabledAt > disableTime.i()) {
            setEnabled(false);
            return;
        }
        if (++ticks <= placeTicks.i()) {
            return;
        }
        ticks = 0;
        BlockPos self = CombatUtil.feet(mc.player);
        List<BlockPos> holes = new ArrayList<>();
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), placeRange.f(), false)) {
            if (!pos.equals(self) && HoleUtil.hole(pos) != HoleUtil.Type.NONE) {
                holes.add(pos);
            }
        }
        CombatUtil.placeAll(holes, CombatUtil.blockSlot(Blocks.COBWEB), blocksPerTick.i(), rotate.on(), CombatUtil.SwitchMode.Silent);
    });
}
