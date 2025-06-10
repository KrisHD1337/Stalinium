package net.krituximon.stalinium.block.custom;

import com.mojang.serialization.MapCodec;
import net.krituximon.stalinium.block.entity.ModBlockEntities;
import net.krituximon.stalinium.block.entity.StaliniumCacheBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class StaliniumCacheBlock extends BaseEntityBlock {
    // 1) supply a real codec so the block actually constructs from your registry
    public static final MapCodec<StaliniumCacheBlock> CODEC =
            simpleCodec(StaliniumCacheBlock::new);

    public StaliniumCacheBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.STALINIUM_CACHE_BE.get().create(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            var container = StaliniumCacheBlockEntity.getPartyInventory(sp);
            sp.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> ChestMenu.threeRows(id, inv, container),
                    Component.translatable("container.stalinium.cache")
            ), pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    public void onRemove(BlockState oldSt, Level world, BlockPos pos,
                         BlockState newSt, boolean moving) {
        if (!oldSt.is(newSt.getBlock())) {
            var be = world.getBlockEntity(pos);
            if (be instanceof StaliniumCacheBlockEntity cache) {
                cache.drops();
            }
        }
        super.onRemove(oldSt, world, pos, newSt, moving);
    }
}