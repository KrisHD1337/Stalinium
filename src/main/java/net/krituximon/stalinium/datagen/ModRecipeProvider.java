package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;


import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        public RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Stalinium Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModBlocks.STALINIUM_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.STALINIUM_INGOT.get())
                .unlockedBy("has_stalinium", has(ModItems.STALINIUM_INGOT)).save(output);

        shaped(RecipeCategory.MISC, ModItems.SOVIET_ANTHEM_MUSIC_DISC.get())
                .pattern("BBB")
                .pattern("BSB")
                .pattern("BBB")
                .define('B', ModItems.STALINIUM_INGOT.get())
                .define('S', Items.GOLD_BLOCK)
                .unlockedBy("has_stalinium", has(ModItems.STALINIUM_INGOT)).save(output);

        shapeless(RecipeCategory.MISC, ModItems.STALINIUM_INGOT.get(), 9)
                .requires(ModBlocks.STALINIUM_BLOCK)
                .unlockedBy("has_stalinium_block", has(ModBlocks.STALINIUM_BLOCK)).save(output);

        shaped(RecipeCategory.MISC, ModBlocks.STALINIUM_PRESS.get())
                .pattern("INI")
                .pattern("NSN")
                .pattern("INI")
                .define('I', Items.NETHERITE_INGOT)
                .define('N', ModItems.STALINIUM_NUGGET.get())
                .define('S', Items.NETHER_STAR)
                .unlockedBy("has_nugget", has(ModItems.STALINIUM_NUGGET)).save(output);

        shaped(RecipeCategory.MISC, ModBlocks.STALINIUM_CACHE.get())
                .pattern("ISI")
                .pattern("SES")
                .pattern("SIS")
                .define('I', Items.IRON_BLOCK)
                .define('E', Items.ENDER_CHEST)
                .define('S', ModItems.STALINIUM_INGOT.get())
                .unlockedBy("has_nugget", has(ModItems.STALINIUM_NUGGET)).save(output);

        shaped(RecipeCategory.MISC, ModItems.STALINIUM_SMITHING_TEMPLATE.get(), 4)
                .pattern("NNN")
                .pattern("NIN")
                .pattern("NSN")
                .define('I', ModItems.STALINIUM_INGOT)
                .define('N', ModItems.STALINIUM_NUGGET.get())
                .define('S', Items.IRON_BLOCK)
                .unlockedBy("has_nugget", has(ModItems.STALINIUM_NUGGET)).save(output);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_SWORD),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_SWORD.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_SWORD))
                .save(output, "smithing/stalinium_sword");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_PICKAXE),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.STALINIUM_PICKAXE.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_PICKAXE))
                .save(output, "smithing/stalinium_pickaxe");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_AXE),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.STALINIUM_AXE.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_AXE))
                .save(output, "smithing/stalinium_axe");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_SHOVEL),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.STALINIUM_SHOVEL.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_SHOVEL))
                .save(output, "smithing/stalinium_shovel");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_HOE),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.STALINIUM_HOE.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_HOE))
                .save(output, "smithing/stalinium_hoe");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_HELMET),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_HELMET.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_HELMET))
                .save(output, "smithing/stalinium_helmet");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_CHESTPLATE),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_CHESTPLATE.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_CHESTPLATE))
                .save(output, "smithing/stalinium_chestplate");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_LEGGINGS),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_LEGGINGS.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_LEGGINGS))
                .save(output, "smithing/stalinium_leggings");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.NETHERITE_BOOTS),
                        Ingredient.of(ModItems.STALINIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_BOOTS.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_BOOTS))
                .save(output, "smithing/stalinium_boots");

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.STALINIUM_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.MACE),
                        Ingredient.of(ModBlocks.STALINIUM_BLOCK.get()),
                        RecipeCategory.COMBAT,
                        ModItems.STALINIUM_MACE.get()
                )
                .unlocks("has_netherite", has(Items.NETHERITE_BOOTS))
                .save(output, "smithing/stalinium_mace");
    }
}
