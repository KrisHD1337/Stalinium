package net.krituximon.stalinium.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class StaliniumPickaxeItem extends PickaxeItem {
    
    public StaliniumPickaxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack,
                             Level level,
                             BlockState state,
                             BlockPos pos,
                             LivingEntity miner) {
        if (!level.isClientSide && state.getBlock() instanceof DropExperienceBlock && miner instanceof Player player) {
            mineVein(player, level, pos, state.getBlock());
        }
        List<Player> comrades = level.getEntitiesOfClass(
                Player.class,
                miner.getBoundingBox().inflate(8),
                p -> p instanceof ServerPlayer && miner.isAlliedTo(p)
        );
        RandomSource rand = level.getRandom();
        if (rand.nextFloat() < 0.5f) {
            for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, null)) {
                for (Player buddy : comrades) {
                    buddy.addItem(drop.copy());
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    private void mineVein(Player player,
                          Level level,
                          BlockPos start,
                          Block block) {
        Set<BlockPos> toBreak = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty() && toBreak.size() < 128) {
            BlockPos here = queue.removeFirst();
            if (toBreak.add(here)) {
                for (Direction d : Direction.values()) {
                    BlockPos next = here.relative(d);
                    if (level.getBlockState(next).is(block)) {
                        queue.add(next);
                    }
                }
            }
        }

        for (BlockPos p : toBreak) {
            level.destroyBlock(p, true, player);
        }
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