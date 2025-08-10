/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;

public class StrongholdStructure
extends Structure {
    public static final MapCodec<StrongholdStructure> CODEC = StrongholdStructure.simpleCodec(StrongholdStructure::new);

    public StrongholdStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        return Optional.of(new Structure.GenerationStub($$0.chunkPos().getWorldPosition(), $$1 -> StrongholdStructure.generatePieces($$1, $$0)));
    }

    private static void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        StrongholdPieces.StartPiece $$3;
        int $$2 = 0;
        do {
            $$0.clear();
            $$1.random().setLargeFeatureSeed($$1.seed() + (long)$$2++, $$1.chunkPos().x, $$1.chunkPos().z);
            StrongholdPieces.resetPieces();
            $$3 = new StrongholdPieces.StartPiece($$1.random(), $$1.chunkPos().getBlockX(2), $$1.chunkPos().getBlockZ(2));
            $$0.addPiece($$3);
            $$3.addChildren($$3, $$0, $$1.random());
            List<StructurePiece> $$4 = $$3.pendingChildren;
            while (!$$4.isEmpty()) {
                int $$5 = $$1.random().nextInt($$4.size());
                StructurePiece $$6 = $$4.remove($$5);
                $$6.addChildren($$3, $$0, $$1.random());
            }
            $$0.moveBelowSeaLevel($$1.chunkGenerator().getSeaLevel(), $$1.chunkGenerator().getMinY(), $$1.random(), 10);
        } while ($$0.isEmpty() || $$3.portalRoomPiece == null);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.STRONGHOLD;
    }
}

