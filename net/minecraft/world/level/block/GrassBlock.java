/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GrassBlock
extends SpreadingSnowyDirtBlock
implements BonemealableBlock {
    public static final MapCodec<GrassBlock> CODEC = GrassBlock.simpleCodec(GrassBlock::new);

    public MapCodec<GrassBlock> codec() {
        return CODEC;
    }

    public GrassBlock(BlockBehaviour.Properties $$0) {
        super($$0);
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
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        BlockPos $$4 = $$2.above();
        BlockState $$5 = Blocks.SHORT_GRASS.defaultBlockState();
        Optional $$6 = $$0.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(VegetationPlacements.GRASS_BONEMEAL);
        block0: for (int $$7 = 0; $$7 < 128; ++$$7) {
            Holder $$15;
            BonemealableBlock $$11;
            BlockPos $$8 = $$4;
            for (int $$9 = 0; $$9 < $$7 / 16; ++$$9) {
                if (!$$0.getBlockState(($$8 = $$8.offset($$1.nextInt(3) - 1, ($$1.nextInt(3) - 1) * $$1.nextInt(3) / 2, $$1.nextInt(3) - 1)).below()).is(this) || $$0.getBlockState($$8).isCollisionShapeFullBlock($$0, $$8)) continue block0;
            }
            BlockState $$10 = $$0.getBlockState($$8);
            if ($$10.is($$5.getBlock()) && $$1.nextInt(10) == 0 && ($$11 = (BonemealableBlock)((Object)$$5.getBlock())).isValidBonemealTarget($$0, $$8, $$10)) {
                $$11.performBonemeal($$0, $$1, $$8, $$10);
            }
            if (!$$10.isAir()) continue;
            if ($$1.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> $$12 = $$0.getBiome($$8).value().getGenerationSettings().getFlowerFeatures();
                if ($$12.isEmpty()) continue;
                int $$13 = $$1.nextInt($$12.size());
                Holder<PlacedFeature> $$14 = ((RandomPatchConfiguration)$$12.get($$13).config()).feature();
            } else {
                if (!$$6.isPresent()) continue;
                $$15 = (Holder)$$6.get();
            }
            ((PlacedFeature)((Object)$$15.value())).place($$0, $$0.getChunkSource().getGenerator(), $$1, $$8);
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}

