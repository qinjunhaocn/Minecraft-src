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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VinesFeature
extends Feature<NoneFeatureConfiguration> {
    public VinesFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        $$0.config();
        if (!$$1.isEmptyBlock($$2)) {
            return false;
        }
        for (Direction $$3 : Direction.values()) {
            if ($$3 == Direction.DOWN || !VineBlock.isAcceptableNeighbour($$1, $$2.relative($$3), $$3)) continue;
            $$1.setBlock($$2, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace($$3), true), 2);
            return true;
        }
        return false;
    }
}

