package net.krituximon.stalinium.item.custom;

import com.google.common.collect.ImmutableMap;
import net.krituximon.stalinium.event.ComradeHandler;
import net.krituximon.stalinium.item.ModArmorMaterials;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StaliniumChestplateLeggingsItem extends ModArmorItem {
    private static final Map<Holder<ArmorMaterial>, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            (new ImmutableMap.Builder<Holder<ArmorMaterial>, List<MobEffectInstance>>())
                    .put(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL,
                            List.of(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false)))
                    .build();

    public StaliniumChestplateLeggingsItem(Holder<ArmorMaterial> material,
                                           Type type,
                                           Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack,
                              Level level,
                              Entity entity,
                              int slotId,
                              boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player)) return;
        Player player = (Player) entity;
        if (hasChestAndLeggingsOn(player)) {
            evaluateArmorEffects(player);
        }
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<Holder<ArmorMaterial>, List<MobEffectInstance>> entry
                : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            Holder<ArmorMaterial> mapArmorMaterial = entry.getKey();
            List<MobEffectInstance> mapEffect = entry.getValue();
            if (hasPlayerCorrectArmorOn(mapArmorMaterial, player)) {
                addEffectToPlayer(player, mapEffect);
            }
        }
    }

    private void addEffectToPlayer(Player player, List<MobEffectInstance> mapEffect) {
        for (MobEffectInstance effect : mapEffect) {
            player.addEffect(new MobEffectInstance(
                    effect.getEffect(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.isVisible()
            ));
            for (ComradeHandler.Party party : ComradeHandler.PARTIES.values()) {
                if (party.isMember(player.getUUID())) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    for (UUID memberUuid : party.members) {
                        if (memberUuid.equals(player.getUUID())) continue;
                        ServerPlayer online = serverPlayer.level()
                                .getServer()
                                .getPlayerList()
                                .getPlayer(memberUuid);
                        if (online != null) {
                            if (online.distanceTo(player) <= 5.0) {
                                online.addEffect(new MobEffectInstance(
                                        effect.getEffect(),
                                        effect.getDuration(),
                                        effect.getAmplifier(),
                                        effect.isAmbient(),
                                        effect.isVisible()
                                ));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean hasPlayerCorrectArmorOn(Holder<ArmorMaterial> mapArmorMaterial,
                                            Player player) {
        ItemStack leggingsStack   = player.getInventory().getArmor(1);
        ItemStack chestplateStack = player.getInventory().getArmor(2);

        if (leggingsStack.isEmpty() || chestplateStack.isEmpty()) {
            return false;
        }
        ArmorItem leggingsItem   = (ArmorItem) leggingsStack.getItem();
        ArmorItem chestplateItem = (ArmorItem) chestplateStack.getItem();
        return leggingsItem.getMaterial()   == mapArmorMaterial
                && chestplateItem.getMaterial() == mapArmorMaterial;
    }

    private boolean hasChestAndLeggingsOn(Player player) {
        ItemStack leggings   = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        return !leggings.isEmpty() && !chestplate.isEmpty();
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
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        boolean shift = Screen.hasShiftDown();
        EquipmentSlot slot = this.getEquipmentSlot();
        if (slot == EquipmentSlot.CHEST) {
            tooltipComponents.add( shift
                    ? Component.translatable("item.stalinium_chestplate.tooltip_shift")
                    : Component.translatable("item.stalinium_chestplate.tooltip")
            );
        } else if (slot == EquipmentSlot.LEGS) {
            tooltipComponents.add( shift
                    ? Component.translatable("item.stalinium_leggings.tooltip_shift")
                    : Component.translatable("item.stalinium_leggings.tooltip")
            );
        }
    }
}