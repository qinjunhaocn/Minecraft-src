/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseBasedStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseThresholdProvider
extends NoiseBasedStateProvider {
    public static final MapCodec<NoiseThresholdProvider> CODEC = RecordCodecBuilder.mapCodec($$02 -> NoiseThresholdProvider.noiseCodec($$02).and($$02.group((App)Codec.floatRange((float)-1.0f, (float)1.0f).fieldOf("threshold").forGetter($$0 -> Float.valueOf($$0.threshold)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("high_chance").forGetter($$0 -> Float.valueOf($$0.highChance)), (App)BlockState.CODEC.fieldOf("default_state").forGetter($$0 -> $$0.defaultState), (App)ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("low_states").forGetter($$0 -> $$0.lowStates), (App)ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("high_states").forGetter($$0 -> $$0.highStates))).apply((Applicative)$$02, NoiseThresholdProvider::new));
    private final float threshold;
    private final float highChance;
    private final BlockState defaultState;
    private final List<BlockState> lowStates;
    private final List<BlockState> highStates;

    public NoiseThresholdProvider(long $$0, NormalNoise.NoiseParameters $$1, float $$2, float $$3, float $$4, BlockState $$5, List<BlockState> $$6, List<BlockState> $$7) {
        super($$0, $$1, $$2);
        this.threshold = $$3;
        this.highChance = $$4;
        this.defaultState = $$5;
        this.lowStates = $$6;
        this.highStates = $$7;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.NOISE_THRESHOLD_PROVIDER;
    }

    @Override
    public BlockState getState(RandomSource $$0, BlockPos $$1) {
        double $$2 = this.getNoiseValue($$1, this.scale);
        if ($$2 < (double)this.threshold) {
            return Util.getRandom(this.lowStates, $$0);
        }
        if ($$0.nextFloat() < this.highChance) {
            return Util.getRandom(this.highStates, $$0);
        }
        return this.defaultState;
    }
}

