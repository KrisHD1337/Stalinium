package net.krituximon.stalinium.worldgen;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> STALINIUM_VEIN_PLACED_KEY =
            registerKey("stalinium_vein_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        Holder<ConfiguredFeature<?, ?>> cfg =
                context.lookup(Registries.CONFIGURED_FEATURE)
                        .getOrThrow(ModConfiguredFeatures.STALINIUM_VEIN_KEY);
        List<PlacementModifier> mods = List.of(
                CountPlacement.of(1),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.absolute(-64),
                        VerticalAnchor.absolute(-61)
                ),
                BiomeFilter.biome()
        );
        context.register(STALINIUM_VEIN_PLACED_KEY, new PlacedFeature(cfg, mods));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, name));
    }
}