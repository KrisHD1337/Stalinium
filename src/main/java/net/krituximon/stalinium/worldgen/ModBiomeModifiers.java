package net.krituximon.stalinium.worldgen;

import net.krituximon.stalinium.Stalinium;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_STALINIUM_ORE = registerKey("add_stalinium_ore");
    public static final ResourceKey<BiomeModifier> ADD_COMPRESSED_BEDROCK = registerKey("add_compressed_bedrock");
    public static void bootstrap(BootstrapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = ctx.lookup(Registries.PLACED_FEATURE);

        HolderSet<PlacedFeature> featureSet = HolderSet.direct(
                placed.getOrThrow(ModPlacedFeatures.STALINIUM_VEIN_PLACED_KEY)
        );

        ctx.register(ADD_STALINIUM_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        featureSet,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }


    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Stalinium.MODID, name));
    }
}