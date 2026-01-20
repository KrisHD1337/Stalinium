package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.event.ComradeHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaliniumBootsItem extends ModArmorItem {
    private static final int SPRINT_THRESHOLD = 5 * 20;
    private static final double RADIUS = 5.0;
    private static final Map<UUID, Integer> sprintTicks = new ConcurrentHashMap<>();

    public StaliniumBootsItem(ArmorMaterial material, ArmorType slot, Properties props) {
        super(material, slot, props);
    }

    @Override
    public void inventoryTick(ItemStack stack,
                              Level level,
                              Entity entity,
                              int slotId,
                              boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player)) return;
        Player player = (Player) entity;
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        if (feet.getItem() != this) return;
        UUID id = player.getUUID();
        int ticks = sprintTicks.getOrDefault(id, 0);
        if (player.isSprinting()) {
            ticks++;
            if (ticks >= SPRINT_THRESHOLD) {
                ticks = 0;
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, 5 * 20, 0, false, true
                ));
                List<Player> allies = new ArrayList<>();
                for (ComradeHandler.Party party : ComradeHandler.PARTIES.values()) {
                    if (party.isMember(player.getUUID())) {
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        for (UUID memberUuid : party.members) {
                            if (memberUuid.equals(player.getUUID())) continue;
                            ServerPlayer online = serverPlayer.level()
                                    .getServer()
                                    .getPlayerList()
                                    .getPlayer(memberUuid);
                            if (online != null && online.distanceTo(player) <= RADIUS) {
                                allies.add(online);
                            }
                        }
                        break;
                    }
                }

                // Apply Speed I to each ally
                for (Player ally : allies) {
                    ally.addEffect(new MobEffectInstance(
                            MobEffects.MOVEMENT_SPEED, 5 * 20, 0, false, true
                    ));
                }
            }
        } else {
            ticks = 0;
        }

        sprintTicks.put(id, ticks);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                net.minecraft.world.item.Item.TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_boots.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_boots.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
