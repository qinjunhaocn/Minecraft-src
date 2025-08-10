/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.SummaryReporter;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionReadEvent;
import net.minecraft.util.profiling.jfr.event.ChunkRegionWriteEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.StructureGenerationEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public class JfrProfiler
implements JvmProfiler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ROOT_CATEGORY = "Minecraft";
    public static final String WORLD_GEN_CATEGORY = "World Generation";
    public static final String TICK_CATEGORY = "Ticking";
    public static final String NETWORK_CATEGORY = "Network";
    public static final String STORAGE_CATEGORY = "Storage";
    private static final List<Class<? extends Event>> CUSTOM_EVENTS = List.of(ChunkGenerationEvent.class, ChunkRegionReadEvent.class, ChunkRegionWriteEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, StructureGenerationEvent.class, WorldLoadFinishedEvent.class);
    private static final String FLIGHT_RECORDER_CONFIG = "/flightrecorder-config.jfc";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
    private static final JfrProfiler INSTANCE = new JfrProfiler();
    @Nullable
    Recording recording;
    private float currentAverageTickTime;
    private final Map<String, NetworkSummaryEvent.SumAggregation> networkTrafficByAddress = new ConcurrentHashMap<String, NetworkSummaryEvent.SumAggregation>();

    private JfrProfiler() {
        CUSTOM_EVENTS.forEach(FlightRecorder::register);
        FlightRecorder.addPeriodicEvent(ServerTickTimeEvent.class, () -> new ServerTickTimeEvent(this.currentAverageTickTime).commit());
        FlightRecorder.addPeriodicEvent(NetworkSummaryEvent.class, () -> {
            Iterator<NetworkSummaryEvent.SumAggregation> $$0 = this.networkTrafficByAddress.values().iterator();
            while ($$0.hasNext()) {
                $$0.next().commitEvent();
                $$0.remove();
            }
        });
    }

    public static JfrProfiler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean start(Environment $$0) {
        boolean bl;
        URL $$1 = JfrProfiler.class.getResource(FLIGHT_RECORDER_CONFIG);
        if ($$1 == null) {
            LOGGER.warn("Could not find default flight recorder config at {}", (Object)FLIGHT_RECORDER_CONFIG);
            return false;
        }
        BufferedReader $$2 = new BufferedReader(new InputStreamReader($$1.openStream()));
        try {
            bl = this.start($$2, $$0);
        } catch (Throwable throwable) {
            try {
                try {
                    $$2.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (IOException $$3) {
                LOGGER.warn("Failed to start flight recorder using configuration at {}", (Object)$$1, (Object)$$3);
                return false;
            }
        }
        $$2.close();
        return bl;
    }

    @Override
    public Path stop() {
        if (this.recording == null) {
            throw new IllegalStateException("Not currently profiling");
        }
        this.networkTrafficByAddress.clear();
        Path $$0 = this.recording.getDestination();
        this.recording.stop();
        return $$0;
    }

    @Override
    public boolean isRunning() {
        return this.recording != null;
    }

    @Override
    public boolean isAvailable() {
        return FlightRecorder.isAvailable();
    }

    private boolean start(Reader $$0, Environment $$1) {
        if (this.isRunning()) {
            LOGGER.warn("Profiling already in progress");
            return false;
        }
        try {
            Configuration $$22 = Configuration.create($$0);
            String $$3 = DATE_TIME_FORMATTER.format(Instant.now());
            this.recording = Util.make(new Recording($$22), $$2 -> {
                CUSTOM_EVENTS.forEach($$2::enable);
                $$2.setDumpOnExit(true);
                $$2.setToDisk(true);
                $$2.setName(String.format(Locale.ROOT, "%s-%s-%s", $$1.getDescription(), SharedConstants.getCurrentVersion().name(), $$3));
            });
            Path $$4 = Paths.get(String.format(Locale.ROOT, "debug/%s-%s.jfr", $$1.getDescription(), $$3), new String[0]);
            FileUtil.createDirectoriesSafe($$4.getParent());
            this.recording.setDestination($$4);
            this.recording.start();
            this.setupSummaryListener();
        } catch (IOException | ParseException $$5) {
            LOGGER.warn("Failed to start jfr profiling", $$5);
            return false;
        }
        LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", this.recording.getId(), this.recording.getName(), this.recording.getDestination());
        return true;
    }

    private void setupSummaryListener() {
        FlightRecorder.addListener(new FlightRecorderListener(){
            final SummaryReporter summaryReporter = new SummaryReporter(() -> {
                JfrProfiler.this.recording = null;
            });

            @Override
            public void recordingStateChanged(Recording $$0) {
                if ($$0 != JfrProfiler.this.recording || $$0.getState() != RecordingState.STOPPED) {
                    return;
                }
                this.summaryReporter.recordingStopped($$0.getDestination());
                FlightRecorder.removeListener(this);
            }
        });
    }

    @Override
    public void onServerTick(float $$0) {
        if (ServerTickTimeEvent.TYPE.isEnabled()) {
            this.currentAverageTickTime = $$0;
        }
    }

    @Override
    public void onPacketReceived(ConnectionProtocol $$0, PacketType<?> $$1, SocketAddress $$2, int $$3) {
        if (PacketReceivedEvent.TYPE.isEnabled()) {
            new PacketReceivedEvent($$0.id(), $$1.flow().id(), $$1.id().toString(), $$2, $$3).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.networkStatFor($$2).trackReceivedPacket($$3);
        }
    }

    @Override
    public void onPacketSent(ConnectionProtocol $$0, PacketType<?> $$1, SocketAddress $$2, int $$3) {
        if (PacketSentEvent.TYPE.isEnabled()) {
            new PacketSentEvent($$0.id(), $$1.flow().id(), $$1.id().toString(), $$2, $$3).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.networkStatFor($$2).trackSentPacket($$3);
        }
    }

    private NetworkSummaryEvent.SumAggregation networkStatFor(SocketAddress $$0) {
        return this.networkTrafficByAddress.computeIfAbsent($$0.toString(), NetworkSummaryEvent.SumAggregation::new);
    }

    @Override
    public void onRegionFileRead(RegionStorageInfo $$0, ChunkPos $$1, RegionFileVersion $$2, int $$3) {
        if (ChunkRegionReadEvent.TYPE.isEnabled()) {
            new ChunkRegionReadEvent($$0, $$1, $$2, $$3).commit();
        }
    }

    @Override
    public void onRegionFileWrite(RegionStorageInfo $$0, ChunkPos $$1, RegionFileVersion $$2, int $$3) {
        if (ChunkRegionWriteEvent.TYPE.isEnabled()) {
            new ChunkRegionWriteEvent($$0, $$1, $$2, $$3).commit();
        }
    }

    @Override
    @Nullable
    public ProfiledDuration onWorldLoadedStarted() {
        if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
            return null;
        }
        WorldLoadFinishedEvent $$0 = new WorldLoadFinishedEvent();
        $$0.begin();
        return $$1 -> $$0.commit();
    }

    @Override
    @Nullable
    public ProfiledDuration onChunkGenerate(ChunkPos $$0, ResourceKey<Level> $$12, String $$2) {
        if (!ChunkGenerationEvent.TYPE.isEnabled()) {
            return null;
        }
        ChunkGenerationEvent $$3 = new ChunkGenerationEvent($$0, $$12, $$2);
        $$3.begin();
        return $$1 -> $$3.commit();
    }

    @Override
    @Nullable
    public ProfiledDuration onStructureGenerate(ChunkPos $$0, ResourceKey<Level> $$12, Holder<Structure> $$2) {
        if (!StructureGenerationEvent.TYPE.isEnabled()) {
            return null;
        }
        StructureGenerationEvent $$3 = new StructureGenerationEvent($$0, $$2, $$12);
        $$3.begin();
        return $$1 -> {
            $$0.success = $$1;
            $$3.commit();
        };
    }
}

