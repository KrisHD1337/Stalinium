package net.krituximon.stalinium.event;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.item.ModItems;
import net.krituximon.stalinium.potion.ModPotions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = Stalinium.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {
    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addMix(Potions.AWKWARD, Items.POTATO, ModPotions.VODKA);
        builder.addMix(ModPotions.VODKA, ModItems.STALINIUM_INGOT.get(), ModPotions.WEAPONS_GRADE_VODKA);
    }
}
