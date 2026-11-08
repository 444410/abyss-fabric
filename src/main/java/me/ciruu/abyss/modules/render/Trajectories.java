package me.ciruu.abyss.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Trajectories extends Module {
    private final Setting<Color> color = new Setting<>("Color", "", this, new Color(142, 0, 255, 255));

    public Trajectories() {
        super("Trajectories", "Shows where projectiles will land.", Category.RENDER);
    }

    private double[] physics(ItemStack stack) {
        if (stack.getItem() instanceof BowItem) {
            float charge = mc.player.isUsingItem() ? BowItem.getPowerForTime(mc.player.getTicksUsingItem()) : 1f;
            return new double[]{3.0 * Math.max(0.1, charge), 0.05, 0.99};
        }
        if (stack.getItem() instanceof CrossbowItem) {
            return new double[]{3.15, 0.05, 0.99};
        }
        if (stack.is(Items.TRIDENT)) {
            return new double[]{2.5, 0.05, 0.99};
        }
        if (stack.is(Items.ENDER_PEARL) || stack.is(Items.SNOWBALL) || stack.is(Items.EGG)) {
            return new double[]{1.5, 0.03, 0.99};
        }
        if (stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
            return new double[]{0.5, 0.05, 0.99};
        }
        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            return new double[]{0.7, 0.07, 0.99};
        }
        return null;
    }

    @EventHandler
    private final Listener<EventRenderWorld> render = new Listener<>(EventRenderWorld.class, event -> {
        double[] phys = physics(mc.player.getMainHandItem());
        if (phys == null) {
            phys = physics(mc.player.getOffhandItem());
        }
        if (phys == null) {
            return;
        }
        float yaw = mc.player.getViewYRot(event.partialTicks());
        float pitch = mc.player.getViewXRot(event.partialTicks());
        if (mc.player.getMainHandItem().is(Items.SPLASH_POTION) || mc.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE)) {
            pitch -= 20;
        }
        Vec3 pos = mc.player.getEyePosition(event.partialTicks()).add(0, -0.1, 0);
        Vec3 vel = Vec3.directionFromRotation(pitch, yaw).scale(phys[0]);
        List<Vec3> points = new ArrayList<>();
        points.add(pos);
        BlockHitResult landed = null;
        for (int i = 0; i < 300; i++) {
            Vec3 next = pos.add(vel);
            BlockHitResult hit = mc.level.clip(new ClipContext(pos, next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
            if (hit.getType() != HitResult.Type.MISS) {
                points.add(hit.getLocation());
                landed = hit;
                break;
            }
            pos = next;
            points.add(pos);
            vel = vel.scale(phys[2]).add(0, -phys[1], 0);
            if (pos.y < mc.level.getMinY()) {
                break;
            }
        }
        RenderUtil.strip(points, color.argb(), 2f);
        if (landed != null) {
            Vec3 l = landed.getLocation();
            RenderUtil.box(new AABB(l.x - 0.25, l.y - 0.25, l.z - 0.25, l.x + 0.25, l.y + 0.25, l.z + 0.25), ColorUtil.withAlpha(color.argb(), 60), color.argb(), 1.5f);
        }
    });
}
