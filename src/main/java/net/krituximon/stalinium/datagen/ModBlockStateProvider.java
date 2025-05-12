package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Stalinium.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.COMPRESSED_BEDROCK.get(),
                models().cubeAll("compressed_bedrock", modLoc("block/compressed_bedrock")));
        simpleBlock(ModBlocks.STALINIUM_ORE.get(),
                models().cubeAll("stalinium_ore",     modLoc("block/stalinium_ore")));
        simpleBlock(ModBlocks.STALINIUM_BLOCK.get(),
                models().cubeAll("stalinium_block",   modLoc("block/stalinium_block")));
    }


    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
