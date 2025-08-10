/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTicks;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public final class SerializableChunkData
extends Record {
    private final Registry<Biome> biomeRegistry;
    private final ChunkPos chunkPos;
    private final int minSectionY;
    private final long lastUpdateTime;
    private final long inhabitedTime;
    private final ChunkStatus chunkStatus;
    @Nullable
    private final BlendingData.Packed blendingData;
    @Nullable
    private final BelowZeroRetrogen belowZeroRetrogen;
    private final UpgradeData upgradeData;
    @Nullable
    private final long[] carvingMask;
    private final Map<Heightmap.Types, long[]> heightmaps;
    private final ChunkAccess.PackedTicks packedTicks;
    private final ShortList[] postProcessingSections;
    private final boolean lightCorrect;
    private final List<SectionData> sectionData;
    private final List<CompoundTag> entities;
    private final List<CompoundTag> blockEntities;
    private final CompoundTag structureData;
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
    private static final Codec<List<SavedTick<Block>>> BLOCK_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.BLOCK.byNameCodec()).listOf();
    private static final Codec<List<SavedTick<Fluid>>> FLUID_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.FLUID.byNameCodec()).listOf();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_UPGRADE_DATA = "UpgradeData";
    private static final String BLOCK_TICKS_TAG = "block_ticks";
    private static final String FLUID_TICKS_TAG = "fluid_ticks";
    public static final String X_POS_TAG = "xPos";
    public static final String Z_POS_TAG = "zPos";
    public static final String HEIGHTMAPS_TAG = "Heightmaps";
    public static final String IS_LIGHT_ON_TAG = "isLightOn";
    public static final String SECTIONS_TAG = "sections";
    public static final String BLOCK_LIGHT_TAG = "BlockLight";
    public static final String SKY_LIGHT_TAG = "SkyLight";

    public SerializableChunkData(Registry<Biome> $$0, ChunkPos $$1, int $$2, long $$3, long $$4, ChunkStatus $$5, @Nullable BlendingData.Packed $$6, @Nullable BelowZeroRetrogen $$7, UpgradeData $$8, @Nullable long[] $$9, Map<Heightmap.Types, long[]> $$10, ChunkAccess.PackedTicks $$11, ShortList[] $$12, boolean $$13, List<SectionData> $$14, List<CompoundTag> $$15, List<CompoundTag> $$16, CompoundTag $$17) {
        this.biomeRegistry = $$0;
        this.chunkPos = $$1;
        this.minSectionY = $$2;
        this.lastUpdateTime = $$3;
        this.inhabitedTime = $$4;
        this.chunkStatus = $$5;
        this.blendingData = $$6;
        this.belowZeroRetrogen = $$7;
        this.upgradeData = $$8;
        this.carvingMask = $$9;
        this.heightmaps = $$10;
        this.packedTicks = $$11;
        this.postProcessingSections = $$12;
        this.lightCorrect = $$13;
        this.sectionData = $$14;
        this.entities = $$15;
        this.blockEntities = $$16;
        this.structureData = $$17;
    }

    @Nullable
    public static SerializableChunkData parse(LevelHeightAccessor $$0, RegistryAccess $$12, CompoundTag $$2) {
        if ($$2.getString("Status").isEmpty()) {
            return null;
        }
        ChunkPos $$32 = new ChunkPos($$2.getIntOr(X_POS_TAG, 0), $$2.getIntOr(Z_POS_TAG, 0));
        long $$4 = $$2.getLongOr("LastUpdate", 0L);
        long $$5 = $$2.getLongOr("InhabitedTime", 0L);
        ChunkStatus $$6 = $$2.read("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY);
        UpgradeData $$7 = $$2.getCompound(TAG_UPGRADE_DATA).map($$1 -> new UpgradeData((CompoundTag)$$1, $$0)).orElse(UpgradeData.EMPTY);
        boolean $$8 = $$2.getBooleanOr(IS_LIGHT_ON_TAG, false);
        BlendingData.Packed $$9 = $$2.read("blending_data", BlendingData.Packed.CODEC).orElse(null);
        BelowZeroRetrogen $$10 = $$2.read("below_zero_retrogen", BelowZeroRetrogen.CODEC).orElse(null);
        long[] $$11 = $$2.getLongArray("carving_mask").orElse(null);
        EnumMap<Heightmap.Types, long[]> $$122 = new EnumMap<Heightmap.Types, long[]>(Heightmap.Types.class);
        $$2.getCompound(HEIGHTMAPS_TAG).ifPresent($$22 -> {
            for (Heightmap.Types $$3 : $$6.heightmapsAfter()) {
                $$22.getLongArray($$3.getSerializationKey()).ifPresent($$2 -> $$122.put($$3, (long[])$$2));
            }
        });
        List<SavedTick<Block>> $$13 = SavedTick.filterTickListForChunk($$2.read(BLOCK_TICKS_TAG, BLOCK_TICKS_CODEC).orElse(List.of()), $$32);
        List<SavedTick<Fluid>> $$14 = SavedTick.filterTickListForChunk($$2.read(FLUID_TICKS_TAG, FLUID_TICKS_CODEC).orElse(List.of()), $$32);
        ChunkAccess.PackedTicks $$15 = new ChunkAccess.PackedTicks($$13, $$14);
        ListTag $$16 = $$2.getListOrEmpty("PostProcessing");
        ShortList[] $$17 = new ShortList[$$16.size()];
        for (int $$18 = 0; $$18 < $$16.size(); ++$$18) {
            ListTag $$19 = $$16.getListOrEmpty($$18);
            ShortArrayList $$20 = new ShortArrayList($$19.size());
            for (int $$21 = 0; $$21 < $$19.size(); ++$$21) {
                $$20.add($$19.getShortOr($$21, (short)0));
            }
            $$17[$$18] = $$20;
        }
        List $$222 = $$2.getList("entities").stream().flatMap(ListTag::compoundStream).toList();
        List $$23 = $$2.getList("block_entities").stream().flatMap(ListTag::compoundStream).toList();
        CompoundTag $$24 = $$2.getCompoundOrEmpty("structures");
        ListTag $$25 = $$2.getListOrEmpty(SECTIONS_TAG);
        ArrayList<SectionData> $$26 = new ArrayList<SectionData>($$25.size());
        HolderLookup.RegistryLookup $$27 = $$12.lookupOrThrow(Registries.BIOME);
        Codec<PalettedContainerRO<Holder<Biome>>> $$28 = SerializableChunkData.makeBiomeCodec((Registry<Biome>)$$27);
        for (int $$29 = 0; $$29 < $$25.size(); ++$$29) {
            LevelChunkSection $$36;
            Optional<CompoundTag> $$30 = $$25.getCompound($$29);
            if ($$30.isEmpty()) continue;
            CompoundTag $$31 = $$30.get();
            byte $$322 = $$31.getByteOr("Y", (byte)0);
            if ($$322 >= $$0.getMinSectionY() && $$322 <= $$0.getMaxSectionY()) {
                PalettedContainer $$33 = $$31.getCompound("block_states").map($$22 -> (PalettedContainer)BLOCK_STATE_CODEC.parse((DynamicOps)NbtOps.INSTANCE, $$22).promotePartial($$2 -> SerializableChunkData.logErrors($$32, $$322, $$2)).getOrThrow(ChunkReadException::new)).orElseGet(() -> new PalettedContainer<BlockState>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES));
                PalettedContainerRO $$34 = $$31.getCompound("biomes").map($$3 -> (PalettedContainerRO)$$28.parse((DynamicOps)NbtOps.INSTANCE, $$3).promotePartial($$2 -> SerializableChunkData.logErrors($$32, $$322, $$2)).getOrThrow(ChunkReadException::new)).orElseGet(() -> SerializableChunkData.lambda$parse$8((Registry)$$27));
                LevelChunkSection $$35 = new LevelChunkSection($$33, $$34);
            } else {
                $$36 = null;
            }
            DataLayer $$37 = $$31.getByteArray(BLOCK_LIGHT_TAG).map(DataLayer::new).orElse(null);
            DataLayer $$38 = $$31.getByteArray(SKY_LIGHT_TAG).map(DataLayer::new).orElse(null);
            $$26.add(new SectionData($$322, $$36, $$37, $$38));
        }
        return new SerializableChunkData((Registry<Biome>)$$27, $$32, $$0.getMinSectionY(), $$4, $$5, $$6, $$9, $$10, $$7, $$11, $$122, $$15, $$17, $$8, $$26, $$222, $$23, $$24);
    }

    public ProtoChunk read(ServerLevel $$0, PoiManager $$1, RegionStorageInfo $$2, ChunkPos $$3) {
        ProtoChunk $$22;
        if (!Objects.equals($$3, this.chunkPos)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", $$3, $$3, this.chunkPos);
            $$0.getServer().reportMisplacedChunk(this.chunkPos, $$3, $$2);
        }
        int $$4 = $$0.getSectionsCount();
        LevelChunkSection[] $$5 = new LevelChunkSection[$$4];
        boolean $$6 = $$0.dimensionType().hasSkyLight();
        ServerChunkCache $$7 = $$0.getChunkSource();
        LevelLightEngine $$8 = ((ChunkSource)$$7).getLightEngine();
        HolderLookup.RegistryLookup $$9 = $$0.registryAccess().lookupOrThrow(Registries.BIOME);
        boolean $$10 = false;
        for (SectionData $$11 : this.sectionData) {
            boolean $$14;
            SectionPos $$12 = SectionPos.of($$3, $$11.y);
            if ($$11.chunkSection != null) {
                $$5[$$0.getSectionIndexFromSectionY((int)$$11.y)] = $$11.chunkSection;
                $$1.checkConsistencyWithBlocks($$12, $$11.chunkSection);
            }
            boolean $$13 = $$11.blockLight != null;
            boolean bl = $$14 = $$6 && $$11.skyLight != null;
            if (!$$13 && !$$14) continue;
            if (!$$10) {
                $$8.retainData($$3, true);
                $$10 = true;
            }
            if ($$13) {
                $$8.queueSectionData(LightLayer.BLOCK, $$12, $$11.blockLight);
            }
            if (!$$14) continue;
            $$8.queueSectionData(LightLayer.SKY, $$12, $$11.skyLight);
        }
        ChunkType $$15 = this.chunkStatus.getChunkType();
        if ($$15 == ChunkType.LEVELCHUNK) {
            LevelChunkTicks<Block> $$16 = new LevelChunkTicks<Block>(this.packedTicks.blocks());
            LevelChunkTicks<Fluid> $$17 = new LevelChunkTicks<Fluid>(this.packedTicks.fluids());
            LevelChunk $$18 = new LevelChunk($$0.getLevel(), $$3, this.upgradeData, $$16, $$17, this.inhabitedTime, $$5, SerializableChunkData.postLoadChunk($$0, this.entities, this.blockEntities), BlendingData.unpack(this.blendingData));
        } else {
            ProtoChunk $$21;
            ProtoChunkTicks<Block> $$19 = ProtoChunkTicks.load(this.packedTicks.blocks());
            ProtoChunkTicks<Fluid> $$20 = ProtoChunkTicks.load(this.packedTicks.fluids());
            $$22 = $$21 = new ProtoChunk($$3, this.upgradeData, $$5, $$19, $$20, $$0, (Registry<Biome>)$$9, BlendingData.unpack(this.blendingData));
            $$22.setInhabitedTime(this.inhabitedTime);
            if (this.belowZeroRetrogen != null) {
                $$21.setBelowZeroRetrogen(this.belowZeroRetrogen);
            }
            $$21.setPersistedStatus(this.chunkStatus);
            if (this.chunkStatus.isOrAfter(ChunkStatus.INITIALIZE_LIGHT)) {
                $$21.setLightEngine($$8);
            }
        }
        $$22.setLightCorrect(this.lightCorrect);
        EnumSet<Heightmap.Types> $$23 = EnumSet.noneOf(Heightmap.Types.class);
        for (Heightmap.Types $$24 : ((ChunkAccess)$$22).getPersistedStatus().heightmapsAfter()) {
            long[] $$25 = this.heightmaps.get($$24);
            if ($$25 != null) {
                $$22.a($$24, $$25);
                continue;
            }
            $$23.add($$24);
        }
        Heightmap.primeHeightmaps($$22, $$23);
        $$22.setAllStarts(SerializableChunkData.unpackStructureStart(StructurePieceSerializationContext.fromLevel($$0), this.structureData, $$0.getSeed()));
        $$22.setAllReferences(SerializableChunkData.unpackStructureReferences($$0.registryAccess(), $$3, this.structureData));
        for (int $$26 = 0; $$26 < this.postProcessingSections.length; ++$$26) {
            ((ChunkAccess)$$22).addPackedPostProcess(this.postProcessingSections[$$26], $$26);
        }
        if ($$15 == ChunkType.LEVELCHUNK) {
            return new ImposterProtoChunk((LevelChunk)((Object)$$22), false);
        }
        ProtoChunk $$27 = $$22;
        for (CompoundTag $$28 : this.entities) {
            $$27.addEntity($$28);
        }
        for (CompoundTag $$29 : this.blockEntities) {
            $$27.setBlockEntityNbt($$29);
        }
        if (this.carvingMask != null) {
            $$27.setCarvingMask(new CarvingMask(this.carvingMask, $$22.getMinY()));
        }
        return $$27;
    }

    private static void logErrors(ChunkPos $$0, int $$1, String $$2) {
        LOGGER.error("Recoverable errors when loading section [{}, {}, {}]: {}", $$0.x, $$1, $$0.z, $$2);
    }

    private static Codec<PalettedContainerRO<Holder<Biome>>> makeBiomeCodec(Registry<Biome> $$0) {
        return PalettedContainer.codecRO($$0.asHolderIdMap(), $$0.holderByNameCodec(), PalettedContainer.Strategy.SECTION_BIOMES, $$0.getOrThrow(Biomes.PLAINS));
    }

    public static SerializableChunkData copyOf(ServerLevel $$02, ChunkAccess $$1) {
        if (!$$1.canBeSerialized()) {
            throw new IllegalArgumentException("Chunk can't be serialized: " + String.valueOf($$1));
        }
        ChunkPos $$2 = $$1.getPos();
        ArrayList<SectionData> $$3 = new ArrayList<SectionData>();
        LevelChunkSection[] $$4 = $$1.d();
        ThreadedLevelLightEngine $$5 = $$02.getChunkSource().getLightEngine();
        for (int $$6 = $$5.getMinLightSection(); $$6 < $$5.getMaxLightSection(); ++$$6) {
            DataLayer $$12;
            int $$7 = $$1.getSectionIndexFromSectionY($$6);
            boolean $$8 = $$7 >= 0 && $$7 < $$4.length;
            DataLayer $$9 = $$5.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of($$2, $$6));
            DataLayer $$10 = $$5.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of($$2, $$6));
            DataLayer dataLayer = $$9 != null && !$$9.isEmpty() ? $$9.copy() : null;
            DataLayer dataLayer2 = $$12 = $$10 != null && !$$10.isEmpty() ? $$10.copy() : null;
            if (!$$8 && dataLayer == null && $$12 == null) continue;
            LevelChunkSection $$13 = $$8 ? $$4[$$7].copy() : null;
            $$3.add(new SectionData($$6, $$13, dataLayer, $$12));
        }
        ArrayList<CompoundTag> $$14 = new ArrayList<CompoundTag>($$1.getBlockEntitiesPos().size());
        for (BlockPos $$15 : $$1.getBlockEntitiesPos()) {
            CompoundTag $$16 = $$1.getBlockEntityNbtForSaving($$15, $$02.registryAccess());
            if ($$16 == null) continue;
            $$14.add($$16);
        }
        ArrayList<CompoundTag> $$17 = new ArrayList<CompoundTag>();
        long[] $$18 = null;
        if ($$1.getPersistedStatus().getChunkType() == ChunkType.PROTOCHUNK) {
            ProtoChunk $$19 = (ProtoChunk)$$1;
            $$17.addAll($$19.getEntities());
            CarvingMask $$20 = $$19.getCarvingMask();
            if ($$20 != null) {
                $$18 = $$20.a();
            }
        }
        EnumMap<Heightmap.Types, long[]> $$21 = new EnumMap<Heightmap.Types, long[]>(Heightmap.Types.class);
        for (Map.Entry entry : $$1.getHeightmaps()) {
            if (!$$1.getPersistedStatus().heightmapsAfter().contains(entry.getKey())) continue;
            long[] $$23 = ((Heightmap)entry.getValue()).a();
            $$21.put((Heightmap.Types)entry.getKey(), (long[])$$23.clone());
        }
        ChunkAccess.PackedTicks $$24 = $$1.getTicksForSerialization($$02.getGameTime());
        ShortList[] shortListArray = (ShortList[])Arrays.stream($$1.p()).map($$0 -> $$0 != null ? new ShortArrayList($$0) : null).toArray(ShortList[]::new);
        CompoundTag $$26 = SerializableChunkData.packStructureData(StructurePieceSerializationContext.fromLevel($$02), $$2, $$1.getAllStarts(), $$1.getAllReferences());
        return new SerializableChunkData((Registry<Biome>)$$02.registryAccess().lookupOrThrow(Registries.BIOME), $$2, $$1.getMinSectionY(), $$02.getGameTime(), $$1.getInhabitedTime(), $$1.getPersistedStatus(), Optionull.map($$1.getBlendingData(), BlendingData::pack), $$1.getBelowZeroRetrogen(), $$1.getUpgradeData().copy(), $$18, $$21, $$24, shortListArray, $$1.isLightCorrect(), $$3, $$17, $$14, $$26);
    }

    public CompoundTag write() {
        CompoundTag $$0 = NbtUtils.addCurrentDataVersion(new CompoundTag());
        $$0.putInt(X_POS_TAG, this.chunkPos.x);
        $$0.putInt("yPos", this.minSectionY);
        $$0.putInt(Z_POS_TAG, this.chunkPos.z);
        $$0.putLong("LastUpdate", this.lastUpdateTime);
        $$0.putLong("InhabitedTime", this.inhabitedTime);
        $$0.putString("Status", BuiltInRegistries.CHUNK_STATUS.getKey(this.chunkStatus).toString());
        $$0.storeNullable("blending_data", BlendingData.Packed.CODEC, this.blendingData);
        $$0.storeNullable("below_zero_retrogen", BelowZeroRetrogen.CODEC, this.belowZeroRetrogen);
        if (!this.upgradeData.isEmpty()) {
            $$0.put(TAG_UPGRADE_DATA, this.upgradeData.write());
        }
        ListTag $$12 = new ListTag();
        Codec<PalettedContainerRO<Holder<Biome>>> $$22 = SerializableChunkData.makeBiomeCodec(this.biomeRegistry);
        for (SectionData $$3 : this.sectionData) {
            CompoundTag $$4 = new CompoundTag();
            LevelChunkSection $$5 = $$3.chunkSection;
            if ($$5 != null) {
                $$4.store("block_states", BLOCK_STATE_CODEC, $$5.getStates());
                $$4.store("biomes", $$22, $$5.getBiomes());
            }
            if ($$3.blockLight != null) {
                $$4.a(BLOCK_LIGHT_TAG, $$3.blockLight.a());
            }
            if ($$3.skyLight != null) {
                $$4.a(SKY_LIGHT_TAG, $$3.skyLight.a());
            }
            if ($$4.isEmpty()) continue;
            $$4.putByte("Y", (byte)$$3.y);
            $$12.add($$4);
        }
        $$0.put(SECTIONS_TAG, $$12);
        if (this.lightCorrect) {
            $$0.putBoolean(IS_LIGHT_ON_TAG, true);
        }
        ListTag $$6 = new ListTag();
        $$6.addAll(this.blockEntities);
        $$0.put("block_entities", $$6);
        if (this.chunkStatus.getChunkType() == ChunkType.PROTOCHUNK) {
            ListTag $$7 = new ListTag();
            $$7.addAll(this.entities);
            $$0.put("entities", $$7);
            if (this.carvingMask != null) {
                $$0.a("carving_mask", this.carvingMask);
            }
        }
        SerializableChunkData.saveTicks($$0, this.packedTicks);
        $$0.put("PostProcessing", SerializableChunkData.a(this.postProcessingSections));
        CompoundTag $$8 = new CompoundTag();
        this.heightmaps.forEach(($$1, $$2) -> $$8.put($$1.getSerializationKey(), new LongArrayTag((long[])$$2)));
        $$0.put(HEIGHTMAPS_TAG, $$8);
        $$0.put("structures", this.structureData);
        return $$0;
    }

    private static void saveTicks(CompoundTag $$0, ChunkAccess.PackedTicks $$1) {
        $$0.store(BLOCK_TICKS_TAG, BLOCK_TICKS_CODEC, $$1.blocks());
        $$0.store(FLUID_TICKS_TAG, FLUID_TICKS_CODEC, $$1.fluids());
    }

    public static ChunkStatus getChunkStatusFromTag(@Nullable CompoundTag $$0) {
        return $$0 != null ? $$0.read("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY) : ChunkStatus.EMPTY;
    }

    @Nullable
    private static LevelChunk.PostLoadProcessor postLoadChunk(ServerLevel $$0, List<CompoundTag> $$1, List<CompoundTag> $$2) {
        if ($$1.isEmpty() && $$2.isEmpty()) {
            return null;
        }
        return $$3 -> {
            if (!$$1.isEmpty()) {
                try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector($$3.problemPath(), LOGGER);){
                    $$0.addLegacyChunkEntities(EntityType.loadEntitiesRecursive(TagValueInput.create((ProblemReporter)$$4, (HolderLookup.Provider)$$0.registryAccess(), $$1), $$0, EntitySpawnReason.LOAD));
                }
            }
            for (CompoundTag $$5 : $$2) {
                boolean $$6 = $$5.getBooleanOr("keepPacked", false);
                if ($$6) {
                    $$3.setBlockEntityNbt($$5);
                    continue;
                }
                BlockPos $$7 = BlockEntity.getPosFromTag($$3.getPos(), $$5);
                BlockEntity $$8 = BlockEntity.loadStatic($$7, $$3.getBlockState($$7), $$5, $$0.registryAccess());
                if ($$8 == null) continue;
                $$3.setBlockEntity($$8);
            }
        };
    }

    private static CompoundTag packStructureData(StructurePieceSerializationContext $$0, ChunkPos $$1, Map<Structure, StructureStart> $$2, Map<Structure, LongSet> $$3) {
        CompoundTag $$4 = new CompoundTag();
        CompoundTag $$5 = new CompoundTag();
        HolderLookup.RegistryLookup $$6 = $$0.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        for (Map.Entry<Structure, StructureStart> $$7 : $$2.entrySet()) {
            ResourceLocation $$8 = $$6.getKey($$7.getKey());
            $$5.put($$8.toString(), $$7.getValue().createTag($$0, $$1));
        }
        $$4.put("starts", $$5);
        CompoundTag $$9 = new CompoundTag();
        for (Map.Entry<Structure, LongSet> $$10 : $$3.entrySet()) {
            if ($$10.getValue().isEmpty()) continue;
            ResourceLocation $$11 = $$6.getKey($$10.getKey());
            $$9.a($$11.toString(), $$10.getValue().toLongArray());
        }
        $$4.put("References", $$9);
        return $$4;
    }

    private static Map<Structure, StructureStart> unpackStructureStart(StructurePieceSerializationContext $$0, CompoundTag $$1, long $$2) {
        HashMap<Structure, StructureStart> $$3 = Maps.newHashMap();
        HolderLookup.RegistryLookup $$4 = $$0.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        CompoundTag $$5 = $$1.getCompoundOrEmpty("starts");
        for (String $$6 : $$5.keySet()) {
            ResourceLocation $$7 = ResourceLocation.tryParse($$6);
            Structure $$8 = (Structure)$$4.getValue($$7);
            if ($$8 == null) {
                LOGGER.error("Unknown structure start: {}", (Object)$$7);
                continue;
            }
            StructureStart $$9 = StructureStart.loadStaticStart($$0, $$5.getCompoundOrEmpty($$6), $$2);
            if ($$9 == null) continue;
            $$3.put($$8, $$9);
        }
        return $$3;
    }

    private static Map<Structure, LongSet> unpackStructureReferences(RegistryAccess $$0, ChunkPos $$1, CompoundTag $$2) {
        HashMap<Structure, LongSet> $$3 = Maps.newHashMap();
        HolderLookup.RegistryLookup $$4 = $$0.lookupOrThrow(Registries.STRUCTURE);
        CompoundTag $$5 = $$2.getCompoundOrEmpty("References");
        $$5.forEach((arg_0, arg_1) -> SerializableChunkData.lambda$unpackStructureReferences$14((Registry)$$4, $$1, $$3, arg_0, arg_1));
        return $$3;
    }

    private static ListTag a(ShortList[] $$0) {
        ListTag $$1 = new ListTag();
        for (ShortList $$2 : $$0) {
            ListTag $$3 = new ListTag();
            if ($$2 != null) {
                for (int $$4 = 0; $$4 < $$2.size(); ++$$4) {
                    $$3.add(ShortTag.valueOf($$2.getShort($$4)));
                }
            }
            $$1.add($$3);
        }
        return $$1;
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SerializableChunkData.class, "biomeRegistry;chunkPos;minSectionY;lastUpdateTime;inhabitedTime;chunkStatus;blendingData;belowZeroRetrogen;upgradeData;carvingMask;heightmaps;packedTicks;postProcessingSections;lightCorrect;sectionData;entities;blockEntities;structureData", "biomeRegistry", "chunkPos", "minSectionY", "lastUpdateTime", "inhabitedTime", "chunkStatus", "blendingData", "belowZeroRetrogen", "upgradeData", "carvingMask", "heightmaps", "packedTicks", "postProcessingSections", "lightCorrect", "sectionData", "entities", "blockEntities", "structureData"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SerializableChunkData.class, "biomeRegistry;chunkPos;minSectionY;lastUpdateTime;inhabitedTime;chunkStatus;blendingData;belowZeroRetrogen;upgradeData;carvingMask;heightmaps;packedTicks;postProcessingSections;lightCorrect;sectionData;entities;blockEntities;structureData", "biomeRegistry", "chunkPos", "minSectionY", "lastUpdateTime", "inhabitedTime", "chunkStatus", "blendingData", "belowZeroRetrogen", "upgradeData", "carvingMask", "heightmaps", "packedTicks", "postProcessingSections", "lightCorrect", "sectionData", "entities", "blockEntities", "structureData"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SerializableChunkData.class, "biomeRegistry;chunkPos;minSectionY;lastUpdateTime;inhabitedTime;chunkStatus;blendingData;belowZeroRetrogen;upgradeData;carvingMask;heightmaps;packedTicks;postProcessingSections;lightCorrect;sectionData;entities;blockEntities;structureData", "biomeRegistry", "chunkPos", "minSectionY", "lastUpdateTime", "inhabitedTime", "chunkStatus", "blendingData", "belowZeroRetrogen", "upgradeData", "carvingMask", "heightmaps", "packedTicks", "postProcessingSections", "lightCorrect", "sectionData", "entities", "blockEntities", "structureData"}, this, $$0);
    }

    public Registry<Biome> biomeRegistry() {
        return this.biomeRegistry;
    }

    public ChunkPos chunkPos() {
        return this.chunkPos;
    }

    public int minSectionY() {
        return this.minSectionY;
    }

    public long lastUpdateTime() {
        return this.lastUpdateTime;
    }

    public long inhabitedTime() {
        return this.inhabitedTime;
    }

    public ChunkStatus chunkStatus() {
        return this.chunkStatus;
    }

    @Nullable
    public BlendingData.Packed blendingData() {
        return this.blendingData;
    }

    @Nullable
    public BelowZeroRetrogen belowZeroRetrogen() {
        return this.belowZeroRetrogen;
    }

    public UpgradeData upgradeData() {
        return this.upgradeData;
    }

    @Nullable
    public long[] k() {
        return this.carvingMask;
    }

    public Map<Heightmap.Types, long[]> heightmaps() {
        return this.heightmaps;
    }

    public ChunkAccess.PackedTicks packedTicks() {
        return this.packedTicks;
    }

    public ShortList[] n() {
        return this.postProcessingSections;
    }

    public boolean lightCorrect() {
        return this.lightCorrect;
    }

    public List<SectionData> sectionData() {
        return this.sectionData;
    }

    public List<CompoundTag> entities() {
        return this.entities;
    }

    public List<CompoundTag> blockEntities() {
        return this.blockEntities;
    }

    public CompoundTag structureData() {
        return this.structureData;
    }

    private static /* synthetic */ void lambda$unpackStructureReferences$14(Registry $$0, ChunkPos $$1, Map $$22, String $$3, Tag $$4) {
        ResourceLocation $$5 = ResourceLocation.tryParse($$3);
        Structure $$6 = (Structure)$$0.getValue($$5);
        if ($$6 == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", (Object)$$5, (Object)$$1);
            return;
        }
        Optional<long[]> $$7 = $$4.asLongArray();
        if ($$7.isEmpty()) {
            return;
        }
        $$22.put($$6, new LongOpenHashSet(Arrays.stream($$7.get()).filter($$2 -> {
            ChunkPos $$3 = new ChunkPos($$2);
            if ($$3.getChessboardDistance($$1) > 8) {
                LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", $$5, $$3, $$1);
                return false;
            }
            return true;
        }).toArray()));
    }

    private static /* synthetic */ PalettedContainerRO lambda$parse$8(Registry $$0) {
        return new PalettedContainer<Holder.Reference>($$0.asHolderIdMap(), $$0.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
    }

    public static final class SectionData
    extends Record {
        final int y;
        @Nullable
        final LevelChunkSection chunkSection;
        @Nullable
        final DataLayer blockLight;
        @Nullable
        final DataLayer skyLight;

        public SectionData(int $$0, @Nullable LevelChunkSection $$1, @Nullable DataLayer $$2, @Nullable DataLayer $$3) {
            this.y = $$0;
            this.chunkSection = $$1;
            this.blockLight = $$2;
            this.skyLight = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SectionData.class, "y;chunkSection;blockLight;skyLight", "y", "chunkSection", "blockLight", "skyLight"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SectionData.class, "y;chunkSection;blockLight;skyLight", "y", "chunkSection", "blockLight", "skyLight"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SectionData.class, "y;chunkSection;blockLight;skyLight", "y", "chunkSection", "blockLight", "skyLight"}, this, $$0);
        }

        public int y() {
            return this.y;
        }

        @Nullable
        public LevelChunkSection chunkSection() {
            return this.chunkSection;
        }

        @Nullable
        public DataLayer blockLight() {
            return this.blockLight;
        }

        @Nullable
        public DataLayer skyLight() {
            return this.skyLight;
        }
    }

    public static class ChunkReadException
    extends NbtException {
        public ChunkReadException(String $$0) {
            super($$0);
        }
    }
}

