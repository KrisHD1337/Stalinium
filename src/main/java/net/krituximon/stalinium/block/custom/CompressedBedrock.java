package net.krituximon.stalinium.block.custom;

import net.krituximon.stalinium.block.NetheriteOnlyBlock;
import net.krituximon.stalinium.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CompressedBedrock extends NetheriteOnlyBlock {
    public CompressedBedrock(Properties properties) {
        super(properties);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = Direction.getRandom(random);
            if (direction != Direction.UP) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (!state.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                    double d0 = direction.getStepX() == 0 ? random.nextDouble() : (double) 0.5F + (double) direction.getStepX() * 0.6;
                    double d1 = direction.getStepY() == 0 ? random.nextDouble() : (double) 0.5F + (double) direction.getStepY() * 0.6;
                    double d2 = direction.getStepZ() == 0 ? random.nextDouble() : (double) 0.5F + (double) direction.getStepZ() * 0.6;
                    level.addParticle(ModParticles.BLOOD_PARTICLE.get(), (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, (double) 0.0F, (double) 0.0F, (double) 0.0F);
                }
            }
        }
    }
}