/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SnowAndFreezeFeature
extends Feature<NoneFeatureConfiguration> {
    public SnowAndFreezeFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        BlockPos.MutableBlockPos $$3 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (int $$5 = 0; $$5 < 16; ++$$5) {
            for (int $$6 = 0; $$6 < 16; ++$$6) {
                int $$7 = $$2.getX() + $$5;
                int $$8 = $$2.getZ() + $$6;
                int $$9 = $$1.getHeight(Heightmap.Types.MOTION_BLOCKING, $$7, $$8);
                $$3.set($$7, $$9, $$8);
                $$4.set($$3).move(Direction.DOWN, 1);
                Biome $$10 = $$1.getBiome($$3).value();
                if ($$10.shouldFreeze($$1, $$4, false)) {
                    $$1.setBlock($$4, Blocks.ICE.defaultBlockState(), 2);
                }
                if (!$$10.shouldSnow($$1, $$3)) continue;
                $$1.setBlock($$3, Blocks.SNOW.defaultBlockState(), 2);
                BlockState $$11 = $$1.getBlockState($$4);
                if (!$$11.hasProperty(SnowyDirtBlock.SNOWY)) continue;
                $$1.setBlock($$4, (BlockState)$$11.setValue(SnowyDirtBlock.SNOWY, true), 2);
            }
        }
        return true;
    }
}

