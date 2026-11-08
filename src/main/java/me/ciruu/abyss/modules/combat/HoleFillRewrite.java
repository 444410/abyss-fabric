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

public class HoleFillRewrite extends Module {
    public enum Block {
        Obsidian, Web
    }

    private final Setting<Block> block = new Setting<>("Block", "", this, Block.Obsidian);
    private final Setting<Float> range = new Setting<>("Range", "", this, 4.5f, 1f, 6f);
    private final Setting<Boolean> rotate = new Setting<>("Rotate", "", this, false);

    public HoleFillRewrite() {
        super("HoleFillRewrite", "Fills all holes in range.", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        BlockPos self = CombatUtil.feet(mc.player);
        List<BlockPos> holes = new ArrayList<>();
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), range.f(), false)) {
            if (!pos.equals(self) && HoleUtil.hole(pos) != HoleUtil.Type.NONE) {
                holes.add(pos);
            }
        }
        int slot = block.is(Block.Web) ? CombatUtil.blockSlot(Blocks.COBWEB) : CombatUtil.obsidianSlot();
        CombatUtil.placeAll(holes, slot, 1, rotate.on(), CombatUtil.SwitchMode.Silent);
    });
}
