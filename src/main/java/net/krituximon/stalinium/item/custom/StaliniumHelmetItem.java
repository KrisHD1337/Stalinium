package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class StaliniumHelmetItem extends ModArmorItem {
    public StaliniumHelmetItem(Holder<ArmorMaterial> material, Type slot, Properties props) {
        super(material, slot, props);
    }
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

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_helmet.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_helmet.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
