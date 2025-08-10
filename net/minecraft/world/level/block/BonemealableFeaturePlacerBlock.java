/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BonemealableFeaturePlacerBlock
extends Block
implements BonemealableBlock {
    public static final MapCodec<BonemealableFeaturePlacerBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter($$0 -> $$0.feature), BonemealableFeaturePlacerBlock.propertiesCodec()).apply((Applicative)$$02, BonemealableFeaturePlacerBlock::new));
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;

    public MapCodec<BonemealableFeaturePlacerBlock> codec() {
        return CODEC;
    }

    public BonemealableFeaturePlacerBlock(ResourceKey<ConfiguredFeature<?, ?>> $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.feature = $$0;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return $$0.getBlockState($$1.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$02, RandomSource $$1, BlockPos $$2, BlockState $$32) {
        $$02.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.get(this.feature)).ifPresent($$3 -> ((ConfiguredFeature)((Object)((Object)$$3.value()))).place($$02, $$02.getChunkSource().getGenerator(), $$1, $$2.above()));
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}

