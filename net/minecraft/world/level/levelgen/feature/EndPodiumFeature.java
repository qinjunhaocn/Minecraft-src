/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature
extends Feature<NoneFeatureConfiguration> {
    public static final int PODIUM_RADIUS = 4;
    public static final int PODIUM_PILLAR_HEIGHT = 4;
    public static final int RIM_RADIUS = 1;
    public static final float CORNER_ROUNDING = 0.5f;
    private static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
    private final boolean active;

    public static BlockPos getLocation(BlockPos $$0) {
        return END_PODIUM_LOCATION.offset($$0);
    }

    public EndPodiumFeature(boolean $$0) {
        super(NoneFeatureConfiguration.CODEC);
        this.active = $$0;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        for (BlockPos $$3 : BlockPos.betweenClosed(new BlockPos($$1.getX() - 4, $$1.getY() - 1, $$1.getZ() - 4), new BlockPos($$1.getX() + 4, $$1.getY() + 32, $$1.getZ() + 4))) {
            boolean $$4 = $$3.closerThan($$1, 2.5);
            if (!$$4 && !$$3.closerThan($$1, 3.5)) continue;
            if ($$3.getY() < $$1.getY()) {
                if ($$4) {
                    this.setBlock($$2, $$3, Blocks.BEDROCK.defaultBlockState());
                    continue;
                }
                if ($$3.getY() >= $$1.getY()) continue;
                if (this.active) {
                    this.dropPreviousAndSetBlock($$2, $$3, Blocks.END_STONE);
                    continue;
                }
                this.setBlock($$2, $$3, Blocks.END_STONE.defaultBlockState());
                continue;
            }
            if ($$3.getY() > $$1.getY()) {
                if (this.active) {
                    this.dropPreviousAndSetBlock($$2, $$3, Blocks.AIR);
                    continue;
                }
                this.setBlock($$2, $$3, Blocks.AIR.defaultBlockState());
                continue;
            }
            if (!$$4) {
                this.setBlock($$2, $$3, Blocks.BEDROCK.defaultBlockState());
                continue;
            }
            if (this.active) {
                this.dropPreviousAndSetBlock($$2, new BlockPos($$3), Blocks.END_PORTAL);
                continue;
            }
            this.setBlock($$2, new BlockPos($$3), Blocks.AIR.defaultBlockState());
        }
        for (int $$5 = 0; $$5 < 4; ++$$5) {
            this.setBlock($$2, $$1.above($$5), Blocks.BEDROCK.defaultBlockState());
        }
        BlockPos $$6 = $$1.above(2);
        for (Direction $$7 : Direction.Plane.HORIZONTAL) {
            this.setBlock($$2, $$6.relative($$7), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, $$7));
        }
        return true;
    }

    private void dropPreviousAndSetBlock(WorldGenLevel $$0, BlockPos $$1, Block $$2) {
        if (!$$0.getBlockState($$1).is($$2)) {
            $$0.destroyBlock($$1, true, null);
            this.setBlock($$0, $$1, $$2.defaultBlockState());
        }
    }
}

