package net.krituximon.stalinium.item.custom;

import com.google.common.collect.ImmutableMap;
import net.krituximon.stalinium.event.ComradeHandler;
import net.krituximon.stalinium.item.ModArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ModArmorItem extends ArmorItem {
    private static final Map<Holder<ArmorMaterial>, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP =
            new ImmutableMap.Builder<Holder<ArmorMaterial>, List<MobEffectInstance>>()
                    .put(ModArmorMaterials.STALINIUM_ARMOR_MATERIAL,
                            List.of(new MobEffectInstance(MobEffects.HEALTH_BOOST, 100, 0, false, false)))
                    .build();

    public ModArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack,
                              Level level,
                              Entity entity,
                              int slotId,
                              boolean isSelected) {
        if (!(entity instanceof Player player) || level.isClientSide()) return;
        if (!hasFullSuitOfArmorOn(player)) return;
        evaluateArmorEffects(player);
    }

    private void evaluateArmorEffects(Player player) {
        for (var entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            Holder<ArmorMaterial> mat = entry.getKey();
            List<MobEffectInstance> effects = entry.getValue();
            if (!hasPlayerCorrectArmorOn(mat, player)) continue;
            addEffectToPlayer(player, effects);
        }
    }

    private void addEffectToPlayer(Player player, List<MobEffectInstance> baseEffects) {
        int nearbyComrades = 0;
        Optional<ComradeHandler.Party> partyOpt = ComradeHandler.findPartyOf(player.getUUID());
        if (partyOpt.isPresent() && player.level() instanceof ServerLevel serverLevel) {
            for (UUID memberUuid : partyOpt.get().members) {
                if (memberUuid.equals(player.getUUID())) continue;
                ServerPlayer online = serverLevel.getServer()
                        .getPlayerList()
                        .getPlayer(memberUuid);
                if (online != null && online.distanceTo(player) <= 20.0) {
                    nearbyComrades++;
                }
            }
        }
        for (var base : baseEffects) {
            int newAmp = Math.min(4, base.getAmplifier() + nearbyComrades);
            player.addEffect(new MobEffectInstance(
                    base.getEffect(),
                    base.getDuration(),
                    newAmp,
                    base.isAmbient(),
                    base.isVisible()
            ));
        }
    }

    private boolean hasPlayerCorrectArmorOn(Holder<ArmorMaterial> mat, Player player) {
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (!(armorStack.getItem() instanceof ArmorItem ai)
                    || ai.getMaterial() != mat) {
                return false;
            }
        }
        return true;
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) { return false; }

    @Override
    public boolean isDamaged(ItemStack stack) { return false; }
}