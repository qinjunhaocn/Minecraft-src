/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class FireflyBushBlock
extends VegetationBlock
implements BonemealableBlock {
    private static final double FIREFLY_CHANCE_PER_TICK = 0.7;
    private static final double FIREFLY_HORIZONTAL_RANGE = 10.0;
    private static final double FIREFLY_VERTICAL_RANGE = 5.0;
    private static final int FIREFLY_SPAWN_MAX_BRIGHTNESS_LEVEL = 13;
    private static final int FIREFLY_AMBIENT_SOUND_CHANCE_ONE_IN = 30;
    public static final MapCodec<FireflyBushBlock> CODEC = FireflyBushBlock.simpleCodec(FireflyBushBlock::new);

    public FireflyBushBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected MapCodec<? extends FireflyBushBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(30) == 0 && $$1.isMoonVisible() && $$1.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$2) <= $$2.getY()) {
            $$1.playLocalSound($$2, SoundEvents.FIREFLY_BUSH_IDLE, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
        if ($$1.getMaxLocalRawBrightness($$2) <= 13 && $$3.nextDouble() <= 0.7) {
            double $$4 = (double)$$2.getX() + $$3.nextDouble() * 10.0 - 5.0;
            double $$5 = (double)$$2.getY() + $$3.nextDouble() * 5.0;
            double $$6 = (double)$$2.getZ() + $$3.nextDouble() * 10.0 - 5.0;
            $$1.addParticle(ParticleTypes.FIREFLY, $$4, $$5, $$6, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return BonemealableBlock.hasSpreadableNeighbourPos($$0, $$1, $$2);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$12, BlockPos $$2, BlockState $$3) {
        BonemealableBlock.findSpreadableNeighbourPos($$0, $$2, $$3).ifPresent($$1 -> $$0.setBlockAndUpdate((BlockPos)$$1, this.defaultBlockState()));
    }
}

