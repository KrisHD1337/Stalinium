package net.krituximon.stalinium.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.block.ModBlocks;
import net.krituximon.stalinium.recipe.StaliniumPressRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StaliniumPressRecipeCategory implements IRecipeCategory<StaliniumPressRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "stalinium_chamber");
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, "textures/gui/stalinium_press_gui.png");

    public static final RecipeType<StaliniumPressRecipe> STALINIUM_PRESS_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, StaliniumPressRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    /* --------------------------------------------------------------------- */
    /* GUI layout constants                                                  */
    /* --------------------------------------------------------------------- */
    private static final int[][] INPUT_SLOT_POSITIONS = {
            {54, 34},
            {54, 14},
            {54, 54}
    };
    private static final int OUTPUT_X = 104;
    private static final int OUTPUT_Y = 34;

    public StaliniumPressRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.STALINIUM_PRESS));
    }

    @Override
    public RecipeType<StaliniumPressRecipe> getRecipeType() {
        return STALINIUM_PRESS_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.stalinium.stalinium_press");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                      StaliniumPressRecipe recipe,
                      IFocusGroup focuses) {

    // -------------------- INPUT SLOTS --------------------
    var ingredients = recipe.getIngredients();
    int slotCount = Math.min(ingredients.size(), INPUT_SLOT_POSITIONS.length);

    for (int i = 0; i < slotCount; i++) {
        int x = INPUT_SLOT_POSITIONS[i][0];
        int y = INPUT_SLOT_POSITIONS[i][1];
        builder.addSlot(RecipeIngredientRole.INPUT, x, y)
               .addIngredients(ingredients.get(i));
    }

    // -------------------- OUTPUT SLOT --------------------
    var output = recipe.getResultItem(
            net.minecraft.client.Minecraft.getInstance()
                                          .level
                                          .registryAccess());   // <- real registry
    builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
           .addItemStack(output);
}
}