package me.ciruu.abyss.modules.render;

import java.awt.Color;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class StorageESP extends Module {
    private final Setting<Boolean> chests = new Setting<>("Chests", "", this, true);
    private final Setting<Boolean> enderChests = new Setting<>("EnderChests", "", this, true);
    private final Setting<Boolean> shulkers = new Setting<>("Shulkers", "", this, true);
    private final Setting<Boolean> others = new Setting<>("Others", "Barrels, hoppers, furnaces, dispensers", this, false);
    private final Setting<Integer> alpha = new Setting<>("Alpha", "Fill alpha", this, 50, 0, 255);

    public StorageESP() {
        super("StorageESP", "Highlights storage blocks.", Category.RENDER);
    }

    private int color(BlockEntity be) {
        if (be instanceof ChestBlockEntity && chests.on()) {
            return new Color(255, 170, 0).getRGB();
        }
        if (be instanceof EnderChestBlockEntity && enderChests.on()) {
            return new Color(170, 0, 255).getRGB();
        }
        if (be instanceof ShulkerBoxBlockEntity && shulkers.on()) {
            return new Color(255, 0, 170).getRGB();
        }
        if (others.on() && (be instanceof BarrelBlockEntity || be instanceof HopperBlockEntity || be instanceof AbstractFurnaceBlockEntity || be instanceof DispenserBlockEntity)) {
            return new Color(150, 150, 150).getRGB();
        }
        return 0;
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        int radius = mc.options.renderDistance().get();
        int cx = mc.player.blockPosition().getX() >> 4;
        int cz = mc.player.blockPosition().getZ() >> 4;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                LevelChunk chunk = mc.level.getChunkSource().getChunk(x, z, false);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    int c = color(be);
                    if (c != 0) {
                        RenderUtil.block(be.getBlockPos(), ColorUtil.withAlpha(c, alpha.i()), ColorUtil.withAlpha(c, 255), 1.5f);
                    }
                }
            }
        }
    });
}
