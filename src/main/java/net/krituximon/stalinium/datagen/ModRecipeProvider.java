package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STALINIUM_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.STALINIUM_INGOT.get())
                .unlockedBy("has_stalinium", has(ModItems.STALINIUM_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.STALINIUM_INGOT.get(), 9)
                .requires(ModBlocks.STALINIUM_BLOCK)
                .unlockedBy("has_stalinium_block", has(ModBlocks.STALINIUM_BLOCK)).save(recipeOutput);

    }
}
