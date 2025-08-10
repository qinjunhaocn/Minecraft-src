/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.entity.raid;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;

public class Raids
extends SavedData {
    private static final String RAID_FILE_ID = "raids";
    public static final Codec<Raids> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)RaidWithId.CODEC.listOf().optionalFieldOf(RAID_FILE_ID, (Object)List.of()).forGetter($$0 -> $$0.raidMap.int2ObjectEntrySet().stream().map(RaidWithId::from).toList()), (App)Codec.INT.fieldOf("next_id").forGetter($$0 -> $$0.nextId), (App)Codec.INT.fieldOf("tick").forGetter($$0 -> $$0.tick)).apply((Applicative)$$02, Raids::new));
    public static final SavedDataType<Raids> TYPE = new SavedDataType<Raids>("raids", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
    public static final SavedDataType<Raids> TYPE_END = new SavedDataType<Raids>("raids_end", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
    private final Int2ObjectMap<Raid> raidMap = new Int2ObjectOpenHashMap();
    private int nextId = 1;
    private int tick;

    public static SavedDataType<Raids> getType(Holder<DimensionType> $$0) {
        if ($$0.is(BuiltinDimensionTypes.END)) {
            return TYPE_END;
        }
        return TYPE;
    }

    public Raids() {
        this.setDirty();
    }

    private Raids(List<RaidWithId> $$0, int $$1, int $$2) {
        for (RaidWithId $$3 : $$0) {
            this.raidMap.put($$3.id, (Object)$$3.raid);
        }
        this.nextId = $$1;
        this.tick = $$2;
    }

    @Nullable
    public Raid get(int $$0) {
        return (Raid)this.raidMap.get($$0);
    }

    public OptionalInt getId(Raid $$0) {
        for (Int2ObjectMap.Entry $$1 : this.raidMap.int2ObjectEntrySet()) {
            if ($$1.getValue() != $$0) continue;
            return OptionalInt.of($$1.getIntKey());
        }
        return OptionalInt.empty();
    }

    public void tick(ServerLevel $$0) {
        ++this.tick;
        ObjectIterator $$1 = this.raidMap.values().iterator();
        while ($$1.hasNext()) {
            Raid $$2 = (Raid)$$1.next();
            if ($$0.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                $$2.stop();
            }
            if ($$2.isStopped()) {
                $$1.remove();
                this.setDirty();
                continue;
            }
            $$2.tick($$0);
        }
        if (this.tick % 200 == 0) {
            this.setDirty();
        }
        DebugPackets.sendRaids($$0, (Collection<Raid>)this.raidMap.values());
    }

    public static boolean canJoinRaid(Raider $$0) {
        return $$0.isAlive() && $$0.canJoinRaid() && $$0.getNoActionTime() <= 2400;
    }

    @Nullable
    public Raid createOrExtendRaid(ServerPlayer $$02, BlockPos $$1) {
        BlockPos $$10;
        if ($$02.isSpectator()) {
            return null;
        }
        ServerLevel $$2 = $$02.level();
        if ($$2.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        }
        DimensionType $$3 = $$2.dimensionType();
        if (!$$3.hasRaids()) {
            return null;
        }
        List $$4 = $$2.getPoiManager().getInRange($$0 -> $$0.is(PoiTypeTags.VILLAGE), $$1, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
        int $$5 = 0;
        Vec3 $$6 = Vec3.ZERO;
        for (PoiRecord $$7 : $$4) {
            BlockPos $$8 = $$7.getPos();
            $$6 = $$6.add($$8.getX(), $$8.getY(), $$8.getZ());
            ++$$5;
        }
        if ($$5 > 0) {
            $$6 = $$6.scale(1.0 / (double)$$5);
            BlockPos $$9 = BlockPos.containing($$6);
        } else {
            $$10 = $$1;
        }
        Raid $$11 = this.getOrCreateRaid($$2, $$10);
        if (!$$11.isStarted() && !this.raidMap.containsValue((Object)$$11)) {
            this.raidMap.put(this.getUniqueId(), (Object)$$11);
        }
        if (!$$11.isStarted() || $$11.getRaidOmenLevel() < $$11.getMaxRaidOmenLevel()) {
            $$11.absorbRaidOmen($$02);
        }
        this.setDirty();
        return $$11;
    }

    private Raid getOrCreateRaid(ServerLevel $$0, BlockPos $$1) {
        Raid $$2 = $$0.getRaidAt($$1);
        return $$2 != null ? $$2 : new Raid($$1, $$0.getDifficulty());
    }

    public static Raids load(CompoundTag $$0) {
        return CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$0).resultOrPartial().orElseGet(Raids::new);
    }

    private int getUniqueId() {
        return ++this.nextId;
    }

    @Nullable
    public Raid getNearbyRaid(BlockPos $$0, int $$1) {
        Raid $$2 = null;
        double $$3 = $$1;
        for (Raid $$4 : this.raidMap.values()) {
            double $$5 = $$4.getCenter().distSqr($$0);
            if (!$$4.isActive() || !($$5 < $$3)) continue;
            $$2 = $$4;
            $$3 = $$5;
        }
        return $$2;
    }

    static final class RaidWithId
    extends Record {
        final int id;
        final Raid raid;
        public static final Codec<RaidWithId> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("id").forGetter(RaidWithId::id), (App)Raid.MAP_CODEC.forGetter(RaidWithId::raid)).apply((Applicative)$$0, RaidWithId::new));

        private RaidWithId(int $$0, Raid $$1) {
            this.id = $$0;
            this.raid = $$1;
        }

        public static RaidWithId from(Int2ObjectMap.Entry<Raid> $$0) {
            return new RaidWithId($$0.getIntKey(), (Raid)$$0.getValue());
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RaidWithId.class, "id;raid", "id", "raid"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RaidWithId.class, "id;raid", "id", "raid"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RaidWithId.class, "id;raid", "id", "raid"}, this, $$0);
        }

        public int id() {
            return this.id;
        }

        public Raid raid() {
            return this.raid;
        }
    }
}

