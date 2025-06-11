package net.krituximon.stalinium.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.recipe.StaliniumPressRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class StaliniumPressRecipeCategory implements IRecipeCategory<StaliniumPressRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_press");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "textures/gui/stalinium_press_gui.png");

    public static final RecipeType<StaliniumPressRecipe> TYPE =
            new RecipeType<>(UID, StaliniumPressRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private static final int INPUT_X = 54;
    private static final int INPUT_Y = 34;
    private static final int OUTPUT_X = 104;
    private static final int OUTPUT_Y = 34;

    public StaliniumPressRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.STALINIUM_PRESS)
        );
    }

    @Override
    public RecipeType<StaliniumPressRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.stalinium.stalinium_press");
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return icon;
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          StaliniumPressRecipe recipe,
                          IFocusGroup focuses) {
        IRecipeSlotBuilder inputslot = builder
                .addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y);

        Ingredient nuggetIng = recipe.getIngredients().get(1);
        for (ItemStack stack : nuggetIng.getItems()) {
            ItemStack copy = stack.copy();
            copy.setCount(9);
            inputslot.addItemStack(copy);
        }
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(
                        recipe.getResultItem(
                                Minecraft.getInstance()
                                        .level
                                        .registryAccess()
                        )
                );
    }
}
