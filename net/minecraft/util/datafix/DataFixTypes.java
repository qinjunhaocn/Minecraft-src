/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.fixes.References;

public final class DataFixTypes
extends Enum<DataFixTypes> {
    public static final /* enum */ DataFixTypes LEVEL = new DataFixTypes(References.LEVEL);
    public static final /* enum */ DataFixTypes LEVEL_SUMMARY = new DataFixTypes(References.LIGHTWEIGHT_LEVEL);
    public static final /* enum */ DataFixTypes PLAYER = new DataFixTypes(References.PLAYER);
    public static final /* enum */ DataFixTypes CHUNK = new DataFixTypes(References.CHUNK);
    public static final /* enum */ DataFixTypes HOTBAR = new DataFixTypes(References.HOTBAR);
    public static final /* enum */ DataFixTypes OPTIONS = new DataFixTypes(References.OPTIONS);
    public static final /* enum */ DataFixTypes STRUCTURE = new DataFixTypes(References.STRUCTURE);
    public static final /* enum */ DataFixTypes STATS = new DataFixTypes(References.STATS);
    public static final /* enum */ DataFixTypes SAVED_DATA_COMMAND_STORAGE = new DataFixTypes(References.SAVED_DATA_COMMAND_STORAGE);
    public static final /* enum */ DataFixTypes SAVED_DATA_FORCED_CHUNKS = new DataFixTypes(References.SAVED_DATA_TICKETS);
    public static final /* enum */ DataFixTypes SAVED_DATA_MAP_DATA = new DataFixTypes(References.SAVED_DATA_MAP_DATA);
    public static final /* enum */ DataFixTypes SAVED_DATA_MAP_INDEX = new DataFixTypes(References.SAVED_DATA_MAP_INDEX);
    public static final /* enum */ DataFixTypes SAVED_DATA_RAIDS = new DataFixTypes(References.SAVED_DATA_RAIDS);
    public static final /* enum */ DataFixTypes SAVED_DATA_RANDOM_SEQUENCES = new DataFixTypes(References.SAVED_DATA_RANDOM_SEQUENCES);
    public static final /* enum */ DataFixTypes SAVED_DATA_SCOREBOARD = new DataFixTypes(References.SAVED_DATA_SCOREBOARD);
    public static final /* enum */ DataFixTypes SAVED_DATA_STRUCTURE_FEATURE_INDICES = new DataFixTypes(References.SAVED_DATA_STRUCTURE_FEATURE_INDICES);
    public static final /* enum */ DataFixTypes ADVANCEMENTS = new DataFixTypes(References.ADVANCEMENTS);
    public static final /* enum */ DataFixTypes POI_CHUNK = new DataFixTypes(References.POI_CHUNK);
    public static final /* enum */ DataFixTypes WORLD_GEN_SETTINGS = new DataFixTypes(References.WORLD_GEN_SETTINGS);
    public static final /* enum */ DataFixTypes ENTITY_CHUNK = new DataFixTypes(References.ENTITY_CHUNK);
    public static final Set<DSL.TypeReference> TYPES_FOR_LEVEL_LIST;
    private final DSL.TypeReference type;
    private static final /* synthetic */ DataFixTypes[] $VALUES;

    public static DataFixTypes[] values() {
        return (DataFixTypes[])$VALUES.clone();
    }

    public static DataFixTypes valueOf(String $$0) {
        return Enum.valueOf(DataFixTypes.class, $$0);
    }

    private DataFixTypes(DSL.TypeReference $$0) {
        this.type = $$0;
    }

    static int currentVersion() {
        return SharedConstants.getCurrentVersion().dataVersion().version();
    }

    public <A> Codec<A> wrapCodec(final Codec<A> $$0, final DataFixer $$1, final int $$2) {
        return new Codec<A>(){

            public <T> DataResult<T> encode(A $$02, DynamicOps<T> $$12, T $$22) {
                return $$0.encode($$02, $$12, $$22).flatMap($$1 -> $$12.mergeToMap($$1, $$12.createString("DataVersion"), $$12.createInt(DataFixTypes.currentVersion())));
            }

            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> $$02, T $$12) {
                int $$22 = $$02.get($$12, "DataVersion").flatMap(arg_0 -> $$02.getNumberValue(arg_0)).map(Number::intValue).result().orElse($$2);
                Dynamic $$3 = new Dynamic($$02, $$02.remove($$12, "DataVersion"));
                Dynamic $$4 = DataFixTypes.this.updateToCurrentVersion($$1, $$3, $$22);
                return $$0.decode($$4);
            }
        };
    }

    public <T> Dynamic<T> update(DataFixer $$0, Dynamic<T> $$1, int $$2, int $$3) {
        return $$0.update(this.type, $$1, $$2, $$3);
    }

    public <T> Dynamic<T> updateToCurrentVersion(DataFixer $$0, Dynamic<T> $$1, int $$2) {
        return this.update($$0, $$1, $$2, DataFixTypes.currentVersion());
    }

    public CompoundTag update(DataFixer $$0, CompoundTag $$1, int $$2, int $$3) {
        return (CompoundTag)this.update($$0, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$1), $$2, $$3).getValue();
    }

    public CompoundTag updateToCurrentVersion(DataFixer $$0, CompoundTag $$1, int $$2) {
        return this.update($$0, $$1, $$2, DataFixTypes.currentVersion());
    }

    private static /* synthetic */ DataFixTypes[] b() {
        return new DataFixTypes[]{LEVEL, LEVEL_SUMMARY, PLAYER, CHUNK, HOTBAR, OPTIONS, STRUCTURE, STATS, SAVED_DATA_COMMAND_STORAGE, SAVED_DATA_FORCED_CHUNKS, SAVED_DATA_MAP_DATA, SAVED_DATA_MAP_INDEX, SAVED_DATA_RAIDS, SAVED_DATA_RANDOM_SEQUENCES, SAVED_DATA_SCOREBOARD, SAVED_DATA_STRUCTURE_FEATURE_INDICES, ADVANCEMENTS, POI_CHUNK, WORLD_GEN_SETTINGS, ENTITY_CHUNK};
    }

    static {
        $VALUES = DataFixTypes.b();
        TYPES_FOR_LEVEL_LIST = Set.of((Object)DataFixTypes.LEVEL_SUMMARY.type);
    }
}

