package me.ciruu.abyss.modules.render;

import java.util.ArrayList;
import java.util.List;
import me.ciruu.abyss.Abyss;
import me.ciruu.abyss.events.EventRender2D;
import me.ciruu.abyss.modules.Category;
import me.ciruu.abyss.modules.Module;
import me.ciruu.abyss.settings.Setting;
import me.ciruu.abyss.util.ColorUtil;
import me.ciruu.abyss.util.Projection;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class NameTags extends Module {
    private final Setting<Boolean> health = new Setting<>("Health", "", this, true);
    private final Setting<Boolean> ping = new Setting<>("Ping", "", this, true);
    private final Setting<Boolean> pops = new Setting<>("TotemPops", "", this, true);
    private final Setting<Boolean> armor = new Setting<>("Armor", "", this, true);
    private final Setting<Boolean> durability = new Setting<>("Durability", "", this, true).visible(() -> armor.on());
    private final Setting<Boolean> self = new Setting<>("Self", "Show your own tag in third person", this, false);
    private final Setting<Float> scale = new Setting<>("Scale", "", this, 1f, 0.5f, 3f);
    private final Setting<Boolean> background = new Setting<>("Background", "", this, true);

    public NameTags() {
        super("NameTags", "Better nametags.", Category.RENDER);
    }

    public static boolean hidesVanilla() {
        return Abyss.moduleManager != null && Abyss.moduleManager.isModuleEnabled(NameTags.class);
    }

    @EventHandler
    private final Listener<EventRender2D> render = new Listener<>(EventRender2D.class, event -> {
        GuiGraphicsExtractor g = event.graphics();
        for (Player player : mc.level.players()) {
            if (player.isInvisible() || (player == mc.player && (!self.on() || mc.options.getCameraType().isFirstPerson()))) {
                continue;
            }
            Vec3 pos = player.getPosition(event.partialTicks());
            double[] p = Projection.project(pos.x, pos.y + player.getBbHeight() + 0.35, pos.z);
            if (p == null) {
                continue;
            }
            draw(g, player, (float) p[0], (float) p[1]);
        }
    });

    private void draw(GuiGraphicsExtractor g, Player player, float x, float y) {
        String name = player.getGameProfile().name();
        StringBuilder text = new StringBuilder();
        text.append(Abyss.friendManager.isFriend(player) ? ChatFormatting.AQUA : player.isShiftKeyDown() ? ChatFormatting.GOLD : ChatFormatting.WHITE).append(name);
        if (ping.on() && mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(player.getUUID());
            if (info != null) {
                text.append(ChatFormatting.GRAY).append(' ').append(info.getLatency()).append("ms");
            }
        }
        if (health.on()) {
            float hp = player.getHealth() + player.getAbsorptionAmount();
            ChatFormatting c = hp > 18 ? ChatFormatting.GREEN : hp > 12 ? ChatFormatting.YELLOW : hp > 6 ? ChatFormatting.GOLD : ChatFormatting.RED;
            text.append(c).append(' ').append(Math.round(hp));
        }
        if (pops.on()) {
            int count = Abyss.totemPopManager.getPops(name);
            if (count > 0) {
                text.append(ChatFormatting.RED).append(" -").append(count);
            }
        }
        String s = text.toString();
        float sc = scale.f();
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(sc, sc);
        int w = mc.font.width(s);
        if (background.on()) {
            g.fill(-w / 2 - 2, -2, w / 2 + 2, 9, 0x90000000);
        }
        g.text(mc.font, s, -w / 2, 0, 0xFFFFFFFF, true);
        if (armor.on()) {
            List<ItemStack> items = new ArrayList<>();
            items.add(player.getMainHandItem());
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                items.add(player.getItemBySlot(slot));
            }
            items.add(player.getOffhandItem());
            items.removeIf(ItemStack::isEmpty);
            int ix = -items.size() * 8;
            for (ItemStack stack : items) {
                g.item(stack, ix, -20);
                g.itemDecorations(mc.font, stack, ix, -20);
                if (durability.on() && stack.isDamageableItem()) {
                    int pct = Math.round(100f * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
                    String d = pct + "%";
                    g.pose().pushMatrix();
                    g.pose().translate(ix + 8, -26);
                    g.pose().scale(0.5f, 0.5f);
                    g.text(mc.font, d, -mc.font.width(d) / 2, 0, ColorUtil.healthColor(pct, 100), true);
                    g.pose().popMatrix();
                }
                ix += 16;
            }
        }
        g.pose().popMatrix();
    }
}
