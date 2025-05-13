package net.krituximon.stalinium.worldgen;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Stalinium.MODID);
    public static final DeferredHolder<Feature<?>, StaliniumVeinFeature> STALINIUM_VEIN =
            FEATURES.register("stalinium_vein",
                    () -> new StaliniumVeinFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {}
    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
