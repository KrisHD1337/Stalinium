package net.krituximon.stalinium.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class StaliniumHoeItem extends HoeItem {
    public StaliniumHoeItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = ctx.getPlayer();

        // Only run on server side and if it's raining at this position
        if (!level.isClientSide &&
                level.isRaining() &&
                level.isRainingAt(pos) &&
                state.getBlock() instanceof CropBlock) {

            // Get the block's drops
            List<ItemStack> drops = Block.getDrops(
                    state,
                    (ServerLevel) level,
                    pos,
                    null,                          // no block entity
                    ctx.getPlayer(),
                    ctx.getItemInHand()
            );

            // Multiply each drop's count by 2 and spawn them
            for (ItemStack drop : drops) {
                ItemStack doubled = drop.copy();
                doubled.setCount(drop.getCount() * 2);
                Block.popResource(level, pos, doubled);
            }

            // Destroy the block
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            return InteractionResult.SUCCESS;
        }
        List<Player> comrades = level.getEntitiesOfClass(
                Player.class,
                player.getBoundingBox().inflate(8),
                p -> p instanceof ServerPlayer && player.isAlliedTo(p)
        );
        RandomSource rand = level.getRandom();
        if (rand.nextFloat() < 0.5f) {
            for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, null)) {
                for (Player buddy : comrades) {
                    buddy.addItem(drop.copy());
                }
            }
        }

        return super.useOn(ctx);  // fallback to normal hoe behavior
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
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_hoe.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_hoe.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

