package net.krituximon.stalinium.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class StaliniumShovelItem extends ShovelItem {
    public StaliniumShovelItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (level.isClientSide) return true;
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            boolean anyBroken = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.offset(dx, 0, dz);
                    BlockState bs = level.getBlockState(target);
                    if (bs.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        level.destroyBlock(target, true, miningEntity);
                        anyBroken = true;
                    }
                }
            }
            if (anyBroken && miningEntity instanceof Player player) {
                MobEffectInstance selfHaste = new MobEffectInstance(MobEffects.DIG_SPEED, 100, 1, false, true, true);
                player.addEffect(selfHaste);
                var box = player.getBoundingBox().inflate(10.0);
                List<Player> allies = level.getEntitiesOfClass(
                        Player.class,
                        box,
                        other -> other instanceof ServerPlayer && player.isAlliedTo(other)
                );
                MobEffectInstance allyHaste = new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0, false, true, true);
                for (Player ally : allies) {
                    ally.addEffect(allyHaste);
                }
            }
        }
        return true;
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

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        int oldDamage = stack.getDamageValue();
        InteractionResult res = super.useOn(ctx);
        stack.setDamageValue(oldDamage);
        return res;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_shovel.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_shovel.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}