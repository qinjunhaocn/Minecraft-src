/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class WaterloggedVegetationPatchFeature
extends VegetationPatchFeature {
    public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> $$0) {
        super($$0);
    }

    @Override
    protected Set<BlockPos> placeGroundPatch(WorldGenLevel $$0, VegetationPatchConfiguration $$1, RandomSource $$2, BlockPos $$3, Predicate<BlockState> $$4, int $$5, int $$6) {
        Set<BlockPos> $$7 = super.placeGroundPatch($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        HashSet<BlockPos> $$8 = new HashSet<BlockPos>();
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (BlockPos $$10 : $$7) {
            if (WaterloggedVegetationPatchFeature.isExposed($$0, $$7, $$10, $$9)) continue;
            $$8.add($$10);
        }
        for (BlockPos $$11 : $$8) {
            $$0.setBlock($$11, Blocks.WATER.defaultBlockState(), 2);
        }
        return $$8;
    }

    private static boolean isExposed(WorldGenLevel $$0, Set<BlockPos> $$1, BlockPos $$2, BlockPos.MutableBlockPos $$3) {
        return WaterloggedVegetationPatchFeature.isExposedDirection($$0, $$2, $$3, Direction.NORTH) || WaterloggedVegetationPatchFeature.isExposedDirection($$0, $$2, $$3, Direction.EAST) || WaterloggedVegetationPatchFeature.isExposedDirection($$0, $$2, $$3, Direction.SOUTH) || WaterloggedVegetationPatchFeature.isExposedDirection($$0, $$2, $$3, Direction.WEST) || WaterloggedVegetationPatchFeature.isExposedDirection($$0, $$2, $$3, Direction.DOWN);
    }

    private static boolean isExposedDirection(WorldGenLevel $$0, BlockPos $$1, BlockPos.MutableBlockPos $$2, Direction $$3) {
        $$2.setWithOffset((Vec3i)$$1, $$3);
        return !$$0.getBlockState($$2).isFaceSturdy($$0, $$2, $$3.getOpposite());
    }

    @Override
    protected boolean placeVegetation(WorldGenLevel $$0, VegetationPatchConfiguration $$1, ChunkGenerator $$2, RandomSource $$3, BlockPos $$4) {
        if (super.placeVegetation($$0, $$1, $$2, $$3, $$4.below())) {
            BlockState $$5 = $$0.getBlockState($$4);
            if ($$5.hasProperty(BlockStateProperties.WATERLOGGED) && !$$5.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
                $$0.setBlock($$4, (BlockState)$$5.setValue(BlockStateProperties.WATERLOGGED, true), 2);
            }
            return true;
        }
        return false;
    }
}

