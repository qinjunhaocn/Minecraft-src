/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public class StructureManager {
    private final LevelAccessor level;
    private final WorldOptions worldOptions;
    private final StructureCheck structureCheck;

    public StructureManager(LevelAccessor $$0, WorldOptions $$1, StructureCheck $$2) {
        this.level = $$0;
        this.worldOptions = $$1;
        this.structureCheck = $$2;
    }

    public StructureManager forWorldGenRegion(WorldGenRegion $$0) {
        if ($$0.getLevel() != this.level) {
            throw new IllegalStateException("Using invalid structure manager (source level: " + String.valueOf($$0.getLevel()) + ", region: " + String.valueOf($$0));
        }
        return new StructureManager($$0, this.worldOptions, this.structureCheck);
    }

    public List<StructureStart> startsForStructure(ChunkPos $$0, Predicate<Structure> $$1) {
        Map<Structure, LongSet> $$2 = this.level.getChunk($$0.x, $$0.z, ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
        ImmutableList.Builder $$3 = ImmutableList.builder();
        for (Map.Entry<Structure, LongSet> $$4 : $$2.entrySet()) {
            Structure $$5 = $$4.getKey();
            if (!$$1.test($$5)) continue;
            this.fillStartsForStructure($$5, $$4.getValue(), $$3::add);
        }
        return $$3.build();
    }

    public List<StructureStart> startsForStructure(SectionPos $$0, Structure $$1) {
        LongSet $$2 = this.level.getChunk($$0.x(), $$0.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForStructure($$1);
        ImmutableList.Builder $$3 = ImmutableList.builder();
        this.fillStartsForStructure($$1, $$2, $$3::add);
        return $$3.build();
    }

    public void fillStartsForStructure(Structure $$0, LongSet $$1, Consumer<StructureStart> $$2) {
        LongIterator longIterator = $$1.iterator();
        while (longIterator.hasNext()) {
            long $$3 = (Long)longIterator.next();
            SectionPos $$4 = SectionPos.of(new ChunkPos($$3), this.level.getMinSectionY());
            StructureStart $$5 = this.getStartForStructure($$4, $$0, this.level.getChunk($$4.x(), $$4.z(), ChunkStatus.STRUCTURE_STARTS));
            if ($$5 == null || !$$5.isValid()) continue;
            $$2.accept($$5);
        }
    }

    @Nullable
    public StructureStart getStartForStructure(SectionPos $$0, Structure $$1, StructureAccess $$2) {
        return $$2.getStartForStructure($$1);
    }

    public void setStartForStructure(SectionPos $$0, Structure $$1, StructureStart $$2, StructureAccess $$3) {
        $$3.setStartForStructure($$1, $$2);
    }

    public void addReferenceForStructure(SectionPos $$0, Structure $$1, long $$2, StructureAccess $$3) {
        $$3.addReferenceForStructure($$1, $$2);
    }

    public boolean shouldGenerateStructures() {
        return this.worldOptions.generateStructures();
    }

    public StructureStart getStructureAt(BlockPos $$0, Structure $$1) {
        for (StructureStart $$2 : this.startsForStructure(SectionPos.of($$0), $$1)) {
            if (!$$2.getBoundingBox().isInside($$0)) continue;
            return $$2;
        }
        return StructureStart.INVALID_START;
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, TagKey<Structure> $$12) {
        return this.getStructureWithPieceAt($$0, (Holder<Structure> $$1) -> $$1.is($$12));
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, HolderSet<Structure> $$1) {
        return this.getStructureWithPieceAt($$0, $$1::contains);
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, Predicate<Holder<Structure>> $$1) {
        HolderLookup.RegistryLookup $$2 = this.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        for (StructureStart $$3 : this.startsForStructure(new ChunkPos($$0), arg_0 -> StructureManager.lambda$getStructureWithPieceAt$1((Registry)$$2, $$1, arg_0))) {
            if (!this.structureHasPieceAt($$0, $$3)) continue;
            return $$3;
        }
        return StructureStart.INVALID_START;
    }

    public StructureStart getStructureWithPieceAt(BlockPos $$0, Structure $$1) {
        for (StructureStart $$2 : this.startsForStructure(SectionPos.of($$0), $$1)) {
            if (!this.structureHasPieceAt($$0, $$2)) continue;
            return $$2;
        }
        return StructureStart.INVALID_START;
    }

    public boolean structureHasPieceAt(BlockPos $$0, StructureStart $$1) {
        for (StructurePiece $$2 : $$1.getPieces()) {
            if (!$$2.getBoundingBox().isInside($$0)) continue;
            return true;
        }
        return false;
    }

    public boolean hasAnyStructureAt(BlockPos $$0) {
        SectionPos $$1 = SectionPos.of($$0);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
    }

    public Map<Structure, LongSet> getAllStructuresAt(BlockPos $$0) {
        SectionPos $$1 = SectionPos.of($$0);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
    }

    public StructureCheckResult checkStructurePresence(ChunkPos $$0, Structure $$1, StructurePlacement $$2, boolean $$3) {
        return this.structureCheck.checkStart($$0, $$1, $$2, $$3);
    }

    public void addReference(StructureStart $$0) {
        $$0.addReference();
        this.structureCheck.incrementReference($$0.getChunkPos(), $$0.getStructure());
    }

    public RegistryAccess registryAccess() {
        return this.level.registryAccess();
    }

    private static /* synthetic */ boolean lambda$getStructureWithPieceAt$1(Registry $$0, Predicate $$1, Structure $$2) {
        return $$0.get($$0.getId($$2)).map($$1::test).orElse(false);
    }
}

