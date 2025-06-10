package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Stalinium.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.COMPRESSED_BEDROCK);
        blockWithItem(ModBlocks.STALINIUM_BLOCK);
        blockWithItem(ModBlocks.STALINIUM_ORE);
        ModelFile pressModel = models()
                .getBuilder("stalinium_press")
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/stalinium_press")));
        getVariantBuilder(ModBlocks.STALINIUM_PRESS.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(pressModel)
                        .build());
        simpleBlockItem(ModBlocks.STALINIUM_PRESS.get(), pressModel);

        ModelFile cacheModel = models()
                .getBuilder("stalinium_cache")
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/stalinium_cache")));
        getVariantBuilder(ModBlocks.STALINIUM_CACHE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(cacheModel)
                        .build());
        simpleBlockItem(ModBlocks.STALINIUM_CACHE.get(), cacheModel);
    }


    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
