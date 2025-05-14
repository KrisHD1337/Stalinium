package net.krituximon.stalinium.worldgen;

import com.mojang.serialization.Codec;
import net.krituximon.stalinium.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StaliniumVeinFeature extends Feature<NoneFeatureConfiguration> {
    public StaliniumVeinFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        LevelAccessor world = ctx.level();
        BlockPos center = ctx.origin();
        RandomSource rand = ctx.random();
        if (center.getY() > -60) return false;
        BlockState bedrock = ModBlocks.COMPRESSED_BEDROCK.get().defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    world.setBlock(pos, bedrock, 2);
                }
            }
        }

        // put the ore in the middle
        BlockState ore = ModBlocks.STALINIUM_ORE.get().defaultBlockState();
        world.setBlock(center, ore, 2);

        return true;
    }
}
