/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public class LevelChunk
extends ChunkAccess {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity(){

        @Override
        public void tick() {
        }

        @Override
        public boolean isRemoved() {
            return true;
        }

        @Override
        public BlockPos getPos() {
            return BlockPos.ZERO;
        }

        @Override
        public String getType() {
            return "<null>";
        }
    };
    private final Map<BlockPos, RebindableTickingBlockEntityWrapper> tickersInLevel = Maps.newHashMap();
    private boolean loaded;
    final Level level;
    @Nullable
    private Supplier<FullChunkStatus> fullStatus;
    @Nullable
    private PostLoadProcessor postLoad;
    private final Int2ObjectMap<GameEventListenerRegistry> gameEventListenerRegistrySections;
    private final LevelChunkTicks<Block> blockTicks;
    private final LevelChunkTicks<Fluid> fluidTicks;
    private UnsavedListener unsavedListener = $$0 -> {};

    public LevelChunk(Level $$0, ChunkPos $$1) {
        this($$0, $$1, UpgradeData.EMPTY, new LevelChunkTicks<Block>(), new LevelChunkTicks<Fluid>(), 0L, null, null, null);
    }

    public LevelChunk(Level $$02, ChunkPos $$1, UpgradeData $$2, LevelChunkTicks<Block> $$3, LevelChunkTicks<Fluid> $$4, long $$5, @Nullable LevelChunkSection[] $$6, @Nullable PostLoadProcessor $$7, @Nullable BlendingData $$8) {
        super($$1, $$2, $$02, (Registry<Biome>)$$02.registryAccess().lookupOrThrow(Registries.BIOME), $$5, $$6, $$8);
        this.level = $$02;
        this.gameEventListenerRegistrySections = new Int2ObjectOpenHashMap();
        for (Heightmap.Types $$9 : Heightmap.Types.values()) {
            if (!ChunkStatus.FULL.heightmapsAfter().contains($$9)) continue;
            this.heightmaps.put($$9, new Heightmap(this, $$9));
        }
        this.postLoad = $$7;
        this.blockTicks = $$3;
        this.fluidTicks = $$4;
    }

    public LevelChunk(ServerLevel $$0, ProtoChunk $$1, @Nullable PostLoadProcessor $$2) {
        this($$0, $$1.getPos(), $$1.getUpgradeData(), $$1.unpackBlockTicks(), $$1.unpackFluidTicks(), $$1.getInhabitedTime(), $$1.d(), $$2, $$1.getBlendingData());
        if (!Collections.disjoint($$1.pendingBlockEntities.keySet(), $$1.blockEntities.keySet())) {
            LOGGER.error("Chunk at {} contains duplicated block entities", (Object)$$1.getPos());
        }
        for (BlockEntity $$3 : $$1.getBlockEntities().values()) {
            this.setBlockEntity($$3);
        }
        this.pendingBlockEntities.putAll($$1.getBlockEntityNbts());
        for (int $$4 = 0; $$4 < $$1.p().length; ++$$4) {
            this.postProcessing[$$4] = $$1.p()[$$4];
        }
        this.setAllStarts($$1.getAllStarts());
        this.setAllReferences($$1.getAllReferences());
        for (Map.Entry<Heightmap.Types, Heightmap> $$5 : $$1.getHeightmaps()) {
            if (!ChunkStatus.FULL.heightmapsAfter().contains($$5.getKey())) continue;
            this.a($$5.getKey(), $$5.getValue().a());
        }
        this.skyLightSources = $$1.skyLightSources;
        this.setLightCorrect($$1.isLightCorrect());
        this.markUnsaved();
    }

    public void setUnsavedListener(UnsavedListener $$0) {
        this.unsavedListener = $$0;
        if (this.isUnsaved()) {
            $$0.setUnsaved(this.chunkPos);
        }
    }

    @Override
    public void markUnsaved() {
        boolean $$0 = this.isUnsaved();
        super.markUnsaved();
        if (!$$0) {
            this.unsavedListener.setUnsaved(this.chunkPos);
        }
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    public ChunkAccess.PackedTicks getTicksForSerialization(long $$0) {
        return new ChunkAccess.PackedTicks(this.blockTicks.pack($$0), this.fluidTicks.pack($$0));
    }

    @Override
    public GameEventListenerRegistry getListenerRegistry(int $$0) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            return (GameEventListenerRegistry)this.gameEventListenerRegistrySections.computeIfAbsent($$0, $$2 -> new EuclideanGameEventListenerRegistry($$1, $$0, this::removeGameEventListenerRegistry));
        }
        return super.getListenerRegistry($$0);
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        if (this.level.isDebug()) {
            BlockState $$4 = null;
            if ($$2 == 60) {
                $$4 = Blocks.BARRIER.defaultBlockState();
            }
            if ($$2 == 70) {
                $$4 = DebugLevelSource.getBlockStateFor($$1, $$3);
            }
            return $$4 == null ? Blocks.AIR.defaultBlockState() : $$4;
        }
        try {
            LevelChunkSection $$6;
            int $$5 = this.getSectionIndex($$2);
            if ($$5 >= 0 && $$5 < this.sections.length && !($$6 = this.sections[$$5]).hasOnlyAir()) {
                return $$6.getBlockState($$1 & 0xF, $$2 & 0xF, $$3 & 0xF);
            }
            return Blocks.AIR.defaultBlockState();
        } catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Getting block state");
            CrashReportCategory $$9 = $$8.addCategory("Block being got");
            $$9.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$1, $$2, $$3));
            throw new ReportedException($$8);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return this.getFluidState($$0.getX(), $$0.getY(), $$0.getZ());
    }

    public FluidState getFluidState(int $$0, int $$1, int $$2) {
        try {
            LevelChunkSection $$4;
            int $$3 = this.getSectionIndex($$1);
            if ($$3 >= 0 && $$3 < this.sections.length && !($$4 = this.sections[$$3]).hasOnlyAir()) {
                return $$4.getFluidState($$0 & 0xF, $$1 & 0xF, $$2 & 0xF);
            }
            return Fluids.EMPTY.defaultFluidState();
        } catch (Throwable $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Getting fluid state");
            CrashReportCategory $$7 = $$6.addCategory("Block being got");
            $$7.setDetail("Location", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this, $$0, $$1, $$2));
            throw new ReportedException($$6);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos $$0, BlockState $$1, int $$2) {
        Level level;
        boolean $$15;
        int $$8;
        int $$7;
        int $$3 = $$0.getY();
        LevelChunkSection $$4 = this.getSection(this.getSectionIndex($$3));
        boolean $$5 = $$4.hasOnlyAir();
        if ($$5 && $$1.isAir()) {
            return null;
        }
        int $$6 = $$0.getX() & 0xF;
        BlockState $$9 = $$4.setBlockState($$6, $$7 = $$3 & 0xF, $$8 = $$0.getZ() & 0xF, $$1);
        if ($$9 == $$1) {
            return null;
        }
        Block $$10 = $$1.getBlock();
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update($$6, $$3, $$8, $$1);
        ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update($$6, $$3, $$8, $$1);
        boolean $$11 = $$4.hasOnlyAir();
        if ($$5 != $$11) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus($$0, $$11);
            this.level.getChunkSource().onSectionEmptinessChanged(this.chunkPos.x, SectionPos.blockToSectionCoord($$3), this.chunkPos.z, $$11);
        }
        if (LightEngine.hasDifferentLightProperties($$9, $$1)) {
            ProfilerFiller $$12 = Profiler.get();
            $$12.push("updateSkyLightSources");
            this.skyLightSources.update(this, $$6, $$3, $$8);
            $$12.popPush("queueCheckLight");
            this.level.getChunkSource().getLightEngine().checkBlock($$0);
            $$12.pop();
        }
        boolean $$13 = !$$9.is($$10);
        boolean $$14 = ($$2 & 0x40) != 0;
        boolean bl = $$15 = ($$2 & 0x100) == 0;
        if ($$13 && $$9.hasBlockEntity()) {
            BlockEntity $$16;
            if (!this.level.isClientSide && $$15 && ($$16 = this.level.getBlockEntity($$0)) != null) {
                $$16.preRemoveSideEffects($$0, $$9);
            }
            this.removeBlockEntity($$0);
        }
        if (($$13 || $$10 instanceof BaseRailBlock) && (level = this.level) instanceof ServerLevel) {
            ServerLevel $$17 = (ServerLevel)level;
            if (($$2 & 1) != 0 || $$14) {
                $$9.affectNeighborsAfterRemoval($$17, $$0, $$14);
            }
        }
        if (!$$4.getBlockState($$6, $$7, $$8).is($$10)) {
            return null;
        }
        if (!this.level.isClientSide && ($$2 & 0x200) == 0) {
            $$1.onPlace(this.level, $$0, $$9, $$14);
        }
        if ($$1.hasBlockEntity()) {
            BlockEntity $$18 = this.getBlockEntity($$0, EntityCreationType.CHECK);
            if ($$18 != null && !$$18.isValidBlockState($$1)) {
                LOGGER.warn("Found mismatched block entity @ {}: type = {}, state = {}", $$0, $$18.getType().builtInRegistryHolder().key().location(), $$1);
                this.removeBlockEntity($$0);
                $$18 = null;
            }
            if ($$18 == null) {
                $$18 = ((EntityBlock)((Object)$$10)).newBlockEntity($$0, $$1);
                if ($$18 != null) {
                    this.addAndRegisterBlockEntity($$18);
                }
            } else {
                $$18.setBlockState($$1);
                this.updateBlockEntityTicker($$18);
            }
        }
        this.markUnsaved();
        return $$9;
    }

    @Override
    @Deprecated
    public void addEntity(Entity $$0) {
    }

    @Nullable
    private BlockEntity createBlockEntity(BlockPos $$0) {
        BlockState $$1 = this.getBlockState($$0);
        if (!$$1.hasBlockEntity()) {
            return null;
        }
        return ((EntityBlock)((Object)$$1.getBlock())).newBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.getBlockEntity($$0, EntityCreationType.CHECK);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0, EntityCreationType $$1) {
        BlockEntity $$4;
        CompoundTag $$3;
        BlockEntity $$2 = (BlockEntity)this.blockEntities.get($$0);
        if ($$2 == null && ($$3 = (CompoundTag)this.pendingBlockEntities.remove($$0)) != null && ($$4 = this.promotePendingBlockEntity($$0, $$3)) != null) {
            return $$4;
        }
        if ($$2 == null) {
            if ($$1 == EntityCreationType.IMMEDIATE && ($$2 = this.createBlockEntity($$0)) != null) {
                this.addAndRegisterBlockEntity($$2);
            }
        } else if ($$2.isRemoved()) {
            this.blockEntities.remove($$0);
            return null;
        }
        return $$2;
    }

    public void addAndRegisterBlockEntity(BlockEntity $$0) {
        this.setBlockEntity($$0);
        if (this.isInLevel()) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel $$1 = (ServerLevel)level;
                this.addGameEventListener($$0, $$1);
            }
            this.level.onBlockEntityAdded($$0);
            this.updateBlockEntityTicker($$0);
        }
    }

    private boolean isInLevel() {
        return this.loaded || this.level.isClientSide();
    }

    boolean isTicking(BlockPos $$0) {
        if (!this.level.getWorldBorder().isWithinBounds($$0)) {
            return false;
        }
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            return this.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING) && $$1.areEntitiesLoaded(ChunkPos.asLong($$0));
        }
        return true;
    }

    @Override
    public void setBlockEntity(BlockEntity $$0) {
        BlockPos $$1 = $$0.getBlockPos();
        BlockState $$2 = this.getBlockState($$1);
        if (!$$2.hasBlockEntity()) {
            LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", $$0, $$1, $$2);
            return;
        }
        BlockState $$3 = $$0.getBlockState();
        if ($$2 != $$3) {
            if (!$$0.getType().isValid($$2)) {
                LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", $$0, $$1, $$2);
                return;
            }
            if ($$2.getBlock() != $$3.getBlock()) {
                LOGGER.warn("Block state mismatch on block entity {} in position {}, {} != {}, updating", $$0, $$1, $$2, $$3);
            }
            $$0.setBlockState($$2);
        }
        $$0.setLevel(this.level);
        $$0.clearRemoved();
        BlockEntity $$4 = this.blockEntities.put($$1.immutable(), $$0);
        if ($$4 != null && $$4 != $$0) {
            $$4.setRemoved();
        }
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos $$0, HolderLookup.Provider $$1) {
        BlockEntity $$2 = this.getBlockEntity($$0);
        if ($$2 != null && !$$2.isRemoved()) {
            CompoundTag $$3 = $$2.saveWithFullMetadata(this.level.registryAccess());
            $$3.putBoolean("keepPacked", false);
            return $$3;
        }
        CompoundTag $$4 = (CompoundTag)this.pendingBlockEntities.get($$0);
        if ($$4 != null) {
            $$4 = $$4.copy();
            $$4.putBoolean("keepPacked", true);
        }
        return $$4;
    }

    @Override
    public void removeBlockEntity(BlockPos $$0) {
        BlockEntity $$1;
        if (this.isInLevel() && ($$1 = (BlockEntity)this.blockEntities.remove($$0)) != null) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)level;
                this.removeGameEventListener($$1, $$2);
            }
            $$1.setRemoved();
        }
        this.removeBlockEntityTicker($$0);
    }

    private <T extends BlockEntity> void removeGameEventListener(T $$0, ServerLevel $$1) {
        GameEventListener $$3;
        Block $$2 = $$0.getBlockState().getBlock();
        if ($$2 instanceof EntityBlock && ($$3 = ((EntityBlock)((Object)$$2)).getListener($$1, $$0)) != null) {
            int $$4 = SectionPos.blockToSectionCoord($$0.getBlockPos().getY());
            GameEventListenerRegistry $$5 = this.getListenerRegistry($$4);
            $$5.unregister($$3);
        }
    }

    private void removeGameEventListenerRegistry(int $$0) {
        this.gameEventListenerRegistrySections.remove($$0);
    }

    private void removeBlockEntityTicker(BlockPos $$0) {
        RebindableTickingBlockEntityWrapper $$1 = this.tickersInLevel.remove($$0);
        if ($$1 != null) {
            $$1.rebind(NULL_TICKER);
        }
    }

    public void runPostLoad() {
        if (this.postLoad != null) {
            this.postLoad.run(this);
            this.postLoad = null;
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public void replaceWithPacketData(FriendlyByteBuf $$0, Map<Heightmap.Types, long[]> $$12, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> $$22) {
        this.clearAllBlockEntities();
        for (LevelChunkSection $$32 : this.sections) {
            $$32.read($$0);
        }
        $$12.forEach(this::a);
        this.initializeLightSources();
        try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);){
            $$22.accept(($$1, $$2, $$3) -> {
                BlockEntity $$4 = this.getBlockEntity($$1, EntityCreationType.IMMEDIATE);
                if ($$4 != null && $$3 != null && $$4.getType() == $$2) {
                    $$4.loadWithComponents(TagValueInput.create($$4.forChild($$4.problemPath()), (HolderLookup.Provider)this.level.registryAccess(), $$3));
                }
            });
        }
    }

    public void replaceBiomes(FriendlyByteBuf $$0) {
        for (LevelChunkSection $$1 : this.sections) {
            $$1.readBiomes($$0);
        }
    }

    public void setLoaded(boolean $$0) {
        this.loaded = $$0;
    }

    public Level getLevel() {
        return this.level;
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void postProcessGeneration(ServerLevel $$0) {
        ChunkPos $$1 = this.getPos();
        for (int $$2 = 0; $$2 < this.postProcessing.length; ++$$2) {
            if (this.postProcessing[$$2] == null) continue;
            for (Short $$3 : this.postProcessing[$$2]) {
                BlockState $$7;
                BlockPos $$4 = ProtoChunk.unpackOffsetCoordinates($$3, this.getSectionYFromSectionIndex($$2), $$1);
                BlockState $$5 = this.getBlockState($$4);
                FluidState $$6 = $$5.getFluidState();
                if (!$$6.isEmpty()) {
                    $$6.tick($$0, $$4, $$5);
                }
                if ($$5.getBlock() instanceof LiquidBlock || ($$7 = Block.updateFromNeighbourShapes($$5, $$0, $$4)) == $$5) continue;
                $$0.setBlock($$4, $$7, 276);
            }
            this.postProcessing[$$2].clear();
        }
        for (BlockPos $$8 : ImmutableList.copyOf(this.pendingBlockEntities.keySet())) {
            this.getBlockEntity($$8);
        }
        this.pendingBlockEntities.clear();
        this.upgradeData.upgrade(this);
    }

    @Nullable
    private BlockEntity promotePendingBlockEntity(BlockPos $$0, CompoundTag $$1) {
        BlockEntity $$5;
        BlockState $$2 = this.getBlockState($$0);
        if ("DUMMY".equals($$1.getStringOr("id", ""))) {
            if ($$2.hasBlockEntity()) {
                BlockEntity $$3 = ((EntityBlock)((Object)$$2.getBlock())).newBlockEntity($$0, $$2);
            } else {
                Object $$4 = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)$$0, (Object)$$2);
            }
        } else {
            $$5 = BlockEntity.loadStatic($$0, $$2, $$1, this.level.registryAccess());
        }
        if ($$5 != null) {
            $$5.setLevel(this.level);
            this.addAndRegisterBlockEntity($$5);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)$$2, (Object)$$0);
        }
        return $$5;
    }

    public void unpackTicks(long $$0) {
        this.blockTicks.unpack($$0);
        this.fluidTicks.unpack($$0);
    }

    public void registerTickContainerInLevel(ServerLevel $$0) {
        ((LevelTicks)$$0.getBlockTicks()).addContainer(this.chunkPos, this.blockTicks);
        ((LevelTicks)$$0.getFluidTicks()).addContainer(this.chunkPos, this.fluidTicks);
    }

    public void unregisterTickContainerFromLevel(ServerLevel $$0) {
        ((LevelTicks)$$0.getBlockTicks()).removeContainer(this.chunkPos);
        ((LevelTicks)$$0.getFluidTicks()).removeContainer(this.chunkPos);
    }

    @Override
    public ChunkStatus getPersistedStatus() {
        return ChunkStatus.FULL;
    }

    public FullChunkStatus getFullStatus() {
        if (this.fullStatus == null) {
            return FullChunkStatus.FULL;
        }
        return this.fullStatus.get();
    }

    public void setFullStatus(Supplier<FullChunkStatus> $$0) {
        this.fullStatus = $$0;
    }

    public void clearAllBlockEntities() {
        this.blockEntities.values().forEach(BlockEntity::setRemoved);
        this.blockEntities.clear();
        this.tickersInLevel.values().forEach($$0 -> $$0.rebind(NULL_TICKER));
        this.tickersInLevel.clear();
    }

    public void registerAllBlockEntitiesAfterLevelLoad() {
        this.blockEntities.values().forEach($$0 -> {
            Level $$1 = this.level;
            if ($$1 instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)$$1;
                this.addGameEventListener($$0, $$2);
            }
            this.level.onBlockEntityAdded((BlockEntity)$$0);
            this.updateBlockEntityTicker($$0);
        });
    }

    private <T extends BlockEntity> void addGameEventListener(T $$0, ServerLevel $$1) {
        GameEventListener $$3;
        Block $$2 = $$0.getBlockState().getBlock();
        if ($$2 instanceof EntityBlock && ($$3 = ((EntityBlock)((Object)$$2)).getListener($$1, $$0)) != null) {
            this.getListenerRegistry(SectionPos.blockToSectionCoord($$0.getBlockPos().getY())).register($$3);
        }
    }

    private <T extends BlockEntity> void updateBlockEntityTicker(T $$0) {
        BlockState $$1 = $$0.getBlockState();
        BlockEntityTicker<?> $$22 = $$1.getTicker(this.level, $$0.getType());
        if ($$22 == null) {
            this.removeBlockEntityTicker($$0.getBlockPos());
        } else {
            this.tickersInLevel.compute($$0.getBlockPos(), ($$2, $$3) -> {
                TickingBlockEntity $$4 = this.createTicker($$0, $$22);
                if ($$3 != null) {
                    $$3.rebind($$4);
                    return $$3;
                }
                if (this.isInLevel()) {
                    RebindableTickingBlockEntityWrapper $$5 = new RebindableTickingBlockEntityWrapper($$4);
                    this.level.addBlockEntityTicker($$5);
                    return $$5;
                }
                return null;
            });
        }
    }

    private <T extends BlockEntity> TickingBlockEntity createTicker(T $$0, BlockEntityTicker<T> $$1) {
        return new BoundTickingBlockEntity(this, $$0, $$1);
    }

    @FunctionalInterface
    public static interface PostLoadProcessor {
        public void run(LevelChunk var1);
    }

    @FunctionalInterface
    public static interface UnsavedListener {
        public void setUnsaved(ChunkPos var1);
    }

    public static final class EntityCreationType
    extends Enum<EntityCreationType> {
        public static final /* enum */ EntityCreationType IMMEDIATE = new EntityCreationType();
        public static final /* enum */ EntityCreationType QUEUED = new EntityCreationType();
        public static final /* enum */ EntityCreationType CHECK = new EntityCreationType();
        private static final /* synthetic */ EntityCreationType[] $VALUES;

        public static EntityCreationType[] values() {
            return (EntityCreationType[])$VALUES.clone();
        }

        public static EntityCreationType valueOf(String $$0) {
            return Enum.valueOf(EntityCreationType.class, $$0);
        }

        private static /* synthetic */ EntityCreationType[] a() {
            return new EntityCreationType[]{IMMEDIATE, QUEUED, CHECK};
        }

        static {
            $VALUES = EntityCreationType.a();
        }
    }

    static class RebindableTickingBlockEntityWrapper
    implements TickingBlockEntity {
        private TickingBlockEntity ticker;

        RebindableTickingBlockEntityWrapper(TickingBlockEntity $$0) {
            this.ticker = $$0;
        }

        void rebind(TickingBlockEntity $$0) {
            this.ticker = $$0;
        }

        @Override
        public void tick() {
            this.ticker.tick();
        }

        @Override
        public boolean isRemoved() {
            return this.ticker.isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return this.ticker.getPos();
        }

        @Override
        public String getType() {
            return this.ticker.getType();
        }

        public String toString() {
            return String.valueOf(this.ticker) + " <wrapped>";
        }
    }

    static class BoundTickingBlockEntity<T extends BlockEntity>
    implements TickingBlockEntity {
        private final T blockEntity;
        private final BlockEntityTicker<T> ticker;
        private boolean loggedInvalidBlockState;
        final /* synthetic */ LevelChunk this$0;

        BoundTickingBlockEntity(T $$0, BlockEntityTicker<T> $$1) {
            this.this$0 = var1_1;
            this.blockEntity = $$0;
            this.ticker = $$1;
        }

        @Override
        public void tick() {
            BlockPos $$0;
            if (!((BlockEntity)this.blockEntity).isRemoved() && ((BlockEntity)this.blockEntity).hasLevel() && this.this$0.isTicking($$0 = ((BlockEntity)this.blockEntity).getBlockPos())) {
                try {
                    ProfilerFiller $$1 = Profiler.get();
                    $$1.push(this::getType);
                    BlockState $$2 = this.this$0.getBlockState($$0);
                    if (((BlockEntity)this.blockEntity).getType().isValid($$2)) {
                        this.ticker.tick(this.this$0.level, ((BlockEntity)this.blockEntity).getBlockPos(), $$2, this.blockEntity);
                        this.loggedInvalidBlockState = false;
                    } else if (!this.loggedInvalidBlockState) {
                        this.loggedInvalidBlockState = true;
                        LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", LogUtils.defer(this::getType), LogUtils.defer(this::getPos), $$2);
                    }
                    $$1.pop();
                } catch (Throwable $$3) {
                    CrashReport $$4 = CrashReport.forThrowable($$3, "Ticking block entity");
                    CrashReportCategory $$5 = $$4.addCategory("Block entity being ticked");
                    ((BlockEntity)this.blockEntity).fillCrashReportCategory($$5);
                    throw new ReportedException($$4);
                }
            }
        }

        @Override
        public boolean isRemoved() {
            return ((BlockEntity)this.blockEntity).isRemoved();
        }

        @Override
        public BlockPos getPos() {
            return ((BlockEntity)this.blockEntity).getBlockPos();
        }

        @Override
        public String getType() {
            return BlockEntityType.getKey(((BlockEntity)this.blockEntity).getType()).toString();
        }

        public String toString() {
            return "Level ticker for " + this.getType() + "@" + String.valueOf(this.getPos());
        }
    }
}

