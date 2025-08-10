/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.LinkedList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;

public class WoodlandMansionStructure
extends Structure {
    public static final MapCodec<WoodlandMansionStructure> CODEC = WoodlandMansionStructure.simpleCodec(WoodlandMansionStructure::new);

    public WoodlandMansionStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        Rotation $$1 = Rotation.getRandom($$0.random());
        BlockPos $$2 = this.getLowestYIn5by5BoxOffset7Blocks($$0, $$1);
        if ($$2.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new Structure.GenerationStub($$2, $$3 -> this.generatePieces((StructurePiecesBuilder)$$3, $$0, $$2, $$1)));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1, BlockPos $$2, Rotation $$3) {
        LinkedList<WoodlandMansionPieces.WoodlandMansionPiece> $$4 = Lists.newLinkedList();
        WoodlandMansionPieces.generateMansion($$1.structureTemplateManager(), $$2, $$3, $$4, $$1.random());
        $$4.forEach($$0::addPiece);
    }

    @Override
    public void afterPlace(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, PiecesContainer $$6) {
        BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos();
        int $$8 = $$0.getMinY();
        BoundingBox $$9 = $$6.calculateBoundingBox();
        int $$10 = $$9.minY();
        for (int $$11 = $$4.minX(); $$11 <= $$4.maxX(); ++$$11) {
            block1: for (int $$12 = $$4.minZ(); $$12 <= $$4.maxZ(); ++$$12) {
                $$7.set($$11, $$10, $$12);
                if ($$0.isEmptyBlock($$7) || !$$9.isInside($$7) || !$$6.isInsidePiece($$7)) continue;
                for (int $$13 = $$10 - 1; $$13 > $$8; --$$13) {
                    $$7.setY($$13);
                    if (!$$0.isEmptyBlock($$7) && !$$0.getBlockState($$7).liquid()) continue block1;
                    $$0.setBlock($$7, Blocks.COBBLESTONE.defaultBlockState(), 2);
                }
            }
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.WOODLAND_MANSION;
    }
}

