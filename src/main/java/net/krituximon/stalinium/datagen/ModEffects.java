package net.krituximon.stalinium.datagen;

import net.krituximon.stalinium.Stalinium;
import net.krituximon.stalinium.effect.StaliniumChargeEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Stalinium.MODID);

    public static final DeferredHolder<MobEffect, StaliniumChargeEffect> STALINIUM_CHARGE =
            EFFECTS.register("stalinium_charge", StaliniumChargeEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
