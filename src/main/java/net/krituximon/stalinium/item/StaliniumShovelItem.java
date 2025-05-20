package net.krituximon.stalinium.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class StaliniumShovelItem extends ShovelItem {
    public StaliniumShovelItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide) {
            MobEffectInstance haste = new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0, false, true);
            miningEntity.addEffect(haste);
            Level world = miningEntity.getCommandSenderWorld();
            var box = miningEntity.getBoundingBox().inflate(10.0);
            world.getEntitiesOfClass(LivingEntity.class, box, p -> p instanceof LivingEntity).forEach(
                    p -> p.addEffect(haste)
            );
        }
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

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }
}
