package me.ciruu.abyss.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RenderUtil {
    private static PoseStack pose;
    private static SubmitNodeCollector collector;
    private static Vec3 camera = Vec3.ZERO;

    private RenderUtil() {
    }

    public static void begin(PoseStack stack, SubmitNodeCollector nodes, Vec3 cameraPos) {
        pose = stack;
        collector = nodes;
        camera = cameraPos;
    }

    public static void end() {
        pose = null;
        collector = null;
    }

    public static Vec3 cameraPos() {
        return camera;
    }

    private static boolean ready() {
        return pose != null && collector != null;
    }

    public static void line(Vec3 from, Vec3 to, int color, float width) {
        line(from, to, color, color, width);
    }

    public static void line(Vec3 from, Vec3 to, int c1, int c2, float width) {
        if (!ready()) {
            return;
        }
        collector.submitCustomGeometry(pose, Layers.lines(), (p, v) -> addLine(v, p, from, to, c1, c2, width));
    }

    public static void strip(List<Vec3> points, int color, float width) {
        if (!ready() || points.size() < 2) {
            return;
        }
        collector.submitCustomGeometry(pose, Layers.lines(), (p, v) -> {
            for (int i = 1; i < points.size(); i++) {
                addLine(v, p, points.get(i - 1), points.get(i), color, color, width);
            }
        });
    }

    public static void boxEdges(AABB box, int color, float width) {
        if (!ready()) {
            return;
        }
        collector.submitCustomGeometry(pose, Layers.lines(), (p, v) -> {
            double[][] c = corners(box);
            int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
            for (int[] e : edges) {
                addLine(v, p, new Vec3(c[e[0]][0], c[e[0]][1], c[e[0]][2]), new Vec3(c[e[1]][0], c[e[1]][1], c[e[1]][2]), color, color, width);
            }
        });
    }

    public static void boxFill(AABB box, int color) {
        if (!ready()) {
            return;
        }
        collector.submitCustomGeometry(pose, Layers.quads(), (p, v) -> {
            float x1 = (float) (box.minX - camera.x), y1 = (float) (box.minY - camera.y), z1 = (float) (box.minZ - camera.z);
            float x2 = (float) (box.maxX - camera.x), y2 = (float) (box.maxY - camera.y), z2 = (float) (box.maxZ - camera.z);
            quad(v, p, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, color);
            quad(v, p, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, color);
            quad(v, p, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, color);
            quad(v, p, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, color);
            quad(v, p, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, color);
            quad(v, p, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, color);
        });
    }

    public static void box(AABB box, int fill, int outline, float width) {
        if ((fill >>> 24) > 0) {
            boxFill(box, fill);
        }
        if ((outline >>> 24) > 0) {
            boxEdges(box, outline, width);
        }
    }

    public static void block(BlockPos pos, int fill, int outline, float width) {
        box(new AABB(pos), fill, outline, width);
    }

    public static void flat(BlockPos pos, int fill, int outline, float width) {
        box(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.002, pos.getZ() + 1), fill, outline, width);
    }

    public static void circle(Vec3 center, double radius, int color, float width, int segments) {
        if (!ready()) {
            return;
        }
        collector.submitCustomGeometry(pose, Layers.lines(), (p, v) -> {
            for (int i = 0; i < segments; i++) {
                double a1 = Math.PI * 2 * i / segments;
                double a2 = Math.PI * 2 * (i + 1) / segments;
                addLine(v, p, center.add(Math.cos(a1) * radius, 0, Math.sin(a1) * radius),
                        center.add(Math.cos(a2) * radius, 0, Math.sin(a2) * radius), color, color, width);
            }
        });
    }

    public static void addLine(VertexConsumer v, PoseStack.Pose p, Vec3 from, Vec3 to, int c1, int c2, float width) {
        float x1 = (float) (from.x - camera.x), y1 = (float) (from.y - camera.y), z1 = (float) (from.z - camera.z);
        float x2 = (float) (to.x - camera.x), y2 = (float) (to.y - camera.y), z2 = (float) (to.z - camera.z);
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0E-6f) {
            return;
        }
        dx /= len;
        dy /= len;
        dz /= len;
        v.addVertex(p, x1, y1, z1).setColor(c1).setNormal(p, dx, dy, dz).setLineWidth(width);
        v.addVertex(p, x2, y2, z2).setColor(c2).setNormal(p, dx, dy, dz).setLineWidth(width);
    }

    private static void quad(VertexConsumer v, PoseStack.Pose p, float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz, int color) {
        v.addVertex(p, ax, ay, az).setColor(color);
        v.addVertex(p, bx, by, bz).setColor(color);
        v.addVertex(p, cx, cy, cz).setColor(color);
        v.addVertex(p, dx, dy, dz).setColor(color);
    }

    private static double[][] corners(AABB b) {
        return new double[][]{
                {b.minX, b.minY, b.minZ}, {b.maxX, b.minY, b.minZ}, {b.maxX, b.minY, b.maxZ}, {b.minX, b.minY, b.maxZ},
                {b.minX, b.maxY, b.minZ}, {b.maxX, b.maxY, b.minZ}, {b.maxX, b.maxY, b.maxZ}, {b.minX, b.maxY, b.maxZ}};
    }
}
