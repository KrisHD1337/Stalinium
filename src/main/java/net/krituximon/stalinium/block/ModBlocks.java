package net.krituximon.stalinium.block;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Stalinium.MODID);

    public static final DeferredBlock<Block> COMPRESSED_BEDROCK = registerBlock("compressed_bedrock",
            () -> new NetheriteOnlyBlock(BlockBehaviour.Properties.of()
                    .strength(120f, 1200f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> STALINIUM_ORE = registerBlock("stalinium_ore",
            () -> new NetheriteOnlyBlock(BlockBehaviour.Properties.of()
                    .strength(50f, 1200f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHER_ORE)));

    public static final DeferredBlock<Block> STALINIUM_BLOCK = registerBlock("stalinium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(25f, 1200f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));



    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}