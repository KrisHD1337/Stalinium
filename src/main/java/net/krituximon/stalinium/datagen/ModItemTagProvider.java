package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.item.ModItems;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Stalinium.MODID, existingFileHelper);
    }

    // Add your tag entries here.
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        this.tag(Tags.Items.INGOTS)
                .add(ModItems.STALINIUM_INGOT.get());
        this.tag(Tags.Items.NUGGETS)
                .add(ModItems.STALINIUM_NUGGET.get());
        this.tag(ItemTags.SWORDS)
                .add(ModItems.STALINIUM_SWORD.get());
        this.tag(ItemTags.AXES)
                .add(ModItems.STALINIUM_AXE.get());
        this.tag(ItemTags.SHOVELS)
                .add(ModItems.STALINIUM_SHOVEL.get());
        this.tag(ItemTags.HOES)
                .add(ModItems.STALINIUM_HOE.get());
        this.tag(ItemTags.PICKAXES)
                .add(ModItems.STALINIUM_PICKAXE.get());
        this.tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.STALINIUM_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.STALINIUM_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR)
                .add(ModItems.STALINIUM_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.STALINIUM_BOOTS.get());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.STALINIUM_HELMET.get())
                .add(ModItems.STALINIUM_CHESTPLATE.get())
                .add(ModItems.STALINIUM_LEGGINGS.get())
                .add(ModItems.STALINIUM_BOOTS.get());
    }
}
