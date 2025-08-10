/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.io.FileUtils
 */
package net.minecraft.stats;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter
extends StatsCounter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<Map<Stat<?>, Integer>> STATS_CODEC = Codec.dispatchedMap(BuiltInRegistries.STAT_TYPE.byNameCodec(), Util.memoize(ServerStatsCounter::createTypedStatsCodec)).xmap($$0 -> {
        HashMap $$12 = new HashMap();
        $$0.forEach(($$1, $$2) -> $$12.putAll($$2));
        return $$12;
    }, $$02 -> $$02.entrySet().stream().collect(Collectors.groupingBy($$0 -> ((Stat)$$0.getKey()).getType(), Util.toMap())));
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> dirty = Sets.newHashSet();

    private static <T> Codec<Map<Stat<?>, Integer>> createTypedStatsCodec(StatType<T> $$0) {
        Codec<T> $$12 = $$0.getRegistry().byNameCodec();
        Codec $$2 = $$12.flatComapMap($$0::get, $$1 -> {
            if ($$1.getType() == $$0) {
                return DataResult.success($$1.getValue());
            }
            return DataResult.error(() -> "Expected type " + String.valueOf($$0) + ", but got " + String.valueOf($$1.getType()));
        });
        return Codec.unboundedMap((Codec)$$2, (Codec)Codec.INT);
    }

    public ServerStatsCounter(MinecraftServer $$0, File $$1) {
        this.server = $$0;
        this.file = $$1;
        if ($$1.isFile()) {
            try {
                this.parseLocal($$0.getFixerUpper(), FileUtils.readFileToString((File)$$1));
            } catch (IOException $$2) {
                LOGGER.error("Couldn't read statistics file {}", (Object)$$1, (Object)$$2);
            } catch (JsonParseException $$3) {
                LOGGER.error("Couldn't parse statistics file {}", (Object)$$1, (Object)$$3);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile((File)this.file, (String)this.toJson());
        } catch (IOException $$0) {
            LOGGER.error("Couldn't save stats", $$0);
        }
    }

    @Override
    public void setValue(Player $$0, Stat<?> $$1, int $$2) {
        super.setValue($$0, $$1, $$2);
        this.dirty.add($$1);
    }

    private Set<Stat<?>> getDirty() {
        HashSet<Stat<?>> $$0 = Sets.newHashSet(this.dirty);
        this.dirty.clear();
        return $$0;
    }

    public void parseLocal(DataFixer $$02, String $$1) {
        try {
            JsonElement $$2 = StrictJsonParser.parse($$1);
            if ($$2.isJsonNull()) {
                LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
                return;
            }
            Dynamic $$3 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)$$2);
            $$3 = DataFixTypes.STATS.updateToCurrentVersion($$02, $$3, NbtUtils.getDataVersion($$3, 1343));
            this.stats.putAll(STATS_CODEC.parse($$3.get("stats").orElseEmptyMap()).resultOrPartial($$0 -> LOGGER.error("Failed to parse statistics for {}: {}", (Object)this.file, $$0)).orElse(Map.of()));
        } catch (JsonParseException $$4) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.file, (Object)$$4);
        }
    }

    protected String toJson() {
        JsonObject $$0 = new JsonObject();
        $$0.add("stats", (JsonElement)STATS_CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.stats).getOrThrow());
        $$0.addProperty("DataVersion", (Number)SharedConstants.getCurrentVersion().dataVersion().version());
        return $$0.toString();
    }

    public void markAllDirty() {
        this.dirty.addAll((Collection<Stat<?>>)this.stats.keySet());
    }

    public void sendStats(ServerPlayer $$0) {
        Object2IntOpenHashMap $$1 = new Object2IntOpenHashMap();
        for (Stat<?> $$2 : this.getDirty()) {
            $$1.put($$2, this.getValue($$2));
        }
        $$0.connection.send(new ClientboundAwardStatsPacket((Object2IntMap<Stat<?>>)$$1));
    }
}

