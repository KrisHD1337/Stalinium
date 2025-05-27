package net.krituximon.stalinium.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

public class StaliniumPickaxeItem extends PickaxeItem {
    public StaliniumPickaxeItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
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

