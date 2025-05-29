package net.krituximon.stalinium.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * power  – ingredient in slot 0
 * nugget – ingredient in slot 1
 * fuel   – ingredient in slot 2
 */
public record StaliniumPressRecipe(NonNullList<Ingredient> ingredients,
                                   ItemStack output) implements Recipe<StaliniumPressRecipeInput> {

    /* --------------------------------------------------------------------- */
    /* Convenience accessors – referenced by the codec                       */
    /* --------------------------------------------------------------------- */
    public Ingredient power() {
        return ingredients.get(0);
    }

    public Ingredient nugget() {
        return ingredients.get(1);
    }

    public Ingredient fuel() {
        return ingredients.get(2);
    }

    /* --------------------------------------------------------------------- */
    /* Recipe implementation                                                 */
    /* --------------------------------------------------------------------- */
    @Override
    public boolean matches(StaliniumPressRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        if (ingredients.size() != 3) return false;   // malformed recipe JSON

        return power().test(input.power())
                && nugget().test(input.nugget())
                && fuel().test(input.fuel());
    }

    @Override
    public ItemStack assemble(StaliniumPressRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider prov) {
        return output;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STALINIUM_PRESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.STALINIUM_PRESS_TYPE.get();
    }

    /* --------------------------------------------------------------------- */
    /* Serializer                                                            */
    /* --------------------------------------------------------------------- */
    public static class Serializer implements RecipeSerializer<StaliniumPressRecipe> {

        /* ---------- Codec used for JSON files ---------- */
        public static final MapCodec<StaliniumPressRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("nugget").forGetter(StaliniumPressRecipe::nugget),
                        Ingredient.CODEC_NONEMPTY.fieldOf("power").forGetter(StaliniumPressRecipe::power),
                        Ingredient.CODEC_NONEMPTY.fieldOf("fuel").forGetter(StaliniumPressRecipe::fuel),
                        ItemStack.CODEC.fieldOf("result").forGetter(StaliniumPressRecipe::output)
                ).apply(inst, (p, n, f, res) -> {
                    NonNullList<Ingredient> list = NonNullList.of(Ingredient.EMPTY, p, n, f);
                    return new StaliniumPressRecipe(list, res);
                }));

        /* ---------- Codec used for network sync ---------- */
        private static final StreamCodec<RegistryFriendlyByteBuf, StaliniumPressRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, StaliniumPressRecipe::power,
                        Ingredient.CONTENTS_STREAM_CODEC, StaliniumPressRecipe::nugget,
                        Ingredient.CONTENTS_STREAM_CODEC, StaliniumPressRecipe::fuel,
                        ItemStack.STREAM_CODEC, StaliniumPressRecipe::output,
                        (p, n, f, res) -> {
                            NonNullList<Ingredient> list = NonNullList.of(Ingredient.EMPTY, p, n, f);
                            return new StaliniumPressRecipe(list, res);
                        }
                );

        @Override
        public MapCodec<StaliniumPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StaliniumPressRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}