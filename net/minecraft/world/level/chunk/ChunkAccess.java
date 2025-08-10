/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public abstract class ChunkAccess
implements BiomeManager.NoiseBiomeSource,
LightChunk,
StructureAccess {
    public static final int NO_FILLED_SECTION = -1;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LongSet EMPTY_REFERENCE_SET = new LongOpenHashSet();
    protected final ShortList[] postProcessing;
    private volatile boolean unsaved;
    private volatile boolean isLightCorrect;
    protected final ChunkPos chunkPos;
    private long inhabitedTime;
    @Nullable
    @Deprecated
    private BiomeGenerationSettings carverBiomeSettings;
    @Nullable
    protected NoiseChunk noiseChunk;
    protected final UpgradeData upgradeData;
    @Nullable
    protected BlendingData blendingData;
    protected final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
    protected ChunkSkyLightSources skyLightSources;
    private final Map<Structure, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<Structure, LongSet> structuresRefences = Maps.newHashMap();
    protected final Map<BlockPos, CompoundTag> pendingBlockEntities = Maps.newHashMap();
    protected final Map<BlockPos, BlockEntity> blockEntities = new Object2ObjectOpenHashMap();
    protected final LevelHeightAccessor levelHeightAccessor;
    protected final LevelChunkSection[] sections;

    public ChunkAccess(ChunkPos $$0, UpgradeData $$1, LevelHeightAccessor $$2, Registry<Biome> $$3, long $$4, @Nullable LevelChunkSection[] $$5, @Nullable BlendingData $$6) {
        this.chunkPos = $$0;
        this.upgradeData = $$1;
        this.levelHeightAccessor = $$2;
        this.sections = new LevelChunkSection[$$2.getSectionsCount()];
        this.inhabitedTime = $$4;
        this.postProcessing = new ShortList[$$2.getSectionsCount()];
        this.blendingData = $$6;
        this.skyLightSources = new ChunkSkyLightSources($$2);
        if ($$5 != null) {
            if (this.sections.length == $$5.length) {
                System.arraycopy($$5, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)$$5.length, (Object)this.sections.length);
            }
        }
        ChunkAccess.a($$3, this.sections);
    }

    private static void a(Registry<Biome> $$0, LevelChunkSection[] $$1) {
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$1[$$2] != null) continue;
            $$1[$$2] = new LevelChunkSection($$0);
        }
    }

    public GameEventListenerRegistry getListenerRegistry(int $$0) {
        return GameEventListenerRegistry.NOOP;
    }

    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1) {
        return this.setBlockState($$0, $$1, 3);
    }

    @Nullable
    public abstract BlockState setBlockState(BlockPos var1, BlockState var2, int var3);

    public abstract void setBlockEntity(BlockEntity var1);

    public abstract void addEntity(Entity var1);

    public int getHighestFilledSectionIndex() {
        LevelChunkSection[] $$0 = this.d();
        for (int $$1 = $$0.length - 1; $$1 >= 0; --$$1) {
            LevelChunkSection $$2 = $$0[$$1];
            if ($$2.hasOnlyAir()) continue;
            return $$1;
        }
        return -1;
    }

    @Deprecated(forRemoval=true)
    public int getHighestSectionPosition() {
        int $$0 = this.getHighestFilledSectionIndex();
        return $$0 == -1 ? this.getMinY() : SectionPos.sectionToBlockCoord(this.getSectionYFromSectionIndex($$0));
    }

    public Set<BlockPos> getBlockEntitiesPos() {
        HashSet<BlockPos> $$0 = Sets.newHashSet(this.pendingBlockEntities.keySet());
        $$0.addAll(this.blockEntities.keySet());
        return $$0;
    }

    public LevelChunkSection[] d() {
        return this.sections;
    }

    public LevelChunkSection getSection(int $$0) {
        return this.d()[$$0];
    }

    public Collection<Map.Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public void a(Heightmap.Types $$0, long[] $$1) {
        this.getOrCreateHeightmapUnprimed($$0).a(this, $$0, $$1);
    }

    public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types $$02) {
        return this.heightmaps.computeIfAbsent($$02, $$0 -> new Heightmap(this, (Heightmap.Types)$$0));
    }

    public boolean hasPrimedHeightmap(Heightmap.Types $$0) {
        return this.heightmaps.get($$0) != null;
    }

    public int getHeight(Heightmap.Types $$0, int $$1, int $$2) {
        Heightmap $$3 = this.heightmaps.get($$0);
        if ($$3 == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE && this instanceof LevelChunk) {
                LOGGER.error("Unprimed heightmap: " + String.valueOf($$0) + " " + $$1 + " " + $$2);
            }
            Heightmap.primeHeightmaps(this, EnumSet.of($$0));
            $$3 = this.heightmaps.get($$0);
        }
        return $$3.getFirstAvailable($$1 & 0xF, $$2 & 0xF) - 1;
    }

    public ChunkPos getPos() {
        return this.chunkPos;
    }

    @Override
    @Nullable
    public StructureStart getStartForStructure(Structure $$0) {
        return this.structureStarts.get($$0);
    }

    @Override
    public void setStartForStructure(Structure $$0, StructureStart $$1) {
        this.structureStarts.put($$0, $$1);
        this.markUnsaved();
    }

    public Map<Structure, StructureStart> getAllStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    public void setAllStarts(Map<Structure, StructureStart> $$0) {
        this.structureStarts.clear();
        this.structureStarts.putAll($$0);
        this.markUnsaved();
    }

    @Override
    public LongSet getReferencesForStructure(Structure $$0) {
        return this.structuresRefences.getOrDefault($$0, EMPTY_REFERENCE_SET);
    }

    @Override
    public void addReferenceForStructure(Structure $$02, long $$1) {
        this.structuresRefences.computeIfAbsent($$02, $$0 -> new LongOpenHashSet()).add($$1);
        this.markUnsaved();
    }

    @Override
    public Map<Structure, LongSet> getAllReferences() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Override
    public void setAllReferences(Map<Structure, LongSet> $$0) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll($$0);
        this.markUnsaved();
    }

    public boolean isYSpaceEmpty(int $$0, int $$1) {
        if ($$0 < this.getMinY()) {
            $$0 = this.getMinY();
        }
        if ($$1 > this.getMaxY()) {
            $$1 = this.getMaxY();
        }
        for (int $$2 = $$0; $$2 <= $$1; $$2 += 16) {
            if (this.getSection(this.getSectionIndex($$2)).hasOnlyAir()) continue;
            return false;
        }
        return true;
    }

    public void markUnsaved() {
        this.unsaved = true;
    }

    public boolean tryMarkSaved() {
        if (this.unsaved) {
            this.unsaved = false;
            return true;
        }
        return false;
    }

    public boolean isUnsaved() {
        return this.unsaved;
    }

    public abstract ChunkStatus getPersistedStatus();

    public ChunkStatus getHighestGeneratedStatus() {
        ChunkStatus $$0 = this.getPersistedStatus();
        BelowZeroRetrogen $$1 = this.getBelowZeroRetrogen();
        if ($$1 != null) {
            ChunkStatus $$2 = $$1.targetStatus();
            return ChunkStatus.max($$2, $$0);
        }
        return $$0;
    }

    public abstract void removeBlockEntity(BlockPos var1);

    public void markPosForPostprocessing(BlockPos $$0) {
        LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)$$0);
    }

    public ShortList[] p() {
        return this.postProcessing;
    }

    public void addPackedPostProcess(ShortList $$0, int $$1) {
        ChunkAccess.a(this.p(), $$1).addAll($$0);
    }

    public void setBlockEntityNbt(CompoundTag $$0) {
        BlockPos $$1 = BlockEntity.getPosFromTag(this.chunkPos, $$0);
        if (!this.blockEntities.containsKey($$1)) {
            this.pendingBlockEntities.put($$1, $$0);
        }
    }

    @Nullable
    public CompoundTag getBlockEntityNbt(BlockPos $$0) {
        return this.pendingBlockEntities.get($$0);
    }

    @Nullable
    public abstract CompoundTag getBlockEntityNbtForSaving(BlockPos var1, HolderLookup.Provider var2);

    @Override
    public final void findBlockLightSources(BiConsumer<BlockPos, BlockState> $$02) {
        this.findBlocks($$0 -> $$0.getLightEmission() != 0, $$02);
    }

    public void findBlocks(Predicate<BlockState> $$0, BiConsumer<BlockPos, BlockState> $$1) {
        BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
        for (int $$3 = this.getMinSectionY(); $$3 <= this.getMaxSectionY(); ++$$3) {
            LevelChunkSection $$4 = this.getSection(this.getSectionIndexFromSectionY($$3));
            if (!$$4.maybeHas($$0)) continue;
            BlockPos $$5 = SectionPos.of(this.chunkPos, $$3).origin();
            for (int $$6 = 0; $$6 < 16; ++$$6) {
                for (int $$7 = 0; $$7 < 16; ++$$7) {
                    for (int $$8 = 0; $$8 < 16; ++$$8) {
                        BlockState $$9 = $$4.getBlockState($$8, $$6, $$7);
                        if (!$$0.test($$9)) continue;
                        $$1.accept($$2.setWithOffset($$5, $$8, $$6, $$7), $$9);
                    }
                }
            }
        }
    }

    public abstract TickContainerAccess<Block> getBlockTicks();

    public abstract TickContainerAccess<Fluid> getFluidTicks();

    public boolean canBeSerialized() {
        return true;
    }

    public abstract PackedTicks getTicksForSerialization(long var1);

    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    public boolean isOldNoiseGeneration() {
        return this.blendingData != null;
    }

    @Nullable
    public BlendingData getBlendingData() {
        return this.blendingData;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public void incrementInhabitedTime(long $$0) {
        this.inhabitedTime += $$0;
    }

    public void setInhabitedTime(long $$0) {
        this.inhabitedTime = $$0;
    }

    public static ShortList a(ShortList[] $$0, int $$1) {
        if ($$0[$$1] == null) {
            $$0[$$1] = new ShortArrayList();
        }
        return $$0[$$1];
    }

    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }

    public void setLightCorrect(boolean $$0) {
        this.isLightCorrect = $$0;
        this.markUnsaved();
    }

    @Override
    public int getMinY() {
        return this.levelHeightAccessor.getMinY();
    }

    @Override
    public int getHeight() {
        return this.levelHeightAccessor.getHeight();
    }

    public NoiseChunk getOrCreateNoiseChunk(Function<ChunkAccess, NoiseChunk> $$0) {
        if (this.noiseChunk == null) {
            this.noiseChunk = $$0.apply(this);
        }
        return this.noiseChunk;
    }

    @Deprecated
    public BiomeGenerationSettings carverBiome(Supplier<BiomeGenerationSettings> $$0) {
        if (this.carverBiomeSettings == null) {
            this.carverBiomeSettings = $$0.get();
        }
        return this.carverBiomeSettings;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        try {
            int $$3 = QuartPos.fromBlock(this.getMinY());
            int $$4 = $$3 + QuartPos.fromBlock(this.getHeight()) - 1;
            int $$5 = Mth.clamp($$1, $$3, $$4);
            int $$6 = this.getSectionIndex(QuartPos.toBlock($$5));
            return this.sections[$$6].getNoiseBiome($$0 & 3, $$5 & 3, $$2 & 3);
        } catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Getting biome");
            CrashReportCategory $$9 = $$8.addCategory("Biome being got");
            $$9.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$0, $$1, $$2));
            throw new ReportedException($$8);
        }
    }

    public void fillBiomesFromNoise(BiomeResolver $$0, Climate.Sampler $$1) {
        ChunkPos $$2 = this.getPos();
        int $$3 = QuartPos.fromBlock($$2.getMinBlockX());
        int $$4 = QuartPos.fromBlock($$2.getMinBlockZ());
        LevelHeightAccessor $$5 = this.getHeightAccessorForGeneration();
        for (int $$6 = $$5.getMinSectionY(); $$6 <= $$5.getMaxSectionY(); ++$$6) {
            LevelChunkSection $$7 = this.getSection(this.getSectionIndexFromSectionY($$6));
            int $$8 = QuartPos.fromSection($$6);
            $$7.fillBiomesFromNoise($$0, $$1, $$3, $$8, $$4);
        }
    }

    public boolean hasAnyStructureReferences() {
        return !this.getAllReferences().isEmpty();
    }

    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return null;
    }

    public boolean isUpgrading() {
        return this.getBelowZeroRetrogen() != null;
    }

    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return this;
    }

    public void initializeLightSources() {
        this.skyLightSources.fillFrom(this);
    }

    @Override
    public ChunkSkyLightSources getSkyLightSources() {
        return this.skyLightSources;
    }

    public static ProblemReporter.PathElement problemPath(ChunkPos $$0) {
        return new ChunkPathElement($$0);
    }

    public ProblemReporter.PathElement problemPath() {
        return ChunkAccess.problemPath(this.getPos());
    }

    record ChunkPathElement(ChunkPos pos) implements ProblemReporter.PathElement
    {
        @Override
        public String get() {
            return "chunk@" + String.valueOf(this.pos);
        }
    }

    public record PackedTicks(List<SavedTick<Block>> blocks, List<SavedTick<Fluid>> fluids) {
    }
}

