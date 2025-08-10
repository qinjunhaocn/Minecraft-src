/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature
extends Feature<TreeConfiguration> {
    private static final int BLOCK_UPDATE_FLAGS = 19;

    public TreeFeature(Codec<TreeConfiguration> $$0) {
        super($$0);
    }

    public static boolean isVine(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, $$0 -> $$0.is(Blocks.VINE));
    }

    public static boolean isAirOrLeaves(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, $$0 -> $$0.isAir() || $$0.is(BlockTags.LEAVES));
    }

    private static void setBlockKnownShape(LevelWriter $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, $$2, 19);
    }

    public static boolean validTreePos(LevelSimulatedReader $$02, BlockPos $$1) {
        return $$02.isStateAtPosition($$1, $$0 -> $$0.isAir() || $$0.is(BlockTags.REPLACEABLE_BY_TREES));
    }

    private boolean doPlace(WorldGenLevel $$0, RandomSource $$1, BlockPos $$22, BiConsumer<BlockPos, BlockState> $$3, BiConsumer<BlockPos, BlockState> $$4, FoliagePlacer.FoliageSetter $$5, TreeConfiguration $$6) {
        int $$72 = $$6.trunkPlacer.getTreeHeight($$1);
        int $$8 = $$6.foliagePlacer.foliageHeight($$1, $$72, $$6);
        int $$9 = $$72 - $$8;
        int $$10 = $$6.foliagePlacer.foliageRadius($$1, $$9);
        BlockPos $$11 = $$6.rootPlacer.map($$2 -> $$2.getTrunkOrigin($$22, $$1)).orElse($$22);
        int $$12 = Math.min($$22.getY(), $$11.getY());
        int $$13 = Math.max($$22.getY(), $$11.getY()) + $$72 + 1;
        if ($$12 < $$0.getMinY() + 1 || $$13 > $$0.getMaxY() + 1) {
            return false;
        }
        OptionalInt $$14 = $$6.minimumSize.minClippedHeight();
        int $$15 = this.getMaxFreeTreeHeight($$0, $$72, $$11, $$6);
        if ($$15 < $$72 && ($$14.isEmpty() || $$15 < $$14.getAsInt())) {
            return false;
        }
        if ($$6.rootPlacer.isPresent() && !$$6.rootPlacer.get().placeRoots($$0, $$3, $$1, $$22, $$11, $$6)) {
            return false;
        }
        List<FoliagePlacer.FoliageAttachment> $$16 = $$6.trunkPlacer.placeTrunk($$0, $$4, $$1, $$15, $$11, $$6);
        $$16.forEach($$7 -> $$0.foliagePlacer.createFoliage($$0, $$5, $$1, $$6, $$15, (FoliagePlacer.FoliageAttachment)$$7, $$8, $$10));
        return true;
    }

    private int getMaxFreeTreeHeight(LevelSimulatedReader $$0, int $$1, BlockPos $$2, TreeConfiguration $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (int $$5 = 0; $$5 <= $$1 + 1; ++$$5) {
            int $$6 = $$3.minimumSize.getSizeAtHeight($$1, $$5);
            for (int $$7 = -$$6; $$7 <= $$6; ++$$7) {
                for (int $$8 = -$$6; $$8 <= $$6; ++$$8) {
                    $$4.setWithOffset($$2, $$7, $$5, $$8);
                    if ($$3.trunkPlacer.isFree($$0, $$4) && ($$3.ignoreVines || !TreeFeature.isVine($$0, $$4))) continue;
                    return $$5 - 2;
                }
            }
        }
        return $$1;
    }

    @Override
    protected void setBlock(LevelWriter $$0, BlockPos $$1, BlockState $$2) {
        TreeFeature.setBlockKnownShape($$0, $$1, $$2);
    }

    @Override
    public final boolean place(FeaturePlaceContext<TreeConfiguration> $$0) {
        final WorldGenLevel $$12 = $$0.level();
        RandomSource $$22 = $$0.random();
        BlockPos $$32 = $$0.origin();
        TreeConfiguration $$42 = $$0.config();
        HashSet<BlockPos> $$5 = Sets.newHashSet();
        HashSet<BlockPos> $$6 = Sets.newHashSet();
        final HashSet<BlockPos> $$7 = Sets.newHashSet();
        HashSet $$8 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> $$9 = ($$2, $$3) -> {
            $$5.add($$2.immutable());
            $$12.setBlock((BlockPos)$$2, (BlockState)$$3, 19);
        };
        BiConsumer<BlockPos, BlockState> $$10 = ($$2, $$3) -> {
            $$6.add($$2.immutable());
            $$12.setBlock((BlockPos)$$2, (BlockState)$$3, 19);
        };
        FoliagePlacer.FoliageSetter $$11 = new FoliagePlacer.FoliageSetter(){

            @Override
            public void set(BlockPos $$0, BlockState $$1) {
                $$7.add($$0.immutable());
                $$12.setBlock($$0, $$1, 19);
            }

            @Override
            public boolean isSet(BlockPos $$0) {
                return $$7.contains($$0);
            }
        };
        BiConsumer<BlockPos, BlockState> $$122 = ($$2, $$3) -> {
            $$8.add($$2.immutable());
            $$12.setBlock((BlockPos)$$2, (BlockState)$$3, 19);
        };
        boolean $$13 = this.doPlace($$12, $$22, $$32, $$9, $$10, $$11, $$42);
        if (!$$13 || $$6.isEmpty() && $$7.isEmpty()) {
            return false;
        }
        if (!$$42.decorators.isEmpty()) {
            TreeDecorator.Context $$14 = new TreeDecorator.Context($$12, $$122, $$22, $$6, $$7, $$5);
            $$42.decorators.forEach($$1 -> $$1.place($$14));
        }
        return BoundingBox.encapsulatingPositions(Iterables.concat($$5, $$6, $$7, $$8)).map($$4 -> {
            DiscreteVoxelShape $$5 = TreeFeature.updateLeaves($$12, $$4, $$6, $$8, $$5);
            StructureTemplate.updateShapeAtEdge($$12, 3, $$5, $$4.minX(), $$4.minY(), $$4.minZ());
            return true;
        }).orElse(false);
    }

    /*
     * Unable to fully structure code
     */
    private static DiscreteVoxelShape updateLeaves(LevelAccessor $$0, BoundingBox $$1, Set<BlockPos> $$2, Set<BlockPos> $$3, Set<BlockPos> $$4) {
        $$5 = new BitSetDiscreteVoxelShape($$1.getXSpan(), $$1.getYSpan(), $$1.getZSpan());
        $$6 = 7;
        $$7 = Lists.newArrayList();
        for ($$8 = 0; $$8 < 7; ++$$8) {
            $$7.add(Sets.newHashSet());
        }
        for (BlockPos $$9 : Lists.newArrayList(Sets.union($$3, $$4))) {
            if (!$$1.isInside($$9)) continue;
            $$5.fill($$9.getX() - $$1.minX(), $$9.getY() - $$1.minY(), $$9.getZ() - $$1.minZ());
        }
        $$10 = new BlockPos.MutableBlockPos();
        $$11 = 0;
        ((Set)$$7.get(0)).addAll($$2);
        block2: while (true) {
            if ($$11 < 7 && ((Set)$$7.get($$11)).isEmpty()) {
                ++$$11;
                continue;
            }
            if ($$11 >= 7) break;
            $$12 = ((Set)$$7.get($$11)).iterator();
            $$13 = (BlockPos)$$12.next();
            $$12.remove();
            if (!$$1.isInside($$13)) continue;
            if ($$11 != 0) {
                $$14 = $$0.getBlockState($$13);
                TreeFeature.setBlockKnownShape($$0, $$13, (BlockState)$$14.setValue(BlockStateProperties.DISTANCE, $$11));
            }
            $$5.fill($$13.getX() - $$1.minX(), $$13.getY() - $$1.minY(), $$13.getZ() - $$1.minZ());
            var12_14 = Direction.values();
            var13_15 = var12_14.length;
            var14_16 = 0;
            while (true) {
                if (var14_16 < var13_15) ** break;
                continue block2;
                $$15 = var12_14[var14_16];
                $$10.setWithOffset((Vec3i)$$13, $$15);
                if ($$1.isInside($$10) && !$$5.isFull($$16 = $$10.getX() - $$1.minX(), $$17 = $$10.getY() - $$1.minY(), $$18 = $$10.getZ() - $$1.minZ()) && !($$20 = LeavesBlock.getOptionalDistanceAt($$19 = $$0.getBlockState($$10))).isEmpty() && ($$21 = Math.min($$20.getAsInt(), $$11 + 1)) < 7) {
                    ((Set)$$7.get($$21)).add($$10.immutable());
                    $$11 = Math.min($$11, $$21);
                }
                ++var14_16;
            }
            break;
        }
        return $$5;
    }

    public static List<BlockPos> getLowestTrunkOrRootOfTree(TreeDecorator.Context $$0) {
        ArrayList<BlockPos> $$1 = Lists.newArrayList();
        ObjectArrayList<BlockPos> $$2 = $$0.roots();
        ObjectArrayList<BlockPos> $$3 = $$0.logs();
        if ($$2.isEmpty()) {
            $$1.addAll((Collection<BlockPos>)$$3);
        } else if (!$$3.isEmpty() && ((BlockPos)$$2.get(0)).getY() == ((BlockPos)$$3.get(0)).getY()) {
            $$1.addAll((Collection<BlockPos>)$$3);
            $$1.addAll((Collection<BlockPos>)$$2);
        } else {
            $$1.addAll((Collection<BlockPos>)$$2);
        }
        return $$1;
    }
}

