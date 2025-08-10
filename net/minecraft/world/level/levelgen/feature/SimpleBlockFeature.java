/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature
extends Feature<SimpleBlockConfiguration> {
    public SimpleBlockFeature(Codec<SimpleBlockConfiguration> $$0) {
        super($$0);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> $$0) {
        SimpleBlockConfiguration $$1 = $$0.config();
        WorldGenLevel $$2 = $$0.level();
        BlockPos $$3 = $$0.origin();
        BlockState $$4 = $$1.toPlace().getState($$0.random(), $$3);
        if (!$$4.canSurvive($$2, $$3)) return false;
        if ($$4.getBlock() instanceof DoublePlantBlock) {
            if (!$$2.isEmptyBlock($$3.above())) return false;
            DoublePlantBlock.placeAt($$2, $$4, $$3, 2);
        } else if ($$4.getBlock() instanceof MossyCarpetBlock) {
            MossyCarpetBlock.placeAt($$2, $$3, $$2.getRandom(), 2);
        } else {
            $$2.setBlock($$3, $$4, 2);
        }
        if (!$$1.scheduleTick()) return true;
        $$2.scheduleTick($$3, $$2.getBlockState($$3).getBlock(), 1);
        return true;
    }
}

