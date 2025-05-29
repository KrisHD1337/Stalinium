package net.krituximon.stalinium.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record StaliniumPressRecipeInput(ItemStack power, ItemStack nugget, ItemStack fuel) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        ItemStack var10000 = null;
        switch (i) {
            case 1 -> var10000 = power;
            case 0 -> var10000 = nugget;
            case 2 -> var10000 = fuel;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + i);
        }

        return var10000;
    }

    @Override
    public int size() {
        return 3;
    }
}
