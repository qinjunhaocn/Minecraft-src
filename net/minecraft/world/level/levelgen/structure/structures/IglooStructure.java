/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;

public class IglooStructure
extends Structure {
    public static final MapCodec<IglooStructure> CODEC = IglooStructure.simpleCodec(IglooStructure::new);

    public IglooStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        return IglooStructure.onTopOfChunkCenter($$0, Heightmap.Types.WORLD_SURFACE_WG, $$1 -> this.generatePieces((StructurePiecesBuilder)$$1, $$0));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        ChunkPos $$2 = $$1.chunkPos();
        WorldgenRandom $$3 = $$1.random();
        BlockPos $$4 = new BlockPos($$2.getMinBlockX(), 90, $$2.getMinBlockZ());
        Rotation $$5 = Rotation.getRandom($$3);
        IglooPieces.addPieces($$1.structureTemplateManager(), $$4, $$5, $$0, $$3);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.IGLOO;
    }
}

