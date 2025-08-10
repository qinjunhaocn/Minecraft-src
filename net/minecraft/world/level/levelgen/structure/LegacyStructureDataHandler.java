/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
    private static final Map<String, String> CURRENT_TO_LEGACY_MAP = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put("Village", "Village");
        $$0.put("Mineshaft", "Mineshaft");
        $$0.put("Mansion", "Mansion");
        $$0.put("Igloo", "Temple");
        $$0.put("Desert_Pyramid", "Temple");
        $$0.put("Jungle_Pyramid", "Temple");
        $$0.put("Swamp_Hut", "Temple");
        $$0.put("Stronghold", "Stronghold");
        $$0.put("Monument", "Monument");
        $$0.put("Fortress", "Fortress");
        $$0.put("EndCity", "EndCity");
    });
    private static final Map<String, String> LEGACY_TO_CURRENT_MAP = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put("Iglu", "Igloo");
        $$0.put("TeDP", "Desert_Pyramid");
        $$0.put("TeJP", "Jungle_Pyramid");
        $$0.put("TeSH", "Swamp_Hut");
    });
    private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of((Object[])new String[]{"pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal", "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure", "village", "nether_fossil", "bastion_remnant"});
    private final boolean hasLegacyData;
    private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
    private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
    private final List<String> legacyKeys;
    private final List<String> currentKeys;

    public LegacyStructureDataHandler(@Nullable DimensionDataStorage $$0, List<String> $$1, List<String> $$2) {
        this.legacyKeys = $$1;
        this.currentKeys = $$2;
        this.populateCaches($$0);
        boolean $$3 = false;
        for (String $$4 : this.currentKeys) {
            $$3 |= this.dataMap.get($$4) != null;
        }
        this.hasLegacyData = $$3;
    }

    public void removeIndex(long $$0) {
        for (String $$1 : this.legacyKeys) {
            StructureFeatureIndexSavedData $$2 = this.indexMap.get($$1);
            if ($$2 == null || !$$2.hasUnhandledIndex($$0)) continue;
            $$2.removeIndex($$0);
        }
    }

    public CompoundTag updateFromLegacy(CompoundTag $$0) {
        CompoundTag $$1 = $$0.getCompoundOrEmpty("Level");
        ChunkPos $$2 = new ChunkPos($$1.getIntOr("xPos", 0), $$1.getIntOr("zPos", 0));
        if (this.isUnhandledStructureStart($$2.x, $$2.z)) {
            $$0 = this.updateStructureStart($$0, $$2);
        }
        CompoundTag $$3 = $$1.getCompoundOrEmpty("Structures");
        CompoundTag $$4 = $$3.getCompoundOrEmpty("References");
        for (String $$5 : this.currentKeys) {
            boolean $$6 = OLD_STRUCTURE_REGISTRY_KEYS.contains($$5.toLowerCase(Locale.ROOT));
            if ($$4.getLongArray($$5).isPresent() || !$$6) continue;
            int $$7 = 8;
            LongArrayList $$8 = new LongArrayList();
            for (int $$9 = $$2.x - 8; $$9 <= $$2.x + 8; ++$$9) {
                for (int $$10 = $$2.z - 8; $$10 <= $$2.z + 8; ++$$10) {
                    if (!this.hasLegacyStart($$9, $$10, $$5)) continue;
                    $$8.add(ChunkPos.asLong($$9, $$10));
                }
            }
            $$4.a($$5, $$8.toLongArray());
        }
        $$3.put("References", $$4);
        $$1.put("Structures", $$3);
        $$0.put("Level", $$1);
        return $$0;
    }

    private boolean hasLegacyStart(int $$0, int $$1, String $$2) {
        if (!this.hasLegacyData) {
            return false;
        }
        return this.dataMap.get($$2) != null && this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$2)).hasStartIndex(ChunkPos.asLong($$0, $$1));
    }

    private boolean isUnhandledStructureStart(int $$0, int $$1) {
        if (!this.hasLegacyData) {
            return false;
        }
        for (String $$2 : this.currentKeys) {
            if (this.dataMap.get($$2) == null || !this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$2)).hasUnhandledIndex(ChunkPos.asLong($$0, $$1))) continue;
            return true;
        }
        return false;
    }

    private CompoundTag updateStructureStart(CompoundTag $$0, ChunkPos $$1) {
        CompoundTag $$2 = $$0.getCompoundOrEmpty("Level");
        CompoundTag $$3 = $$2.getCompoundOrEmpty("Structures");
        CompoundTag $$4 = $$3.getCompoundOrEmpty("Starts");
        for (String $$5 : this.currentKeys) {
            CompoundTag $$8;
            Long2ObjectMap<CompoundTag> $$6 = this.dataMap.get($$5);
            if ($$6 == null) continue;
            long $$7 = $$1.toLong();
            if (!this.indexMap.get(CURRENT_TO_LEGACY_MAP.get($$5)).hasUnhandledIndex($$7) || ($$8 = (CompoundTag)$$6.get($$7)) == null) continue;
            $$4.put($$5, $$8);
        }
        $$3.put("Starts", $$4);
        $$2.put("Structures", $$3);
        $$0.put("Level", $$2);
        return $$0;
    }

    private void populateCaches(@Nullable DimensionDataStorage $$0) {
        if ($$0 == null) {
            return;
        }
        for (String $$12 : this.legacyKeys) {
            CompoundTag $$22 = new CompoundTag();
            try {
                $$22 = $$0.readTagFromDisk($$12, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493).getCompoundOrEmpty("data").getCompoundOrEmpty("Features");
                if ($$22.isEmpty()) {
                    continue;
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            $$22.forEach(($$02, $$1) -> {
                void $$3;
                if (!($$1 instanceof CompoundTag)) {
                    return;
                }
                CompoundTag $$2 = (CompoundTag)$$1;
                long $$4 = ChunkPos.asLong($$3.getIntOr("ChunkX", 0), $$3.getIntOr("ChunkZ", 0));
                ListTag $$5 = $$3.getListOrEmpty("Children");
                if (!$$5.isEmpty()) {
                    Optional<String> $$6 = $$5.getCompound(0).flatMap($$0 -> $$0.getString("id"));
                    $$6.map(LEGACY_TO_CURRENT_MAP::get).ifPresent(arg_0 -> LegacyStructureDataHandler.lambda$populateCaches$3((CompoundTag)$$3, arg_0));
                }
                $$3.getString("id").ifPresent(arg_0 -> this.lambda$populateCaches$5($$4, (CompoundTag)$$3, arg_0));
            });
            String $$3 = $$12 + "_index";
            StructureFeatureIndexSavedData $$4 = $$0.computeIfAbsent(StructureFeatureIndexSavedData.type($$3));
            if ($$4.getAll().isEmpty()) {
                StructureFeatureIndexSavedData $$5 = new StructureFeatureIndexSavedData();
                this.indexMap.put($$12, $$5);
                $$22.forEach(($$1, $$2) -> {
                    if ($$2 instanceof CompoundTag) {
                        CompoundTag $$3 = (CompoundTag)$$2;
                        $$5.addIndex(ChunkPos.asLong($$3.getIntOr("ChunkX", 0), $$3.getIntOr("ChunkZ", 0)));
                    }
                });
                continue;
            }
            this.indexMap.put($$12, $$4);
        }
    }

    public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> $$0, @Nullable DimensionDataStorage $$1) {
        if ($$0 == Level.OVERWORLD) {
            return new LegacyStructureDataHandler($$1, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
        }
        if ($$0 == Level.NETHER) {
            ImmutableList<String> $$2 = ImmutableList.of("Fortress");
            return new LegacyStructureDataHandler($$1, $$2, $$2);
        }
        if ($$0 == Level.END) {
            ImmutableList<String> $$3 = ImmutableList.of("EndCity");
            return new LegacyStructureDataHandler($$1, $$3, $$3);
        }
        throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", $$0));
    }

    private /* synthetic */ void lambda$populateCaches$5(long $$02, CompoundTag $$1, String $$2) {
        this.dataMap.computeIfAbsent($$2, $$0 -> new Long2ObjectOpenHashMap()).put($$02, (Object)$$1);
    }

    private static /* synthetic */ void lambda$populateCaches$3(CompoundTag $$0, String $$1) {
        $$0.putString("id", $$1);
    }
}

