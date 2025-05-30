package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class StaliniumHelmetItem extends ArmorItem {
    public StaliniumHelmetItem(Holder<ArmorMaterial> material, Type slot, Properties props) {
        super(material, slot, props);
    }

    /**
     * Static subscriber that listens for when *any* player attacks an entity.
     * If the attacker is wearing our helmet, we give the target Glowing for 10 s.
     */
    @EventBusSubscriber(modid = Stalinium.MODID)
    public static class HitSubscriber {
        @SubscribeEvent
        public static void onPlayerHitEntity(AttackEntityEvent event) {
            if (event.getEntity().level().isClientSide) return;
            if (!(event.getEntity() instanceof Player player)) return;
            if (!(event.getTarget() instanceof LivingEntity target)) return;
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!(head.getItem() instanceof StaliniumHelmetItem)) return;
            target.addEffect(new MobEffectInstance(
                    MobEffects.GLOWING,
                    10 * 20,
                    0,
                    false,
                    true
            ));
        }
    }
}
