/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryPropertyContainer
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 */
package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record TelemetryProperty<T>(String id, String exportKey, Codec<T> codec, Exporter<T> exporter) {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    public static final TelemetryProperty<String> USER_ID = TelemetryProperty.string("user_id", "userId");
    public static final TelemetryProperty<String> CLIENT_ID = TelemetryProperty.string("client_id", "clientId");
    public static final TelemetryProperty<UUID> MINECRAFT_SESSION_ID = TelemetryProperty.uuid("minecraft_session_id", "deviceSessionId");
    public static final TelemetryProperty<String> GAME_VERSION = TelemetryProperty.string("game_version", "buildDisplayName");
    public static final TelemetryProperty<String> OPERATING_SYSTEM = TelemetryProperty.string("operating_system", "buildPlatform");
    public static final TelemetryProperty<String> PLATFORM = TelemetryProperty.string("platform", "platform");
    public static final TelemetryProperty<Boolean> CLIENT_MODDED = TelemetryProperty.bool("client_modded", "clientModded");
    public static final TelemetryProperty<String> LAUNCHER_NAME = TelemetryProperty.string("launcher_name", "launcherName");
    public static final TelemetryProperty<UUID> WORLD_SESSION_ID = TelemetryProperty.uuid("world_session_id", "worldSessionId");
    public static final TelemetryProperty<Boolean> SERVER_MODDED = TelemetryProperty.bool("server_modded", "serverModded");
    public static final TelemetryProperty<ServerType> SERVER_TYPE = TelemetryProperty.create("server_type", "serverType", ServerType.CODEC, ($$0, $$1, $$2) -> $$0.addProperty($$1, $$2.getSerializedName()));
    public static final TelemetryProperty<Boolean> OPT_IN = TelemetryProperty.bool("opt_in", "isOptional");
    public static final TelemetryProperty<Instant> EVENT_TIMESTAMP_UTC = TelemetryProperty.create("event_timestamp_utc", "eventTimestampUtc", ExtraCodecs.INSTANT_ISO8601, ($$0, $$1, $$2) -> $$0.addProperty($$1, TIMESTAMP_FORMATTER.format((TemporalAccessor)$$2)));
    public static final TelemetryProperty<GameMode> GAME_MODE = TelemetryProperty.create("game_mode", "playerGameMode", GameMode.CODEC, ($$0, $$1, $$2) -> $$0.addProperty($$1, $$2.id()));
    public static final TelemetryProperty<String> REALMS_MAP_CONTENT = TelemetryProperty.string("realms_map_content", "realmsMapContent");
    public static final TelemetryProperty<Integer> SECONDS_SINCE_LOAD = TelemetryProperty.integer("seconds_since_load", "secondsSinceLoad");
    public static final TelemetryProperty<Integer> TICKS_SINCE_LOAD = TelemetryProperty.integer("ticks_since_load", "ticksSinceLoad");
    public static final TelemetryProperty<LongList> FRAME_RATE_SAMPLES = TelemetryProperty.longSamples("frame_rate_samples", "serializedFpsSamples");
    public static final TelemetryProperty<LongList> RENDER_TIME_SAMPLES = TelemetryProperty.longSamples("render_time_samples", "serializedRenderTimeSamples");
    public static final TelemetryProperty<LongList> USED_MEMORY_SAMPLES = TelemetryProperty.longSamples("used_memory_samples", "serializedUsedMemoryKbSamples");
    public static final TelemetryProperty<Integer> NUMBER_OF_SAMPLES = TelemetryProperty.integer("number_of_samples", "numSamples");
    public static final TelemetryProperty<Integer> RENDER_DISTANCE = TelemetryProperty.integer("render_distance", "renderDistance");
    public static final TelemetryProperty<Integer> DEDICATED_MEMORY_KB = TelemetryProperty.integer("dedicated_memory_kb", "dedicatedMemoryKb");
    public static final TelemetryProperty<Integer> WORLD_LOAD_TIME_MS = TelemetryProperty.integer("world_load_time_ms", "worldLoadTimeMs");
    public static final TelemetryProperty<Boolean> NEW_WORLD = TelemetryProperty.bool("new_world", "newWorld");
    public static final TelemetryProperty<GameLoadTimesEvent.Measurement> LOAD_TIME_TOTAL_TIME_MS = TelemetryProperty.gameLoadMeasurement("load_time_total_time_ms", "loadTimeTotalTimeMs");
    public static final TelemetryProperty<GameLoadTimesEvent.Measurement> LOAD_TIME_PRE_WINDOW_MS = TelemetryProperty.gameLoadMeasurement("load_time_pre_window_ms", "loadTimePreWindowMs");
    public static final TelemetryProperty<GameLoadTimesEvent.Measurement> LOAD_TIME_BOOTSTRAP_MS = TelemetryProperty.gameLoadMeasurement("load_time_bootstrap_ms", "loadTimeBootstrapMs");
    public static final TelemetryProperty<GameLoadTimesEvent.Measurement> LOAD_TIME_LOADING_OVERLAY_MS = TelemetryProperty.gameLoadMeasurement("load_time_loading_overlay_ms", "loadTimeLoadingOverlayMs");
    public static final TelemetryProperty<String> ADVANCEMENT_ID = TelemetryProperty.string("advancement_id", "advancementId");
    public static final TelemetryProperty<Long> ADVANCEMENT_GAME_TIME = TelemetryProperty.makeLong("advancement_game_time", "advancementGameTime");

    public static <T> TelemetryProperty<T> create(String $$0, String $$1, Codec<T> $$2, Exporter<T> $$3) {
        return new TelemetryProperty<T>($$0, $$1, $$2, $$3);
    }

    public static TelemetryProperty<Boolean> bool(String $$0, String $$1) {
        return TelemetryProperty.create($$0, $$1, Codec.BOOL, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryProperty<String> string(String $$0, String $$1) {
        return TelemetryProperty.create($$0, $$1, Codec.STRING, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryProperty<Integer> integer(String $$0, String $$1) {
        return TelemetryProperty.create($$0, $$1, Codec.INT, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryProperty<Long> makeLong(String $$0, String $$1) {
        return TelemetryProperty.create($$0, $$1, Codec.LONG, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryProperty<UUID> uuid(String $$02, String $$12) {
        return TelemetryProperty.create($$02, $$12, UUIDUtil.STRING_CODEC, ($$0, $$1, $$2) -> $$0.addProperty($$1, $$2.toString()));
    }

    public static TelemetryProperty<GameLoadTimesEvent.Measurement> gameLoadMeasurement(String $$02, String $$12) {
        return TelemetryProperty.create($$02, $$12, GameLoadTimesEvent.Measurement.CODEC, ($$0, $$1, $$2) -> $$0.addProperty($$1, $$2.millis()));
    }

    public static TelemetryProperty<LongList> longSamples(String $$02, String $$12) {
        return TelemetryProperty.create($$02, $$12, Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity()), ($$0, $$1, $$2) -> $$0.addProperty($$1, $$2.longStream().mapToObj(String::valueOf).collect(Collectors.joining(";"))));
    }

    public void export(TelemetryPropertyMap $$0, TelemetryPropertyContainer $$1) {
        Object $$2 = $$0.get(this);
        if ($$2 != null) {
            this.exporter.apply($$1, this.exportKey, $$2);
        } else {
            $$1.addNullProperty(this.exportKey);
        }
    }

    public MutableComponent title() {
        return Component.translatable("telemetry.property." + this.id + ".title");
    }

    public String toString() {
        return "TelemetryProperty[" + this.id + "]";
    }

    public static interface Exporter<T> {
        public void apply(TelemetryPropertyContainer var1, String var2, T var3);
    }

    public static final class GameMode
    extends Enum<GameMode>
    implements StringRepresentable {
        public static final /* enum */ GameMode SURVIVAL = new GameMode("survival", 0);
        public static final /* enum */ GameMode CREATIVE = new GameMode("creative", 1);
        public static final /* enum */ GameMode ADVENTURE = new GameMode("adventure", 2);
        public static final /* enum */ GameMode SPECTATOR = new GameMode("spectator", 6);
        public static final /* enum */ GameMode HARDCORE = new GameMode("hardcore", 99);
        public static final Codec<GameMode> CODEC;
        private final String key;
        private final int id;
        private static final /* synthetic */ GameMode[] $VALUES;

        public static GameMode[] values() {
            return (GameMode[])$VALUES.clone();
        }

        public static GameMode valueOf(String $$0) {
            return Enum.valueOf(GameMode.class, $$0);
        }

        private GameMode(String $$0, int $$1) {
            this.key = $$0;
            this.id = $$1;
        }

        public int id() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.key;
        }

        private static /* synthetic */ GameMode[] b() {
            return new GameMode[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR, HARDCORE};
        }

        static {
            $VALUES = GameMode.b();
            CODEC = StringRepresentable.fromEnum(GameMode::values);
        }
    }

    public static final class ServerType
    extends Enum<ServerType>
    implements StringRepresentable {
        public static final /* enum */ ServerType REALM = new ServerType("realm");
        public static final /* enum */ ServerType LOCAL = new ServerType("local");
        public static final /* enum */ ServerType OTHER = new ServerType("server");
        public static final Codec<ServerType> CODEC;
        private final String key;
        private static final /* synthetic */ ServerType[] $VALUES;

        public static ServerType[] values() {
            return (ServerType[])$VALUES.clone();
        }

        public static ServerType valueOf(String $$0) {
            return Enum.valueOf(ServerType.class, $$0);
        }

        private ServerType(String $$0) {
            this.key = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.key;
        }

        private static /* synthetic */ ServerType[] a() {
            return new ServerType[]{REALM, LOCAL, OTHER};
        }

        static {
            $VALUES = ServerType.a();
            CODEC = StringRepresentable.fromEnum(ServerType::values);
        }
    }
}

