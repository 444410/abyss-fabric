package me.ciruu.abyss.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventNetworkPacketEvent;
import me.ciruu.abyss.events.EventPlayerUpdate;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.events.EventRenderWorld;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.settings.SubMenu;
import me.ciruu.abyss.util.BlockUtil;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.DamageUtil;
import me.ciruu.abyss.util.EntityUtil;
import me.ciruu.abyss.util.InventoryUtil;
import me.ciruu.abyss.util.PacketUtil;
import me.ciruu.abyss.util.PlayerUtil;
import me.ciruu.abyss.util.Projection;
import me.ciruu.abyss.util.RotationUtil;
import me.ciruu.abyss.util.render.RenderUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AutoCrystal extends Module {
    public enum Switch {
        None, Normal, Silent
    }

    public enum Rotate {
        Off, Place, Break, All
    }

    public enum Target {
        Damage, Closest
    }

    public enum Swing {
        Auto, Mainhand, Offhand, None
    }

    private final Setting<SubMenu> main = new Setting<>("Main", "", this, new SubMenu("Main Settings"));
    private final Setting<Switch> switchMode = new Setting<>("Switch", "", this, Switch.Normal).parent(main);
    private final Setting<Rotate> rotate = new Setting<>("Rotate", "Rotate", this, Rotate.Off).parent(main);
    private final Setting<Target> target = new Setting<>("Target", "", this, Target.Damage).parent(main);
    private final Setting<Float> targetRange = new Setting<>("TargetRange", "", this, 10f, 0f, 16f).parent(main);
    private final Setting<Boolean> pauseEating = new Setting<>("PauseEating", "Pause While Eating", this, false).parent(main);
    private final Setting<Swing> swing = new Setting<>("Swing", "", this, Swing.Auto).parent(main);

    private final Setting<SubMenu> placeMenu = new Setting<>("PlaceMenu", "", this, new SubMenu("Place Settings"));
    private final Setting<Boolean> place = new Setting<>("Place", "Place crystals", this, true).parent(placeMenu);
    private final Setting<Float> placeRange = new Setting<>("PlaceRange", "Place Range", this, 5f, 0f, 6f).parent(placeMenu);
    private final Setting<Float> facePlace = new Setting<>("FacePlace", "Ignore MinDamage when the target is below this health", this, 10f, 0.1f, 37f).parent(placeMenu);
    private final Setting<Integer> armorBreaker = new Setting<>("ArmourBreaker", "Faceplace when a target armour piece is below this %", this, 0, 0, 100).parent(placeMenu);
    private final Setting<Boolean> oneThirteen = new Setting<>("1.13", "Only one air block is needed above the base", this, true).parent(placeMenu);

    private final Setting<SubMenu> breakMenu = new Setting<>("BreakMenu", "", this, new SubMenu("Break Settings"));
    private final Setting<Boolean> doBreak = new Setting<>("Break", "Break crystals", this, true).parent(breakMenu);
    private final Setting<Float> breakRange = new Setting<>("BreakRange", "Break Range", this, 5f, 0f, 6f).parent(breakMenu);
    private final Setting<Boolean> instaBreak = new Setting<>("InstaBreak", "Break a crystal as soon as it spawns", this, true).parent(breakMenu);

    private final Setting<SubMenu> damageMenu = new Setting<>("DamageMenu", "", this, new SubMenu("Damage Settings"));
    private final Setting<Boolean> noSuicide = new Setting<>("NoSuicide", "", this, true).parent(damageMenu);
    private final Setting<Float> minDamage = new Setting<>("MinDamage", "MinDamage", this, 6f, 0.1f, 20f).parent(damageMenu);
    private final Setting<Boolean> ignoreSelfDamage = new Setting<>("IgnoreSelfDMG", "", this, false).parent(damageMenu);
    private final Setting<Float> maxSelfDamage = new Setting<>("MaxSelfDMG", "Maximum self damage", this, 9f, 0f, 20f).parent(damageMenu).visible(() -> !ignoreSelfDamage.on());

    private final Setting<SubMenu> traceMenu = new Setting<>("RaytraceMenu", "", this, new SubMenu("Raytrace Settings"));
    private final Setting<Float> placeTrace = new Setting<>("PlaceTrace", "Place range through walls", this, 3f, 0f, 6f).parent(traceMenu);
    private final Setting<Float> breakTrace = new Setting<>("BreakTrace", "Break range through walls", this, 3f, 0f, 6f).parent(traceMenu);

    private final Setting<SubMenu> delayMenu = new Setting<>("DelayMenu", "", this, new SubMenu("Delay Settings"));
    private final Setting<Integer> placeTicks = new Setting<>("PlaceTicks", "", this, 0, 0, 10).parent(delayMenu);
    private final Setting<Integer> breakTicks = new Setting<>("BreakTicks", "", this, 0, 0, 10).parent(delayMenu);

    private final Setting<SubMenu> renderMenu = new Setting<>("RenderMenu", "", this, new SubMenu("Render Settings"));
    private final Setting<Boolean> render = new Setting<>("Render", "Render", this, true).parent(renderMenu);
    private final Setting<Boolean> renderText = new Setting<>("Text", "Damage text", this, true).parent(renderMenu);
    private final Setting<Color> boxColor = new Setting<>("BoxColor", "Box color", this, new Color(142, 0, 255, 60)).parent(renderMenu);
    private final Setting<Color> outlineColor = new Setting<>("OutLinecolor", "OutLine color", this, new Color(142, 0, 255, 255)).parent(renderMenu);
    private final Setting<Float> lineWidth = new Setting<>("LineWidth", "LineWidth", this, 1.5f, 0.1f, 5f).parent(renderMenu);

    private int placeTimer;
    private int breakTimer;
    private BlockPos renderPos;
    private float renderDamage;
    private Player currentTarget;

    public AutoCrystal() {
        super("AutoCrystal", "Attacks nearby players by placing and breaking end crystals.", Category.COMBAT);
    }

    @Override
    public String getHudInfo() {
        return currentTarget == null ? null : ChatFormatting.WHITE + currentTarget.getGameProfile().name();
    }

    @Override
    protected void onDisable() {
        renderPos = null;
        currentTarget = null;
    }

    public Player getTarget() {
        return currentTarget;
    }

    private boolean canSee(Vec3 to) {
        Vec3 eyes = mc.player.getEyePosition();
        BlockHitResult hit = mc.level.clip(new ClipContext(eyes, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
        return hit.getType() == HitResult.Type.MISS;
    }

    private boolean canPlaceCrystal(BlockPos base) {
        var block = BlockUtil.block(base);
        if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
            return false;
        }
        BlockPos up = base.above();
        if (!BlockUtil.isAir(up) || (!oneThirteen.on() && !BlockUtil.isAir(up.above()))) {
            return false;
        }
        AABB space = new AABB(up.getX(), up.getY(), up.getZ(), up.getX() + 1, up.getY() + 2, up.getZ() + 1);
        for (Entity entity : mc.level.getEntities(null, space)) {
            if (entity.isAlive() && !(entity instanceof EndCrystal)) {
                return false;
            }
        }
        return mc.level.getEntitiesOfClass(EndCrystal.class, space).isEmpty();
    }

    private boolean shouldFacePlace(Player player) {
        if (DamageUtil.health(player) <= facePlace.f()) {
            return true;
        }
        if (armorBreaker.i() > 0) {
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.isDamageableItem() && 100f * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage() < armorBreaker.i()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean selfSafe(float self) {
        if (ignoreSelfDamage.on()) {
            return true;
        }
        if (noSuicide.on() && self >= DamageUtil.health(mc.player) - 0.5f) {
            return false;
        }
        return self <= maxSelfDamage.f();
    }

    private List<Player> targets() {
        List<Player> list = EntityUtil.targets(targetRange.f());
        list.removeIf(p -> p.getHealth() <= 0);
        return list;
    }

    @EventHandler
    private final Listener<EventPlayerUpdate> update = new Listener<>(EventPlayerUpdate.class, event -> {
        if (pauseEating.on() && mc.player.isUsingItem() && mc.player.getUseItem().has(net.minecraft.core.component.DataComponents.FOOD)) {
            return;
        }
        List<Player> targets = targets();
        if (target.is(Target.Closest) && targets.size() > 1) {
            targets = List.of(targets.get(0));
        }
        if (targets.isEmpty()) {
            currentTarget = null;
            renderPos = null;
            return;
        }
        if (doBreak.on() && ++breakTimer > breakTicks.i()) {
            breakCrystal(targets);
        }
        if (place.on() && ++placeTimer > placeTicks.i()) {
            placeCrystal(targets);
        }
    });

    private void breakCrystal(List<Player> targets) {
        EndCrystal best = null;
        float bestDamage = 0;
        for (EndCrystal crystal : mc.level.getEntitiesOfClass(EndCrystal.class, mc.player.getBoundingBox().inflate(breakRange.f() + 1))) {
            double dist = mc.player.getEyePosition().distanceTo(crystal.position());
            if (!crystal.isAlive() || dist > breakRange.f() || (dist > breakTrace.f() && !canSee(crystal.position()))) {
                continue;
            }
            float self = DamageUtil.crystalDamage(mc.player, crystal.position());
            if (!selfSafe(self)) {
                continue;
            }
            for (Player player : targets) {
                float damage = DamageUtil.crystalDamage(player, crystal.position());
                if ((damage >= minDamage.f() || shouldFacePlace(player)) && damage > bestDamage) {
                    bestDamage = damage;
                    best = crystal;
                    currentTarget = player;
                }
            }
        }
        if (best == null) {
            return;
        }
        if (rotate.is(Rotate.Break) || rotate.is(Rotate.All)) {
            RotationUtil.lookAt(best.position());
        }
        mc.gameMode.attack(mc.player, best);
        doSwing(InteractionHand.MAIN_HAND);
        breakTimer = 0;
    }

    private void placeCrystal(List<Player> targets) {
        boolean offhand = mc.player.getOffhandItem().is(Items.END_CRYSTAL);
        int slot = offhand ? -1 : InventoryUtil.findHotbar(Items.END_CRYSTAL);
        if (!offhand && !mc.player.getMainHandItem().is(Items.END_CRYSTAL) && (slot == -1 || switchMode.is(Switch.None))) {
            return;
        }
        BlockPos bestPos = null;
        float bestDamage = 0;
        Player bestTarget = null;
        for (BlockPos pos : BlockUtil.sphere(mc.player.blockPosition(), placeRange.f() + 1, false)) {
            Vec3 center = Vec3.atBottomCenterOf(pos.above());
            double dist = mc.player.getEyePosition().distanceTo(Vec3.atCenterOf(pos));
            if (dist > placeRange.f() || !canPlaceCrystal(pos)) {
                continue;
            }
            if (dist > placeTrace.f() && !canSee(Vec3.atCenterOf(pos).add(0, 0.5, 0))) {
                continue;
            }
            float self = DamageUtil.crystalDamage(mc.player, center);
            if (!selfSafe(self)) {
                continue;
            }
            for (Player player : targets) {
                if (player.distanceToSqr(center) > 144) {
                    continue;
                }
                float damage = DamageUtil.crystalDamage(player, center);
                boolean ok = damage >= minDamage.f() || (shouldFacePlace(player) && damage > 1.5f);
                if (ok && damage > bestDamage) {
                    bestDamage = damage;
                    bestPos = pos;
                    bestTarget = player;
                }
            }
        }
        renderPos = bestPos;
        renderDamage = bestDamage;
        if (bestPos == null) {
            return;
        }
        currentTarget = bestTarget;
        InteractionHand hand = offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        int old = InventoryUtil.selected();
        boolean switched = false;
        if (!offhand && !mc.player.getMainHandItem().is(Items.END_CRYSTAL)) {
            if (switchMode.is(Switch.Silent)) {
                InventoryUtil.switchSilent(slot);
            } else {
                InventoryUtil.switchTo(slot);
            }
            switched = true;
        }
        Vec3 hitVec = Vec3.atCenterOf(bestPos).add(0, 0.5, 0);
        if (rotate.is(Rotate.Place) || rotate.is(Rotate.All)) {
            RotationUtil.lookAt(hitVec);
        }
        mc.gameMode.useItemOn(mc.player, hand, new BlockHitResult(hitVec, Direction.UP, bestPos, false));
        doSwing(hand);
        if (switched && switchMode.is(Switch.Silent)) {
            InventoryUtil.switchSilent(old);
        }
        placeTimer = 0;
    }

    private void doSwing(InteractionHand hand) {
        switch (swing.getValue()) {
            case Auto -> PlayerUtil.swing(hand);
            case Mainhand -> PlayerUtil.swing(InteractionHand.MAIN_HAND);
            case Offhand -> PlayerUtil.swing(InteractionHand.OFF_HAND);
            case None -> {
            }
        }
    }

    @EventHandler
    private final Listener<EventNetworkPacketEvent> spawn = new Listener<>(EventNetworkPacketEvent.class, event -> {
        if (!instaBreak.on() || !doBreak.on() || !(event.getPacket() instanceof ClientboundAddEntityPacket packet) || packet.getType() != EntityTypes.END_CRYSTAL) {
            return;
        }
        if (mc.player == null || renderPos == null) {
            return;
        }
        Vec3 pos = new Vec3(packet.getX(), packet.getY(), packet.getZ());
        if (BlockPos.containing(pos).below().equals(renderPos) && mc.player.getEyePosition().distanceTo(pos) <= breakRange.f()) {
            float self = DamageUtil.crystalDamage(mc.player, pos);
            if (selfSafe(self)) {
                PacketUtil.send(new ServerboundAttackPacket(packet.getId()));
                mc.execute(() -> doSwing(InteractionHand.MAIN_HAND));
            }
        }
    });

    @EventHandler
    private final Listener<EventRenderWorld> renderWorld = new Listener<>(EventRenderWorld.class, event -> {
        if (render.on() && renderPos != null) {
            RenderUtil.block(renderPos, boxColor.argb(), outlineColor.argb(), lineWidth.f());
        }
    });

    @EventHandler
    private final Listener<EventRender2D> render2d = new Listener<>(EventRender2D.class, event -> {
        if (!render.on() || !renderText.on() || renderPos == null) {
            return;
        }
        double[] p = Projection.project(renderPos.getX() + 0.5, renderPos.getY() + 0.5, renderPos.getZ() + 0.5);
        if (p != null) {
            String text = String.format("%.1f", renderDamage);
            event.graphics().text(mc.font, text, (int) p[0] - mc.font.width(text) / 2, (int) p[1] - 4, ColorUtil.withAlpha(0xFFFFFF, 255), true);
        }
    });

    public static boolean isActive() {
        AutoCrystal ac = Abyss.moduleManager.get(AutoCrystal.class);
        return ac != null && ac.isEnabled();
    }
}
