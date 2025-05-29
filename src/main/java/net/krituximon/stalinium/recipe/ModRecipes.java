package net.krituximon.stalinium.recipe;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Stalinium.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Stalinium.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StaliniumPressRecipe>> STALINIUM_PRESS_SERIALIZER =
            SERIALIZERS.register("stalinium_press", StaliniumPressRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<StaliniumPressRecipe>> STALINIUM_PRESS_TYPE =
            TYPES.register("stalinium_press", () -> new RecipeType<StaliniumPressRecipe>() {
                @Override
                public String toString() {
                    return "stalinium_press";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
