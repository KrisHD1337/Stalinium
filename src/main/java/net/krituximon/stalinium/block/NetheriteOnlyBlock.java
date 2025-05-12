package net.krituximon.stalinium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NetheriteOnlyBlock extends Block {
    public NetheriteOnlyBlock(BlockBehaviour.Properties props) {
        super(props);
    }
    
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof PickaxeItem)) {
            return false;
        }
        return ((PickaxeItem) stack.getItem()).getTier() == Tiers.NETHERITE;
    }
}
