package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.item.ModArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaliniumBootsItem extends ArmorItem {
    private static final int SPRINT_THRESHOLD = 5 * 20;
    private static final double RADIUS = 5.0;
    private static final Map<UUID, Integer> sprintTicks = new ConcurrentHashMap<>();

    public StaliniumBootsItem(Holder<ArmorMaterial> material, Type slot, Properties props) {
        super(material, slot, props);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) return;
        ItemStack boots = player.getInventory().getArmor(0);
        if (boots.getItem() != this) return;
        UUID id = player.getUUID();
        int ticks = sprintTicks.getOrDefault(id, 0);
        if (player.isSprinting()) {
            ticks++;
            if (ticks >= SPRINT_THRESHOLD) {
                ticks = 0;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5 * 20, 0, false, true));
                AABB area = player.getBoundingBox().inflate(RADIUS);
                List<Player> allies = level.getEntitiesOfClass(
                        Player.class,
                        area,
                        p -> p instanceof ServerPlayer && p != player && player.isAlliedTo(p)
                );
                MobEffectInstance buff = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5 * 20, 0, false, true);
                for (Player ally : allies) {
                    ally.addEffect(buff);
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
}
