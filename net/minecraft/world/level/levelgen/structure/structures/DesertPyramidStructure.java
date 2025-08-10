/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidStructure
extends SinglePieceStructure {
    public static final MapCodec<DesertPyramidStructure> CODEC = DesertPyramidStructure.simpleCodec(DesertPyramidStructure::new);

    public DesertPyramidStructure(Structure.StructureSettings $$0) {
        super(DesertPyramidPiece::new, 21, 21, $$0);
    }

    @Override
    public void afterPlace(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, PiecesContainer $$6) {
        SortedArraySet $$7 = SortedArraySet.create(Vec3i::compareTo);
        for (StructurePiece $$8 : $$6.pieces()) {
            if (!($$8 instanceof DesertPyramidPiece)) continue;
            DesertPyramidPiece $$9 = (DesertPyramidPiece)$$8;
            $$7.addAll($$9.getPotentialSuspiciousSandWorldPositions());
            DesertPyramidStructure.placeSuspiciousSand($$4, $$0, $$9.getRandomCollapsedRoofPos());
        }
        ObjectArrayList $$10 = new ObjectArrayList((Collection)$$7.stream().toList());
        RandomSource $$11 = RandomSource.create($$0.getSeed()).forkPositional().at($$6.calculateBoundingBox().getCenter());
        Util.shuffle($$10, $$11);
        int $$12 = Math.min($$7.size(), $$11.nextInt(5, 8));
        for (BlockPos $$13 : $$10) {
            if ($$12 > 0) {
                --$$12;
                DesertPyramidStructure.placeSuspiciousSand($$4, $$0, $$13);
                continue;
            }
            if (!$$4.isInside($$13)) continue;
            $$0.setBlock($$13, Blocks.SAND.defaultBlockState(), 2);
        }
    }

    private static void placeSuspiciousSand(BoundingBox $$0, WorldGenLevel $$12, BlockPos $$2) {
        if ($$0.isInside($$2)) {
            $$12.setBlock($$2, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);
            $$12.getBlockEntity($$2, BlockEntityType.BRUSHABLE_BLOCK).ifPresent($$1 -> $$1.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, $$2.asLong()));
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.DESERT_PYRAMID;
    }
}

