package net.krituximon.stalinium.item.custom;

import net.krituximon.stalinium.util.PlacedLogStorage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
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
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    private void cutDownTree(Level level, BlockPos start, Player player, ItemStack stack) {
        Set<BlockPos> toBreak = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        PlacedLogStorage storage = PlacedLogStorage.get((ServerLevel) level);

        while (!queue.isEmpty() && toBreak.size() < 256) {
            BlockPos current = queue.removeFirst();
            if (toBreak.contains(current)) continue;

            BlockState bs = level.getBlockState(current);
            if (storage.contains(current) || !bs.is(BlockTags.LOGS)) continue;

            toBreak.add(current);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        queue.add(current.offset(dx, dy, dz));
                    }
                }
            }
        }
        List<Player> allies = level
                .getEntitiesOfClass(Player.class,
                        player.getBoundingBox().inflate(10.0),
                        p -> p instanceof ServerPlayer);
        for (BlockPos logPos : toBreak) {
            BlockState before = level.getBlockState(logPos);
            Block sapling = getSaplingForLog(before.getBlock());
            Block plankBlock = getPlankForLog(before.getBlock());
            level.destroyBlock(logPos, true, player);
            if (sapling != null) {
                ItemStack saplingStack = new ItemStack(sapling);
                for (Player ally : allies) {
                    ally.addItem(saplingStack.copy());
                }
            }
            if (plankBlock != null) {
                ItemStack plankStack = new ItemStack(plankBlock);
                for (Player ally : allies) {
                    ally.addItem(plankStack.copy());
                }
            }
        }
    }
    
    private @Nullable Block getSaplingForLog(Block log) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(log);
        if (id == null ) return null;
        String path = id.getPath();
        if (!path.endsWith("_log")) return null;
        String saplingPath = path.substring(0, path.length() - 4) + "_sapling";
        ResourceLocation sapId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), saplingPath);
        return BuiltInRegistries.BLOCK.getOptional(sapId)
                .orElse(Blocks.OAK_SAPLING);
    }

    private @Nullable Block getPlankForLog(Block log) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(log);
        if (id == null) return null;
        String path = id.getPath();
        if (!path.endsWith("_log")) return null;
        String plankPath = path.substring(0, path.length() - 4) + "_planks";
        ResourceLocation plankId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), plankPath);
        return BuiltInRegistries.BLOCK.getOptional(plankId)
                .orElse(Blocks.OAK_PLANKS);
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
            tooltipComponents.add(Component.translatable("item.stalinium_axe.tooltip_shift"));
        } else {
            tooltipComponents.add(Component.translatable("item.stalinium_axe.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
