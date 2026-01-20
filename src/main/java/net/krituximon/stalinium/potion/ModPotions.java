package net.krituximon.stalinium.potion;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, Stalinium.MODID);
    
    public static final Holder<Potion> VODKA = POTIONS.register("vodka", 
            () -> new Potion("vodka", new MobEffectInstance(MobEffects.CONFUSION, 1200, 0)));
    
    public static final Holder<Potion> WEAPONS_GRADE_VODKA = POTIONS.register("weapons_grade_vodka",
            () -> new Potion("weapons_grade_vodka", new MobEffectInstance(ModEffects.STALINIUM_CHARGE, 1200, 2)));
    
    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
