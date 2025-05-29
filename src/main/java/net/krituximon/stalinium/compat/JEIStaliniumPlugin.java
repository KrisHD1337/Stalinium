package net.krituximon.stalinium.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.recipe.ModRecipes;
import net.krituximon.stalinium.recipe.StaliniumPressRecipe;
import net.krituximon.stalinium.screen.custom.StaliniumPressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIStaliniumPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new StaliniumPressRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<StaliniumPressRecipe> staliniumPressRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.STALINIUM_PRESS_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(StaliniumPressRecipeCategory.STALINIUM_PRESS_RECIPE_RECIPE_TYPE, staliniumPressRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(StaliniumPressScreen.class, 74, 30, 22, 20,
                StaliniumPressRecipeCategory.STALINIUM_PRESS_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.STALINIUM_PRESS.get().asItem()),
                StaliniumPressRecipeCategory.STALINIUM_PRESS_RECIPE_RECIPE_TYPE);
    }
}
