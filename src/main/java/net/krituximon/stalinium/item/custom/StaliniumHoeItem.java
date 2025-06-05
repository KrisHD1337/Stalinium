package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.event.ComradeHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // Only run on server side and if it's raining at this position and the block is a CropBlock
        if (!level.isClientSide()
                && level.isRaining()
                && level.isRainingAt(pos)
                && state.getBlock() instanceof CropBlock) {

            // Collect that block’s drops
            List<ItemStack> drops = Block.getDrops(
                    state,
                    (ServerLevel) level,
                    pos,
                    null,                       // no block entity
                    player,
                    ctx.getItemInHand()
            );

            // Double each drop’s count and spawn them
            for (ItemStack drop : drops) {
                ItemStack doubled = drop.copy();
                doubled.setCount(drop.getCount() * 2);
                Block.popResource(level, pos, doubled);
            }

            // Destroy the block
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide() && player != null) {
            Optional<ComradeHandler.Party> partyOpt = ComradeHandler.findPartyOf(player.getUUID());
            if (partyOpt.isPresent()) {
                ComradeHandler.Party party = partyOpt.get();
                List<Player> comrades = new ArrayList<>();
                ServerLevel serverLevel = (ServerLevel) level;
                for (UUID memberUuid : party.members) {
                    if (memberUuid.equals(player.getUUID())) continue;
                    ServerPlayer online = serverLevel.getServer()
                            .getPlayerList()
                            .getPlayer(memberUuid);
                    if (online != null) {
                        // Check distance (8 blocks)
                        if (online.distanceTo(player) <= 8.0) {
                            comrades.add(online);
                        }
                    }
                }

                // 50% chance to send normal block drops to those comrades
                if (level.getRandom().nextFloat() < 0.5f) {
                    for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, null)) {
                        for (Player buddy : comrades) {
                            buddy.addItem(drop.copy());
                        }
                    }
                }
            }
        }

        return super.useOn(ctx);
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
                                TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.stalinium_hoe.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_hoe.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
