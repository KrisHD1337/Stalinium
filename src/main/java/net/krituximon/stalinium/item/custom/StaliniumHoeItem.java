package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.event.ComradeHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StaliniumHoeItem extends HoeItem {
    public StaliniumHoeItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public boolean mineBlock(ItemStack stack,
                             Level level,
                             BlockState state,
                             BlockPos pos,
                             LivingEntity miner) {
        if (level.isClientSide || !(miner instanceof Player player))
            return super.mineBlock(stack, level, state, pos, miner);
        if (state.getBlock() instanceof CropBlock) {
            List<ItemStack> drops = Block.getDrops(
                    state,
                    (ServerLevel) level,
                    pos,
                    null,
                    player,
                    stack
            );
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            for (ItemStack drop : drops) {
                player.addItem(drop.copy());
            }
            if (level.isRaining() && level.isRainingAt(pos)) {
                for (ItemStack drop : drops) {
                    ItemStack extra = drop.copy();
                    extra.setCount(drop.getCount());
                    player.addItem(extra);
                }
            }
            if (!level.isClientSide() && level.getRandom().nextFloat() < 0.5f) {
                Optional<ComradeHandler.Party> partyOpt = ComradeHandler.findPartyOf(player.getUUID());
                if (partyOpt.isPresent()) {
                    ComradeHandler.Party party = partyOpt.get();
                    ServerLevel sl = (ServerLevel) level;
                    for (UUID member : party.members) {
                        if (member.equals(player.getUUID())) continue;
                        ServerPlayer ally = sl.getServer()
                                .getPlayerList()
                                .getPlayer(member);
                        if (ally != null && ally.distanceTo(player) <= 8.0) {
                            for (ItemStack drop : drops) {
                                ally.addItem(drop.copy());
                            }
                        }
                    }
                }
            }

            return true;
        }

        return super.mineBlock(stack, level, state, pos, miner);
    }

    @Override public boolean isDamageable(ItemStack stack) { return false; }
    @Override public boolean isDamaged(ItemStack stack)    { return false; }

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