/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.TickContainerAccess;

public class ImposterProtoChunk
extends ProtoChunk {
    private final LevelChunk wrapped;
    private final boolean allowWrites;

    public ImposterProtoChunk(LevelChunk $$0, boolean $$1) {
        super($$0.getPos(), UpgradeData.EMPTY, $$0.levelHeightAccessor, (Registry<Biome>)$$0.getLevel().registryAccess().lookupOrThrow(Registries.BIOME), $$0.getBlendingData());
        this.wrapped = $$0;
        this.allowWrites = $$1;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.wrapped.getBlockEntity($$0);
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        return this.wrapped.getBlockState($$0);
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return this.wrapped.getFluidState($$0);
    }

    @Override
    public LevelChunkSection getSection(int $$0) {
        if (this.allowWrites) {
            return this.wrapped.getSection($$0);
        }
        return super.getSection($$0);
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1, int $$2) {
        if (this.allowWrites) {
            return this.wrapped.setBlockState($$0, $$1, $$2);
        }
        return null;
    }

    @Override
    public void setBlockEntity(BlockEntity $$0) {
        if (this.allowWrites) {
            this.wrapped.setBlockEntity($$0);
        }
    }

    @Override
    public void addEntity(Entity $$0) {
        if (this.allowWrites) {
            this.wrapped.addEntity($$0);
        }
    }

    @Override
    public void setPersistedStatus(ChunkStatus $$0) {
        if (this.allowWrites) {
            super.setPersistedStatus($$0);
        }
    }

    @Override
    public LevelChunkSection[] d() {
        return this.wrapped.d();
    }

    @Override
    public void a(Heightmap.Types $$0, long[] $$1) {
    }

    private Heightmap.Types fixType(Heightmap.Types $$0) {
        if ($$0 == Heightmap.Types.WORLD_SURFACE_WG) {
            return Heightmap.Types.WORLD_SURFACE;
        }
        if ($$0 == Heightmap.Types.OCEAN_FLOOR_WG) {
            return Heightmap.Types.OCEAN_FLOOR;
        }
        return $$0;
    }

    @Override
    public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types $$0) {
        return this.wrapped.getOrCreateHeightmapUnprimed($$0);
    }

    @Override
    public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
        return this.wrapped.getHeight(this.fixType($$0), $$1, $$2);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        return this.wrapped.getNoiseBiome($$0, $$1, $$2);
    }

    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }

    @Override
    @Nullable
    public StructureStart getStartForStructure(Structure $$0) {
        return this.wrapped.getStartForStructure($$0);
    }

    @Override
    public void setStartForStructure(Structure $$0, StructureStart $$1) {
    }

    @Override
    public Map<Structure, StructureStart> getAllStarts() {
        return this.wrapped.getAllStarts();
    }

    @Override
    public void setAllStarts(Map<Structure, StructureStart> $$0) {
    }

    @Override
    public LongSet getReferencesForStructure(Structure $$0) {
        return this.wrapped.getReferencesForStructure($$0);
    }

    @Override
    public void addReferenceForStructure(Structure $$0, long $$1) {
    }

    @Override
    public Map<Structure, LongSet> getAllReferences() {
        return this.wrapped.getAllReferences();
    }

    @Override
    public void setAllReferences(Map<Structure, LongSet> $$0) {
    }

    @Override
    public void markUnsaved() {
        this.wrapped.markUnsaved();
    }

    @Override
    public boolean canBeSerialized() {
        return false;
    }

    @Override
    public boolean tryMarkSaved() {
        return false;
    }

    @Override
    public boolean isUnsaved() {
        return false;
    }

    @Override
    public ChunkStatus getPersistedStatus() {
        return this.wrapped.getPersistedStatus();
    }

    @Override
    public void removeBlockEntity(BlockPos $$0) {
    }

    @Override
    public void markPosForPostprocessing(BlockPos $$0) {
    }

    @Override
    public void setBlockEntityNbt(CompoundTag $$0) {
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityNbt(BlockPos $$0) {
        return this.wrapped.getBlockEntityNbt($$0);
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos $$0, HolderLookup.Provider $$1) {
        return this.wrapped.getBlockEntityNbtForSaving($$0, $$1);
    }

    @Override
    public void findBlocks(Predicate<BlockState> $$0, BiConsumer<BlockPos, BlockState> $$1) {
        this.wrapped.findBlocks($$0, $$1);
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        if (this.allowWrites) {
            return this.wrapped.getBlockTicks();
        }
        return BlackholeTickAccess.emptyContainer();
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        if (this.allowWrites) {
            return this.wrapped.getFluidTicks();
        }
        return BlackholeTickAccess.emptyContainer();
    }

    @Override
    public ChunkAccess.PackedTicks getTicksForSerialization(long $$0) {
        return this.wrapped.getTicksForSerialization($$0);
    }

    @Override
    @Nullable
    public BlendingData getBlendingData() {
        return this.wrapped.getBlendingData();
    }

    @Override
    public CarvingMask getCarvingMask() {
        if (this.allowWrites) {
            return super.getCarvingMask();
        }
        throw Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
    }

    @Override
    public CarvingMask getOrCreateCarvingMask() {
        if (this.allowWrites) {
            return super.getOrCreateCarvingMask();
        }
        throw Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
    }

    public LevelChunk getWrapped() {
        return this.wrapped;
    }

    @Override
    public boolean isLightCorrect() {
        return this.wrapped.isLightCorrect();
    }

    @Override
    public void setLightCorrect(boolean $$0) {
        this.wrapped.setLightCorrect($$0);
    }

    @Override
    public void fillBiomesFromNoise(BiomeResolver $$0, Climate.Sampler $$1) {
        if (this.allowWrites) {
            this.wrapped.fillBiomesFromNoise($$0, $$1);
        }
    }

    @Override
    public void initializeLightSources() {
        this.wrapped.initializeLightSources();
    }

    @Override
    public ChunkSkyLightSources getSkyLightSources() {
        return this.wrapped.getSkyLightSources();
    }
}

