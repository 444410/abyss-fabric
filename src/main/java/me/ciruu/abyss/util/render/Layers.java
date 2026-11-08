package me.ciruu.abyss.util.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class Layers {
    private static final RenderType LINES = RenderType.create("abyss_lines", RenderSetup.builder(Pipelines.LINES).createRenderSetup());
    private static final RenderType QUADS = RenderType.create("abyss_quads", RenderSetup.builder(Pipelines.QUADS).createRenderSetup());

    private Layers() {
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType quads() {
        return QUADS;
    }
}
