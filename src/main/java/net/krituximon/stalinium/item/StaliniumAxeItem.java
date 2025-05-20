package net.krituximon.stalinium.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class StaliniumAxeItem extends AxeItem {
    public StaliniumAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && state.is(BlockTags.LOGS)) {
            cutDownTree(level, pos, (Player)miningEntity, stack);
            return true;
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    private void cutDownTree(Level level, BlockPos start, Player player, ItemStack stack) {
        Set<BlockPos> toBreak = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty() && toBreak.size() < 256) {
            BlockPos current = queue.removeFirst();
            if (toBreak.contains(current)) continue;
            BlockState bs = level.getBlockState(current);
            if (bs.is(BlockTags.LOGS)) {
                toBreak.add(current);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = 0; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            BlockPos next = current.offset(dx, dy, dz);
                            if (!toBreak.contains(next)) {
                                queue.add(next);
                            }
                        }
                    }
                }
            }
        }
        for (BlockPos logPos : toBreak) {
            level.destroyBlock(logPos, true, player);
        }
    }


    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
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
