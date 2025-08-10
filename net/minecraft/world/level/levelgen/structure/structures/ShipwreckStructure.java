/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces;

public class ShipwreckStructure
extends Structure {
    public static final MapCodec<ShipwreckStructure> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group(ShipwreckStructure.settingsCodec($$02), (App)Codec.BOOL.fieldOf("is_beached").forGetter($$0 -> $$0.isBeached)).apply((Applicative)$$02, ShipwreckStructure::new));
    public final boolean isBeached;

    public ShipwreckStructure(Structure.StructureSettings $$0, boolean $$1) {
        super($$0);
        this.isBeached = $$1;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        Heightmap.Types $$12 = this.isBeached ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.OCEAN_FLOOR_WG;
        return ShipwreckStructure.onTopOfChunkCenter($$0, $$12, $$1 -> this.generatePieces((StructurePiecesBuilder)$$1, $$0));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        Rotation $$2 = Rotation.getRandom($$1.random());
        BlockPos $$3 = new BlockPos($$1.chunkPos().getMinBlockX(), 90, $$1.chunkPos().getMinBlockZ());
        ShipwreckPieces.ShipwreckPiece $$4 = ShipwreckPieces.addRandomPiece($$1.structureTemplateManager(), $$3, $$2, $$0, $$1.random(), this.isBeached);
        if ($$4.isTooBigToFitInWorldGenRegion()) {
            int $$8;
            BoundingBox $$5 = $$4.getBoundingBox();
            if (this.isBeached) {
                int $$6 = Structure.getLowestY($$1, $$5.minX(), $$5.getXSpan(), $$5.minZ(), $$5.getZSpan());
                int $$7 = $$4.calculateBeachedPosition($$6, $$1.random());
            } else {
                $$8 = Structure.getMeanFirstOccupiedHeight($$1, $$5.minX(), $$5.getXSpan(), $$5.minZ(), $$5.getZSpan());
            }
            $$4.adjustPositionHeight($$8);
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.SHIPWRECK;
    }
}

