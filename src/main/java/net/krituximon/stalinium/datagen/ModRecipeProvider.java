package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOVIET_ANTHEM_MUSIC_DISC.get())
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('B', ModItems.STALINIUM_INGOT.get())
                .define('S', Items.GOLD_BLOCK)
                .unlockedBy("has_stalinium", has(ModItems.STALINIUM_INGOT)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.STALINIUM_INGOT.get(), 9)
                .requires(ModBlocks.STALINIUM_BLOCK)
                .unlockedBy("has_stalinium_block", has(ModBlocks.STALINIUM_BLOCK)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.STALINIUM_PRESS.get())
                .pattern("INI")
                .pattern("NSN")
                .pattern("INI")
                .define('I', Items.NETHERITE_INGOT)
                .define('N', ModItems.STALINIUM_NUGGET.get())
                .define('S', Items.NETHER_STAR)
                .unlockedBy("has_nugget", has(ModItems.STALINIUM_NUGGET)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STALINIUM_SWORD.get())
                .pattern("I")
                .pattern("I")
                .pattern("G")
                .define('I', ModItems.STALINIUM_INGOT.get())
                .define('G', Items.GOLD_BLOCK)
                .unlockedBy("has_ingot", has(ModItems.STALINIUM_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STALINIUM_SHOVEL.get())
                .pattern("I")
                .pattern("G")
                .pattern("G")
                .define('I', ModItems.STALINIUM_INGOT.get())
                .define('G', Items.GOLD_BLOCK)
                .unlockedBy("has_ingot", has(ModItems.STALINIUM_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STALINIUM_AXE.get())
                .pattern("II")
                .pattern("IG")
                .pattern(" G")
                .define('I', ModItems.STALINIUM_INGOT.get())
                .define('G', Items.GOLD_BLOCK)
                .unlockedBy("has_ingot", has(ModItems.STALINIUM_INGOT)).save(recipeOutput);
    }
}
