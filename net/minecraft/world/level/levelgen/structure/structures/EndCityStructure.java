/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;

public class EndCityStructure
extends Structure {
    public static final MapCodec<EndCityStructure> CODEC = EndCityStructure.simpleCodec(EndCityStructure::new);

    public EndCityStructure(Structure.StructureSettings $$0) {
        super($$0);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        Rotation $$1 = Rotation.getRandom($$0.random());
        BlockPos $$2 = this.getLowestYIn5by5BoxOffset7Blocks($$0, $$1);
        if ($$2.getY() < 60) {
            return Optional.empty();
        }
        return Optional.of(new Structure.GenerationStub($$2, $$3 -> this.generatePieces((StructurePiecesBuilder)$$3, $$2, $$1, $$0)));
    }

    private void generatePieces(StructurePiecesBuilder $$0, BlockPos $$1, Rotation $$2, Structure.GenerationContext $$3) {
        ArrayList<StructurePiece> $$4 = Lists.newArrayList();
        EndCityPieces.startHouseTower($$3.structureTemplateManager(), $$1, $$2, $$4, $$3.random());
        $$4.forEach($$0::addPiece);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.END_CITY;
    }
}

