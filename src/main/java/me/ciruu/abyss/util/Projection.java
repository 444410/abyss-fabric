package me.ciruu.abyss.util;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ciruu.abyss.Globals;
import me.ciruu.abyss.mixin.accessor.GameRendererAccessor;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class Projection implements Globals {
    private static final Matrix4f MATRIX = new Matrix4f();
    private static Vec3 camera = Vec3.ZERO;
    private static float partialTicks;

    private Projection() {
    }

    public static void update() {
        var levelState = mc.gameRenderer.gameRenderState().levelRenderState;
        CameraRenderState cam = levelState.cameraRenderState;
        if (!cam.initialized) {
            return;
        }
        PoseStack bob = new PoseStack();
        GameRendererAccessor renderer = (GameRendererAccessor) mc.gameRenderer;
        renderer.abyss$bobHurt(cam, bob);
        if (mc.gameRenderer.gameRenderState().optionsRenderState.bobView) {
            renderer.abyss$bobView(cam, bob);
        }
        MATRIX.set(cam.projectionMatrix).mul(bob.last().pose()).mul(cam.viewRotationMatrix);
        camera = cam.pos;
        partialTicks = levelState.worldPartialTicks;
    }

    public static float partialTicks() {
        return partialTicks;
    }

    public static Vec3 camera() {
        return camera;
    }

    public static double[] project(double x, double y, double z) {
        Vector4f vec = new Vector4f((float) (x - camera.x), (float) (y - camera.y), (float) (z - camera.z), 1.0f);
        MATRIX.transform(vec);
        if (vec.w <= 1.0E-5f) {
            return null;
        }
        double width = mc.getWindow().getGuiScaledWidth();
        double height = mc.getWindow().getGuiScaledHeight();
        return new double[]{(vec.x / vec.w * 0.5 + 0.5) * width, (1.0 - (vec.y / vec.w * 0.5 + 0.5)) * height};
    }
}
