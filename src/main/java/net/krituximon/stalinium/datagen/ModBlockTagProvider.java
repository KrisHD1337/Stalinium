package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.util.ModTags;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Stalinium.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(ModBlocks.STALINIUM_BLOCK.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.STALINIUM_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        ModBlocks.STALINIUM_BLOCK.get(),
                        ModBlocks.STALINIUM_ORE.get(),
                        ModBlocks.COMPRESSED_BEDROCK.get()
                );
    }
}
