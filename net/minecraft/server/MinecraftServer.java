/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.jtracy.DiscontinuousFrame
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandle;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.runtime.ObjectMethods;
import java.net.Proxy;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.Services;
import net.minecraft.server.SuppressedExceptionCollector;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldStem;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class MinecraftServer
extends ReentrantBlockableEventLoop<TickTask>
implements ServerInfo,
ChunkIOErrorReporter,
CommandSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_BRAND = "vanilla";
    private static final float AVERAGE_TICK_TIME_SMOOTHING = 0.8f;
    private static final int TICK_STATS_SPAN = 100;
    private static final long OVERLOADED_THRESHOLD_NANOS = 20L * TimeUtil.NANOSECONDS_PER_SECOND / 20L;
    private static final int OVERLOADED_TICKS_THRESHOLD = 20;
    private static final long OVERLOADED_WARNING_INTERVAL_NANOS = 10L * TimeUtil.NANOSECONDS_PER_SECOND;
    private static final int OVERLOADED_TICKS_WARNING_INTERVAL = 100;
    private static final long STATUS_EXPIRE_TIME_NANOS = 5L * TimeUtil.NANOSECONDS_PER_SECOND;
    private static final long PREPARE_LEVELS_DEFAULT_DELAY_NANOS = 10L * TimeUtil.NANOSECONDS_PER_MILLISECOND;
    private static final int MAX_STATUS_PLAYER_SAMPLE = 12;
    private static final int SPAWN_POSITION_SEARCH_RADIUS = 5;
    private static final int AUTOSAVE_INTERVAL = 6000;
    private static final int MIMINUM_AUTOSAVE_TICKS = 100;
    private static final int MAX_TICK_LATENCY = 3;
    public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
    public static final LevelSettings DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(FeatureFlags.DEFAULT_FLAGS), WorldDataConfiguration.DEFAULT);
    public static final GameProfile ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
    protected final LevelStorageSource.LevelStorageAccess storageSource;
    protected final PlayerDataStorage playerDataStorage;
    private final List<Runnable> tickables = Lists.newArrayList();
    private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    private Consumer<ProfileResults> onMetricsRecordingStopped = $$0 -> this.stopRecordingMetrics();
    private Consumer<Path> onMetricsRecordingFinished = $$0 -> {};
    private boolean willStartRecordingMetrics;
    @Nullable
    private TimeProfiler debugCommandProfiler;
    private boolean debugCommandProfilerDelayStart;
    private final ServerConnectionListener connection;
    private final ChunkProgressListenerFactory progressListenerFactory;
    @Nullable
    private ServerStatus status;
    @Nullable
    private ServerStatus.Favicon statusIcon;
    private final RandomSource random = RandomSource.create();
    private final DataFixer fixerUpper;
    private String localIp;
    private int port = -1;
    private final LayeredRegistryAccess<RegistryLayer> registries;
    private final Map<ResourceKey<Level>, ServerLevel> levels = Maps.newLinkedHashMap();
    private PlayerList playerList;
    private volatile boolean running = true;
    private boolean stopped;
    private int tickCount;
    private int ticksUntilAutosave = 6000;
    protected final Proxy proxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean pvp;
    private boolean allowFlight;
    @Nullable
    private String motd;
    private int playerIdleTimeout;
    private final long[] tickTimesNanos = new long[100];
    private long aggregatedTickTimesNanos = 0L;
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private GameProfile singleplayerProfile;
    private boolean isDemo;
    private volatile boolean isReady;
    private long lastOverloadWarningNanos;
    protected final Services services;
    private long lastServerStatus;
    private final Thread serverThread;
    private long lastTickNanos = Util.getNanos();
    private long taskExecutionStartNanos = Util.getNanos();
    private long idleTimeNanos;
    private long nextTickTimeNanos = Util.getNanos();
    private boolean waitingForNextTick = false;
    private long delayedTasksMaxNextTickTimeNanos;
    private boolean mayHaveDelayedTasks;
    private final PackRepository packRepository;
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    @Nullable
    private CommandStorage commandStorage;
    private final CustomBossEvents customBossEvents = new CustomBossEvents();
    private final ServerFunctionManager functionManager;
    private boolean enforceWhitelist;
    private float smoothedTickTimeMillis;
    private final Executor executor;
    @Nullable
    private String serverId;
    private ReloadableResources resources;
    private final StructureTemplateManager structureTemplateManager;
    private final ServerTickRateManager tickRateManager;
    protected final WorldData worldData;
    private final PotionBrewing potionBrewing;
    private FuelValues fuelValues;
    private int emptyTicks;
    private volatile boolean isSaving;
    private static final AtomicReference<RuntimeException> fatalException = new AtomicReference();
    private final SuppressedExceptionCollector suppressedExceptions = new SuppressedExceptionCollector();
    private final DiscontinuousFrame tickFrame;

    public static <S extends MinecraftServer> S spin(Function<Thread, S> $$02) {
        AtomicReference<MinecraftServer> $$12 = new AtomicReference<MinecraftServer>();
        Thread $$2 = new Thread(() -> ((MinecraftServer)$$12.get()).runServer(), "Server thread");
        $$2.setUncaughtExceptionHandler(($$0, $$1) -> LOGGER.error("Uncaught exception in server thread", $$1));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            $$2.setPriority(8);
        }
        MinecraftServer $$3 = (MinecraftServer)$$02.apply($$2);
        $$12.set($$3);
        $$2.start();
        return (S)$$3;
    }

    public MinecraftServer(Thread $$02, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, Services $$6, ChunkProgressListenerFactory $$7) {
        super("Server");
        this.registries = $$3.registries();
        this.worldData = $$3.worldData();
        if (!this.registries.compositeAccess().lookupOrThrow(Registries.LEVEL_STEM).containsKey(LevelStem.OVERWORLD)) {
            throw new IllegalStateException("Missing Overworld dimension data");
        }
        this.proxy = $$4;
        this.packRepository = $$2;
        this.resources = new ReloadableResources($$3.resourceManager(), $$3.dataPackResources());
        this.services = $$6;
        if ($$6.profileCache() != null) {
            $$6.profileCache().setExecutor(this);
        }
        this.connection = new ServerConnectionListener(this);
        this.tickRateManager = new ServerTickRateManager(this);
        this.progressListenerFactory = $$7;
        this.storageSource = $$1;
        this.playerDataStorage = $$1.createPlayerStorage();
        this.fixerUpper = $$5;
        this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
        HolderLookup.RegistryLookup $$8 = this.registries.compositeAccess().lookupOrThrow(Registries.BLOCK).filterFeatures(this.worldData.enabledFeatures());
        this.structureTemplateManager = new StructureTemplateManager($$3.resourceManager(), $$1, $$5, $$8);
        this.serverThread = $$02;
        this.executor = Util.backgroundExecutor();
        this.potionBrewing = PotionBrewing.bootstrap(this.worldData.enabledFeatures());
        this.resources.managers.getRecipeManager().finalizeRecipeLoading(this.worldData.enabledFeatures());
        this.fuelValues = FuelValues.vanillaBurnTimes(this.registries.compositeAccess(), this.worldData.enabledFeatures());
        this.tickFrame = TracyClient.createDiscontinuousFrame((String)"Server Tick");
    }

    private void readScoreboard(DimensionDataStorage $$0) {
        $$0.computeIfAbsent(ServerScoreboard.TYPE);
    }

    protected abstract boolean initServer() throws IOException;

    protected void loadLevel() {
        if (!JvmProfiler.INSTANCE.isRunning()) {
            // empty if block
        }
        boolean $$0 = false;
        ProfiledDuration $$1 = JvmProfiler.INSTANCE.onWorldLoadedStarted();
        this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
        ChunkProgressListener $$2 = this.progressListenerFactory.create(this.worldData.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS));
        this.createLevels($$2);
        this.forceDifficulty();
        this.prepareLevels($$2);
        if ($$1 != null) {
            $$1.finish(true);
        }
        if ($$0) {
            try {
                JvmProfiler.INSTANCE.stop();
            } catch (Throwable $$3) {
                LOGGER.warn("Failed to stop JFR profiling", $$3);
            }
        }
    }

    protected void forceDifficulty() {
    }

    protected void createLevels(ChunkProgressListener $$0) {
        ServerLevelData $$1 = this.worldData.overworldData();
        boolean $$2 = this.worldData.isDebugWorld();
        HolderLookup.RegistryLookup $$3 = this.registries.compositeAccess().lookupOrThrow(Registries.LEVEL_STEM);
        WorldOptions $$4 = this.worldData.worldGenOptions();
        long $$5 = $$4.seed();
        long $$6 = BiomeManager.obfuscateSeed($$5);
        ImmutableList<CustomSpawner> $$7 = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner($$1));
        LevelStem $$8 = $$3.getValue(LevelStem.OVERWORLD);
        ServerLevel $$9 = new ServerLevel(this, this.executor, this.storageSource, $$1, Level.OVERWORLD, $$8, $$0, $$2, $$6, $$7, true, null);
        this.levels.put(Level.OVERWORLD, $$9);
        DimensionDataStorage $$10 = $$9.getDataStorage();
        this.readScoreboard($$10);
        this.commandStorage = new CommandStorage($$10);
        WorldBorder $$11 = $$9.getWorldBorder();
        if (!$$1.isInitialized()) {
            try {
                MinecraftServer.setInitialSpawn($$9, $$1, $$4.generateBonusChest(), $$2);
                $$1.setInitialized(true);
                if ($$2) {
                    this.setupDebugLevel(this.worldData);
                }
            } catch (Throwable $$12) {
                CrashReport $$13 = CrashReport.forThrowable($$12, "Exception initializing level");
                try {
                    $$9.fillReportDetails($$13);
                } catch (Throwable throwable) {
                    // empty catch block
                }
                throw new ReportedException($$13);
            }
            $$1.setInitialized(true);
        }
        this.getPlayerList().addWorldborderListener($$9);
        if (this.worldData.getCustomBossEvents() != null) {
            this.getCustomBossEvents().load(this.worldData.getCustomBossEvents(), this.registryAccess());
        }
        RandomSequences $$14 = $$9.getRandomSequences();
        for (Map.Entry $$15 : $$3.entrySet()) {
            ResourceKey $$16 = $$15.getKey();
            if ($$16 == LevelStem.OVERWORLD) continue;
            ResourceKey<Level> $$17 = ResourceKey.create(Registries.DIMENSION, $$16.location());
            DerivedLevelData $$18 = new DerivedLevelData(this.worldData, $$1);
            ServerLevel $$19 = new ServerLevel(this, this.executor, this.storageSource, $$18, $$17, (LevelStem)((Object)$$15.getValue()), $$0, $$2, $$6, ImmutableList.of(), false, $$14);
            $$11.addListener(new BorderChangeListener.DelegateBorderChangeListener($$19.getWorldBorder()));
            this.levels.put($$17, $$19);
        }
        $$11.applySettings($$1.getWorldBorder());
    }

    private static void setInitialSpawn(ServerLevel $$02, ServerLevelData $$1, boolean $$2, boolean $$32) {
        if ($$32) {
            $$1.setSpawn(BlockPos.ZERO.above(80), 0.0f);
            return;
        }
        ServerChunkCache $$4 = $$02.getChunkSource();
        ChunkPos $$5 = new ChunkPos($$4.randomState().sampler().findSpawnPosition());
        int $$6 = $$4.getGenerator().getSpawnHeight($$02);
        if ($$6 < $$02.getMinY()) {
            BlockPos $$7 = $$5.getWorldPosition();
            $$6 = $$02.getHeight(Heightmap.Types.WORLD_SURFACE, $$7.getX() + 8, $$7.getZ() + 8);
        }
        $$1.setSpawn($$5.getWorldPosition().offset(8, $$6, 8), 0.0f);
        int $$8 = 0;
        int $$9 = 0;
        int $$10 = 0;
        int $$11 = -1;
        for (int $$12 = 0; $$12 < Mth.square(11); ++$$12) {
            BlockPos $$13;
            if ($$8 >= -5 && $$8 <= 5 && $$9 >= -5 && $$9 <= 5 && ($$13 = PlayerRespawnLogic.getSpawnPosInChunk($$02, new ChunkPos($$5.x + $$8, $$5.z + $$9))) != null) {
                $$1.setSpawn($$13, 0.0f);
                break;
            }
            if ($$8 == $$9 || $$8 < 0 && $$8 == -$$9 || $$8 > 0 && $$8 == 1 - $$9) {
                int $$14 = $$10;
                $$10 = -$$11;
                $$11 = $$14;
            }
            $$8 += $$10;
            $$9 += $$11;
        }
        if ($$2) {
            $$02.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.get(MiscOverworldFeatures.BONUS_CHEST)).ifPresent($$3 -> ((ConfiguredFeature)((Object)((Object)$$3.value()))).place($$02, $$4.getGenerator(), $$0.random, $$1.getSpawnPos()));
        }
    }

    private void setupDebugLevel(WorldData $$0) {
        $$0.setDifficulty(Difficulty.PEACEFUL);
        $$0.setDifficultyLocked(true);
        ServerLevelData $$1 = $$0.overworldData();
        $$1.setRaining(false);
        $$1.setThundering(false);
        $$1.setClearWeatherTime(1000000000);
        $$1.setDayTime(6000L);
        $$1.setGameType(GameType.SPECTATOR);
    }

    private void prepareLevels(ChunkProgressListener $$0) {
        int $$5;
        ServerLevel $$1 = this.overworld();
        LOGGER.info("Preparing start region for dimension {}", (Object)$$1.dimension().location());
        BlockPos $$2 = $$1.getSharedSpawnPos();
        $$0.updateSpawnPos(new ChunkPos($$2));
        ServerChunkCache $$3 = $$1.getChunkSource();
        this.nextTickTimeNanos = Util.getNanos();
        $$1.setDefaultSpawnPos($$2, $$1.getSharedSpawnAngle());
        int $$4 = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
        int n = $$5 = $$4 > 0 ? Mth.square(ChunkProgressListener.calculateDiameter($$4)) : 0;
        while ($$3.getTickingGenerated() < $$5) {
            this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
            this.waitUntilNextTick();
        }
        this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
        this.waitUntilNextTick();
        for (ServerLevel $$6 : this.levels.values()) {
            TicketStorage $$7 = $$6.getDataStorage().get(TicketStorage.TYPE);
            if ($$7 == null) continue;
            $$7.activateAllDeactivatedTickets();
        }
        this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
        this.waitUntilNextTick();
        $$0.stop();
        this.updateMobSpawningFlags();
    }

    public GameType getDefaultGameType() {
        return this.worldData.getGameType();
    }

    public boolean isHardcore() {
        return this.worldData.isHardcore();
    }

    public abstract int getOperatorUserPermissionLevel();

    public abstract int getFunctionCompilationLevel();

    public abstract boolean shouldRconBroadcast();

    public boolean saveAllChunks(boolean $$0, boolean $$1, boolean $$2) {
        boolean $$3 = false;
        for (ServerLevel $$4 : this.getAllLevels()) {
            if (!$$0) {
                LOGGER.info("Saving chunks for level '{}'/{}", (Object)$$4, (Object)$$4.dimension().location());
            }
            $$4.save(null, $$1, $$4.noSave && !$$2);
            $$3 = true;
        }
        ServerLevel $$5 = this.overworld();
        ServerLevelData $$6 = this.worldData.overworldData();
        $$6.setWorldBorder($$5.getWorldBorder().createSettings());
        this.worldData.setCustomBossEvents(this.getCustomBossEvents().save(this.registryAccess()));
        this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.getPlayerList().getSingleplayerData());
        if ($$1) {
            for (ServerLevel $$7 : this.getAllLevels()) {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)$$7.getChunkSource().chunkMap.getStorageName());
            }
            LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
        }
        return $$3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveEverything(boolean $$0, boolean $$1, boolean $$2) {
        try {
            this.isSaving = true;
            this.getPlayerList().saveAll();
            boolean bl = this.saveAllChunks($$0, $$1, $$2);
            return bl;
        } finally {
            this.isSaving = false;
        }
    }

    @Override
    public void close() {
        this.stopServer();
    }

    public void stopServer() {
        if (this.metricsRecorder.isRecording()) {
            this.cancelRecordingMetrics();
        }
        LOGGER.info("Stopping server");
        this.getConnection().stop();
        this.isSaving = true;
        if (this.playerList != null) {
            LOGGER.info("Saving players");
            this.playerList.saveAll();
            this.playerList.removeAll();
        }
        LOGGER.info("Saving worlds");
        for (ServerLevel $$02 : this.getAllLevels()) {
            if ($$02 == null) continue;
            $$02.noSave = false;
        }
        while (this.levels.values().stream().anyMatch($$0 -> $$0.getChunkSource().chunkMap.hasWork())) {
            this.nextTickTimeNanos = Util.getNanos() + TimeUtil.NANOSECONDS_PER_MILLISECOND;
            for (ServerLevel $$1 : this.getAllLevels()) {
                $$1.getChunkSource().deactivateTicketsOnClosing();
                $$1.getChunkSource().tick(() -> true, false);
            }
            this.waitUntilNextTick();
        }
        this.saveAllChunks(false, true, false);
        for (ServerLevel $$2 : this.getAllLevels()) {
            if ($$2 == null) continue;
            try {
                $$2.close();
            } catch (IOException $$3) {
                LOGGER.error("Exception closing the level", $$3);
            }
        }
        this.isSaving = false;
        this.resources.close();
        try {
            this.storageSource.close();
        } catch (IOException $$4) {
            LOGGER.error("Failed to unlock level {}", (Object)this.storageSource.getLevelId(), (Object)$$4);
        }
    }

    public String getLocalIp() {
        return this.localIp;
    }

    public void setLocalIp(String $$0) {
        this.localIp = $$0;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void halt(boolean $$0) {
        this.running = false;
        if ($$0) {
            try {
                this.serverThread.join();
            } catch (InterruptedException $$1) {
                LOGGER.error("Error while shutting down", $$1);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void runServer() {
        try {
            if (!this.initServer()) throw new IllegalStateException("Failed to initialize server");
            this.nextTickTimeNanos = Util.getNanos();
            this.statusIcon = this.loadStatusIcon().orElse(null);
            this.status = this.buildServerStatus();
            while (this.running) {
                boolean $$4;
                long $$1;
                if (!this.isPaused() && this.tickRateManager.isSprinting() && this.tickRateManager.checkShouldSprintThisTick()) {
                    long $$0 = 0L;
                    this.lastOverloadWarningNanos = this.nextTickTimeNanos = Util.getNanos();
                } else {
                    $$1 = this.tickRateManager.nanosecondsPerTick();
                    long $$2 = Util.getNanos() - this.nextTickTimeNanos;
                    if ($$2 > OVERLOADED_THRESHOLD_NANOS + 20L * $$1 && this.nextTickTimeNanos - this.lastOverloadWarningNanos >= OVERLOADED_WARNING_INTERVAL_NANOS + 100L * $$1) {
                        long $$3 = $$2 / $$1;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", (Object)($$2 / TimeUtil.NANOSECONDS_PER_MILLISECOND), (Object)$$3);
                        this.nextTickTimeNanos += $$3 * $$1;
                        this.lastOverloadWarningNanos = this.nextTickTimeNanos;
                    }
                }
                boolean bl = $$4 = $$1 == 0L;
                if (this.debugCommandProfilerDelayStart) {
                    this.debugCommandProfilerDelayStart = false;
                    this.debugCommandProfiler = new TimeProfiler(Util.getNanos(), this.tickCount);
                }
                this.nextTickTimeNanos += $$1;
                try (Profiler.Scope $$5 = Profiler.use(this.createProfiler());){
                    ProfilerFiller $$6 = Profiler.get();
                    $$6.push("tick");
                    this.tickFrame.start();
                    this.tickServer($$4 ? () -> false : this::haveTime);
                    this.tickFrame.end();
                    $$6.popPush("nextTickWait");
                    this.mayHaveDelayedTasks = true;
                    this.delayedTasksMaxNextTickTimeNanos = Math.max(Util.getNanos() + $$1, this.nextTickTimeNanos);
                    this.startMeasuringTaskExecutionTime();
                    this.waitUntilNextTick();
                    this.finishMeasuringTaskExecutionTime();
                    if ($$4) {
                        this.tickRateManager.endTickWork();
                    }
                    $$6.pop();
                    this.logFullTickTime();
                } finally {
                    this.endMetricsRecordingTick();
                }
                this.isReady = true;
                JvmProfiler.INSTANCE.onServerTick(this.smoothedTickTimeMillis);
            }
            return;
        } catch (Throwable $$8) {
            LOGGER.error("Encountered an unexpected exception", $$8);
            CrashReport $$9 = MinecraftServer.constructOrExtractCrashReport($$8);
            this.fillSystemReport($$9.getSystemReport());
            Path $$10 = this.getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
            if ($$9.saveToFile($$10, ReportType.CRASH)) {
                LOGGER.error("This crash report has been saved to: {}", (Object)$$10.toAbsolutePath());
            } else {
                LOGGER.error("We were unable to save this crash report to disk.");
            }
            this.onServerCrash($$9);
            return;
        } finally {
            try {
                this.stopped = true;
                this.stopServer();
            } catch (Throwable $$7) {
                LOGGER.error("Exception stopping the server", $$7);
            } finally {
                if (this.services.profileCache() != null) {
                    this.services.profileCache().clearExecutor();
                }
                this.onServerExit();
            }
        }
    }

    private void logFullTickTime() {
        long $$0 = Util.getNanos();
        if (this.isTickTimeLoggingEnabled()) {
            this.getTickTimeLogger().logSample($$0 - this.lastTickNanos);
        }
        this.lastTickNanos = $$0;
    }

    private void startMeasuringTaskExecutionTime() {
        if (this.isTickTimeLoggingEnabled()) {
            this.taskExecutionStartNanos = Util.getNanos();
            this.idleTimeNanos = 0L;
        }
    }

    private void finishMeasuringTaskExecutionTime() {
        if (this.isTickTimeLoggingEnabled()) {
            SampleLogger $$0 = this.getTickTimeLogger();
            $$0.logPartialSample(Util.getNanos() - this.taskExecutionStartNanos - this.idleTimeNanos, TpsDebugDimensions.SCHEDULED_TASKS.ordinal());
            $$0.logPartialSample(this.idleTimeNanos, TpsDebugDimensions.IDLE.ordinal());
        }
    }

    private static CrashReport constructOrExtractCrashReport(Throwable $$0) {
        CrashReport $$5;
        ReportedException $$1 = null;
        for (Throwable $$2 = $$0; $$2 != null; $$2 = $$2.getCause()) {
            ReportedException $$3;
            if (!($$2 instanceof ReportedException)) continue;
            $$1 = $$3 = (ReportedException)$$2;
        }
        if ($$1 != null) {
            CrashReport $$4 = $$1.getReport();
            if ($$1 != $$0) {
                $$4.addCategory("Wrapped in").setDetailError("Wrapping exception", $$0);
            }
        } else {
            $$5 = new CrashReport("Exception in server tick loop", $$0);
        }
        return $$5;
    }

    private boolean haveTime() {
        return this.runningTask() || Util.getNanos() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTimeNanos : this.nextTickTimeNanos);
    }

    public static boolean throwIfFatalException() {
        RuntimeException $$0 = fatalException.get();
        if ($$0 != null) {
            throw $$0;
        }
        return true;
    }

    public static void setFatalException(RuntimeException $$0) {
        fatalException.compareAndSet(null, $$0);
    }

    @Override
    public void managedBlock(BooleanSupplier $$0) {
        super.managedBlock(() -> MinecraftServer.throwIfFatalException() && $$0.getAsBoolean());
    }

    protected void waitUntilNextTick() {
        this.runAllTasks();
        this.waitingForNextTick = true;
        try {
            this.managedBlock(() -> !this.haveTime());
        } finally {
            this.waitingForNextTick = false;
        }
    }

    @Override
    public void waitForTasks() {
        boolean $$0 = this.isTickTimeLoggingEnabled();
        long $$1 = $$0 ? Util.getNanos() : 0L;
        long $$2 = this.waitingForNextTick ? this.nextTickTimeNanos - Util.getNanos() : 100000L;
        LockSupport.parkNanos("waiting for tasks", $$2);
        if ($$0) {
            this.idleTimeNanos += Util.getNanos() - $$1;
        }
    }

    @Override
    public TickTask wrapRunnable(Runnable $$0) {
        return new TickTask(this.tickCount, $$0);
    }

    @Override
    protected boolean shouldRun(TickTask $$0) {
        return $$0.getTick() + 3 < this.tickCount || this.haveTime();
    }

    @Override
    public boolean pollTask() {
        boolean $$0;
        this.mayHaveDelayedTasks = $$0 = this.pollTaskInternal();
        return $$0;
    }

    private boolean pollTaskInternal() {
        if (super.pollTask()) {
            return true;
        }
        if (this.tickRateManager.isSprinting() || this.haveTime()) {
            for (ServerLevel $$0 : this.getAllLevels()) {
                if (!$$0.getChunkSource().pollTask()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doRunTask(TickTask $$0) {
        Profiler.get().incrementCounter("runTask");
        super.doRunTask($$0);
    }

    private Optional<ServerStatus.Favicon> loadStatusIcon() {
        Optional $$02 = Optional.of(this.getFile("server-icon.png")).filter($$0 -> Files.isRegularFile($$0, new LinkOption[0])).or(() -> this.storageSource.getIconFile().filter($$0 -> Files.isRegularFile($$0, new LinkOption[0])));
        return $$02.flatMap($$0 -> {
            try {
                BufferedImage $$1 = ImageIO.read($$0.toFile());
                Preconditions.checkState($$1.getWidth() == 64, "Must be 64 pixels wide");
                Preconditions.checkState($$1.getHeight() == 64, "Must be 64 pixels high");
                ByteArrayOutputStream $$2 = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage)$$1, "PNG", $$2);
                return Optional.of(new ServerStatus.Favicon($$2.toByteArray()));
            } catch (Exception $$3) {
                LOGGER.error("Couldn't load server icon", $$3);
                return Optional.empty();
            }
        });
    }

    public Optional<Path> getWorldScreenshotFile() {
        return this.storageSource.getIconFile();
    }

    public Path getServerDirectory() {
        return Path.of((String)"", (String[])new String[0]);
    }

    public void onServerCrash(CrashReport $$0) {
    }

    public void onServerExit() {
    }

    public boolean isPaused() {
        return false;
    }

    public void tickServer(BooleanSupplier $$0) {
        long $$1 = Util.getNanos();
        int $$2 = this.pauseWhileEmptySeconds() * 20;
        if ($$2 > 0) {
            this.emptyTicks = this.playerList.getPlayerCount() == 0 && !this.tickRateManager.isSprinting() ? ++this.emptyTicks : 0;
            if (this.emptyTicks >= $$2) {
                if (this.emptyTicks == $$2) {
                    LOGGER.info("Server empty for {} seconds, pausing", (Object)this.pauseWhileEmptySeconds());
                    this.autoSave();
                }
                this.tickConnection();
                return;
            }
        }
        ++this.tickCount;
        this.tickRateManager.tick();
        this.tickChildren($$0);
        if ($$1 - this.lastServerStatus >= STATUS_EXPIRE_TIME_NANOS) {
            this.lastServerStatus = $$1;
            this.status = this.buildServerStatus();
        }
        --this.ticksUntilAutosave;
        if (this.ticksUntilAutosave <= 0) {
            this.autoSave();
        }
        ProfilerFiller $$3 = Profiler.get();
        $$3.push("tallying");
        long $$4 = Util.getNanos() - $$1;
        int $$5 = this.tickCount % 100;
        this.aggregatedTickTimesNanos -= this.tickTimesNanos[$$5];
        this.aggregatedTickTimesNanos += $$4;
        this.tickTimesNanos[$$5] = $$4;
        this.smoothedTickTimeMillis = this.smoothedTickTimeMillis * 0.8f + (float)$$4 / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND * 0.19999999f;
        this.logTickMethodTime($$1);
        $$3.pop();
    }

    private void autoSave() {
        this.ticksUntilAutosave = this.computeNextAutosaveInterval();
        LOGGER.debug("Autosave started");
        ProfilerFiller $$0 = Profiler.get();
        $$0.push("save");
        this.saveEverything(true, false, false);
        $$0.pop();
        LOGGER.debug("Autosave finished");
    }

    private void logTickMethodTime(long $$0) {
        if (this.isTickTimeLoggingEnabled()) {
            this.getTickTimeLogger().logPartialSample(Util.getNanos() - $$0, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
        }
    }

    private int computeNextAutosaveInterval() {
        float $$2;
        if (this.tickRateManager.isSprinting()) {
            long $$0 = this.getAverageTickTimeNanos() + 1L;
            float $$1 = (float)TimeUtil.NANOSECONDS_PER_SECOND / (float)$$0;
        } else {
            $$2 = this.tickRateManager.tickrate();
        }
        int $$3 = 300;
        return Math.max(100, (int)($$2 * 300.0f));
    }

    public void onTickRateChanged() {
        int $$0 = this.computeNextAutosaveInterval();
        if ($$0 < this.ticksUntilAutosave) {
            this.ticksUntilAutosave = $$0;
        }
    }

    protected abstract SampleLogger getTickTimeLogger();

    public abstract boolean isTickTimeLoggingEnabled();

    private ServerStatus buildServerStatus() {
        ServerStatus.Players $$0 = this.buildPlayerStatus();
        return new ServerStatus(Component.nullToEmpty(this.motd), Optional.of($$0), Optional.of(ServerStatus.Version.current()), Optional.ofNullable(this.statusIcon), this.enforceSecureProfile());
    }

    private ServerStatus.Players buildPlayerStatus() {
        List<ServerPlayer> $$0 = this.playerList.getPlayers();
        int $$1 = this.getMaxPlayers();
        if (this.hidesOnlinePlayers()) {
            return new ServerStatus.Players($$1, $$0.size(), List.of());
        }
        int $$2 = Math.min($$0.size(), 12);
        ObjectArrayList $$3 = new ObjectArrayList($$2);
        int $$4 = Mth.nextInt(this.random, 0, $$0.size() - $$2);
        for (int $$5 = 0; $$5 < $$2; ++$$5) {
            ServerPlayer $$6 = $$0.get($$4 + $$5);
            $$3.add((Object)($$6.allowsListing() ? $$6.getGameProfile() : ANONYMOUS_PLAYER_PROFILE));
        }
        Util.shuffle($$3, this.random);
        return new ServerStatus.Players($$1, $$0.size(), (List<GameProfile>)$$3);
    }

    protected void tickChildren(BooleanSupplier $$02) {
        ProfilerFiller $$1 = Profiler.get();
        this.getPlayerList().getPlayers().forEach($$0 -> $$0.connection.suspendFlushing());
        $$1.push("commandFunctions");
        this.getFunctions().tick();
        $$1.popPush("levels");
        for (ServerLevel $$2 : this.getAllLevels()) {
            $$1.push(() -> String.valueOf($$2) + " " + String.valueOf($$2.dimension().location()));
            if (this.tickCount % 20 == 0) {
                $$1.push("timeSync");
                this.synchronizeTime($$2);
                $$1.pop();
            }
            $$1.push("tick");
            try {
                $$2.tick($$02);
            } catch (Throwable $$3) {
                CrashReport $$4 = CrashReport.forThrowable($$3, "Exception ticking world");
                $$2.fillReportDetails($$4);
                throw new ReportedException($$4);
            }
            $$1.pop();
            $$1.pop();
        }
        $$1.popPush("connection");
        this.tickConnection();
        $$1.popPush("players");
        this.playerList.tick();
        if (this.tickRateManager.runsNormally()) {
            GameTestTicker.SINGLETON.tick();
        }
        $$1.popPush("server gui refresh");
        for (int $$5 = 0; $$5 < this.tickables.size(); ++$$5) {
            this.tickables.get($$5).run();
        }
        $$1.popPush("send chunks");
        for (ServerPlayer $$6 : this.playerList.getPlayers()) {
            $$6.connection.chunkSender.sendNextChunks($$6);
            $$6.connection.resumeFlushing();
        }
        $$1.pop();
    }

    public void tickConnection() {
        this.getConnection().tick();
    }

    private void synchronizeTime(ServerLevel $$0) {
        this.playerList.broadcastAll(new ClientboundSetTimePacket($$0.getGameTime(), $$0.getDayTime(), $$0.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), $$0.dimension());
    }

    public void forceTimeSynchronization() {
        ProfilerFiller $$0 = Profiler.get();
        $$0.push("timeSync");
        for (ServerLevel $$1 : this.getAllLevels()) {
            this.synchronizeTime($$1);
        }
        $$0.pop();
    }

    public boolean isLevelEnabled(Level $$0) {
        return true;
    }

    public void addTickable(Runnable $$0) {
        this.tickables.add($$0);
    }

    protected void setId(String $$0) {
        this.serverId = $$0;
    }

    public boolean isShutdown() {
        return !this.serverThread.isAlive();
    }

    public Path getFile(String $$0) {
        return this.getServerDirectory().resolve($$0);
    }

    public final ServerLevel overworld() {
        return this.levels.get(Level.OVERWORLD);
    }

    @Nullable
    public ServerLevel getLevel(ResourceKey<Level> $$0) {
        return this.levels.get($$0);
    }

    public Set<ResourceKey<Level>> levelKeys() {
        return this.levels.keySet();
    }

    public Iterable<ServerLevel> getAllLevels() {
        return this.levels.values();
    }

    @Override
    public String getServerVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    @Override
    public int getPlayerCount() {
        return this.playerList.getPlayerCount();
    }

    @Override
    public int getMaxPlayers() {
        return this.playerList.getMaxPlayers();
    }

    public String[] P() {
        return this.playerList.e();
    }

    @DontObfuscate
    public String getServerModName() {
        return VANILLA_BRAND;
    }

    public SystemReport fillSystemReport(SystemReport $$0) {
        $$0.setDetail("Server Running", () -> Boolean.toString(this.running));
        if (this.playerList != null) {
            $$0.setDetail("Player Count", () -> this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + String.valueOf(this.playerList.getPlayers()));
        }
        $$0.setDetail("Active Data Packs", () -> PackRepository.displayPackList(this.packRepository.getSelectedPacks()));
        $$0.setDetail("Available Data Packs", () -> PackRepository.displayPackList(this.packRepository.getAvailablePacks()));
        $$0.setDetail("Enabled Feature Flags", () -> FeatureFlags.REGISTRY.toNames(this.worldData.enabledFeatures()).stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
        $$0.setDetail("World Generation", () -> this.worldData.worldGenSettingsLifecycle().toString());
        $$0.setDetail("World Seed", () -> String.valueOf(this.worldData.worldGenOptions().seed()));
        $$0.setDetail("Suppressed Exceptions", this.suppressedExceptions::dump);
        if (this.serverId != null) {
            $$0.setDetail("Server Id", () -> this.serverId);
        }
        return this.fillServerSystemReport($$0);
    }

    public abstract SystemReport fillServerSystemReport(SystemReport var1);

    public ModCheck getModdedStatus() {
        return ModCheck.identify(VANILLA_BRAND, this::getServerModName, "Server", MinecraftServer.class);
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        LOGGER.info($$0.getString());
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int $$0) {
        this.port = $$0;
    }

    @Nullable
    public GameProfile getSingleplayerProfile() {
        return this.singleplayerProfile;
    }

    public void setSingleplayerProfile(@Nullable GameProfile $$0) {
        this.singleplayerProfile = $$0;
    }

    public boolean isSingleplayer() {
        return this.singleplayerProfile != null;
    }

    protected void initializeKeyPair() {
        LOGGER.info("Generating keypair");
        try {
            this.keyPair = Crypt.generateKeyPair();
        } catch (CryptException $$0) {
            throw new IllegalStateException("Failed to generate key pair", $$0);
        }
    }

    public void setDifficulty(Difficulty $$0, boolean $$1) {
        if (!$$1 && this.worldData.isDifficultyLocked()) {
            return;
        }
        this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : $$0);
        this.updateMobSpawningFlags();
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }

    public int getScaledTrackingDistance(int $$0) {
        return $$0;
    }

    private void updateMobSpawningFlags() {
        for (ServerLevel $$0 : this.getAllLevels()) {
            $$0.setSpawnSettings(this.isSpawningMonsters());
        }
    }

    public void setDifficultyLocked(boolean $$0) {
        this.worldData.setDifficultyLocked($$0);
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }

    private void sendDifficultyUpdate(ServerPlayer $$0) {
        LevelData $$1 = $$0.level().getLevelData();
        $$0.connection.send(new ClientboundChangeDifficultyPacket($$1.getDifficulty(), $$1.isDifficultyLocked()));
    }

    public boolean isSpawningMonsters() {
        return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
    }

    public boolean isDemo() {
        return this.isDemo;
    }

    public void setDemo(boolean $$0) {
        this.isDemo = $$0;
    }

    public Optional<ServerResourcePackInfo> getServerResourcePack() {
        return Optional.empty();
    }

    public boolean isResourcePackRequired() {
        return this.getServerResourcePack().filter(ServerResourcePackInfo::isRequired).isPresent();
    }

    public abstract boolean isDedicatedServer();

    public abstract int getRateLimitPacketsPerSecond();

    public boolean usesAuthentication() {
        return this.onlineMode;
    }

    public void setUsesAuthentication(boolean $$0) {
        this.onlineMode = $$0;
    }

    public boolean getPreventProxyConnections() {
        return this.preventProxyConnections;
    }

    public void setPreventProxyConnections(boolean $$0) {
        this.preventProxyConnections = $$0;
    }

    public abstract boolean isEpollEnabled();

    public boolean isPvpAllowed() {
        return this.pvp;
    }

    public void setPvpAllowed(boolean $$0) {
        this.pvp = $$0;
    }

    public boolean isFlightAllowed() {
        return this.allowFlight;
    }

    public void setFlightAllowed(boolean $$0) {
        this.allowFlight = $$0;
    }

    public abstract boolean isCommandBlockEnabled();

    @Override
    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String $$0) {
        this.motd = $$0;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    public void setPlayerList(PlayerList $$0) {
        this.playerList = $$0;
    }

    public abstract boolean isPublished();

    public void setDefaultGameType(GameType $$0) {
        this.worldData.setGameType($$0);
    }

    public ServerConnectionListener getConnection() {
        return this.connection;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public boolean hasGui() {
        return false;
    }

    public boolean publishServer(@Nullable GameType $$0, boolean $$1, int $$2) {
        return false;
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public int getSpawnProtectionRadius() {
        return 16;
    }

    public boolean isUnderSpawnProtection(ServerLevel $$0, BlockPos $$1, Player $$2) {
        return false;
    }

    public boolean repliesToStatus() {
        return true;
    }

    public boolean hidesOnlinePlayers() {
        return false;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }

    public void setPlayerIdleTimeout(int $$0) {
        this.playerIdleTimeout = $$0;
    }

    public MinecraftSessionService getSessionService() {
        return this.services.sessionService();
    }

    @Nullable
    public SignatureValidator getProfileKeySignatureValidator() {
        return this.services.profileKeySignatureValidator();
    }

    public GameProfileRepository getProfileRepository() {
        return this.services.profileRepository();
    }

    @Nullable
    public GameProfileCache getProfileCache() {
        return this.services.profileCache();
    }

    @Nullable
    public ServerStatus getStatus() {
        return this.status;
    }

    public void invalidateStatus() {
        this.lastServerStatus = 0L;
    }

    public int getAbsoluteMaxWorldSize() {
        return 29999984;
    }

    @Override
    public boolean scheduleExecutables() {
        return super.scheduleExecutables() && !this.isStopped();
    }

    @Override
    public void executeIfPossible(Runnable $$0) {
        if (this.isStopped()) {
            throw new RejectedExecutionException("Server already shutting down");
        }
        super.executeIfPossible($$0);
    }

    @Override
    public Thread getRunningThread() {
        return this.serverThread;
    }

    public int getCompressionThreshold() {
        return 256;
    }

    public boolean enforceSecureProfile() {
        return false;
    }

    public long getNextTickTime() {
        return this.nextTickTimeNanos;
    }

    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }

    public int getSpawnRadius(@Nullable ServerLevel $$0) {
        if ($$0 != null) {
            return $$0.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS);
        }
        return 10;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.resources.managers.getAdvancements();
    }

    public ServerFunctionManager getFunctions() {
        return this.functionManager;
    }

    public CompletableFuture<Void> reloadResources(Collection<String> $$02) {
        CompletionStage $$12 = ((CompletableFuture)CompletableFuture.supplyAsync(() -> $$02.stream().map(this.packRepository::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList()), this).thenCompose($$0 -> {
            MultiPackResourceManager $$12 = new MultiPackResourceManager(PackType.SERVER_DATA, (List<PackResources>)$$0);
            List<Registry.PendingTags<?>> $$22 = TagLoader.loadTagsForExistingRegistries($$12, this.registries.compositeAccess());
            return ((CompletableFuture)ReloadableServerResources.loadResources($$12, this.registries, $$22, this.worldData.enabledFeatures(), this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this).whenComplete(($$1, $$2) -> {
                if ($$2 != null) {
                    $$12.close();
                }
            })).thenApply($$1 -> new ReloadableResources($$12, (ReloadableServerResources)$$1));
        })).thenAcceptAsync($$1 -> {
            this.resources.close();
            this.resources = $$1;
            this.packRepository.setSelected($$02);
            WorldDataConfiguration $$2 = new WorldDataConfiguration(MinecraftServer.getSelectedPacks(this.packRepository, true), this.worldData.enabledFeatures());
            this.worldData.setDataConfiguration($$2);
            this.resources.managers.updateStaticRegistryTags();
            this.resources.managers.getRecipeManager().finalizeRecipeLoading(this.worldData.enabledFeatures());
            this.getPlayerList().saveAll();
            this.getPlayerList().reloadResources();
            this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
            this.structureTemplateManager.onResourceManagerReload(this.resources.resourceManager);
            this.fuelValues = FuelValues.vanillaBurnTimes(this.registries.compositeAccess(), this.worldData.enabledFeatures());
        }, (Executor)this);
        if (this.isSameThread()) {
            this.managedBlock(((CompletableFuture)$$12)::isDone);
        }
        return $$12;
    }

    public static WorldDataConfiguration configurePackRepository(PackRepository $$0, WorldDataConfiguration $$1, boolean $$2, boolean $$3) {
        DataPackConfig $$4 = $$1.dataPacks();
        FeatureFlagSet $$5 = $$2 ? FeatureFlagSet.of() : $$1.enabledFeatures();
        FeatureFlagSet $$6 = $$2 ? FeatureFlags.REGISTRY.allFlags() : $$1.enabledFeatures();
        $$0.reload();
        if ($$3) {
            return MinecraftServer.configureRepositoryWithSelection($$0, List.of((Object)VANILLA_BRAND), $$5, false);
        }
        LinkedHashSet<String> $$7 = Sets.newLinkedHashSet();
        for (String $$8 : $$4.getEnabled()) {
            if ($$0.isAvailable($$8)) {
                $$7.add($$8);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)$$8);
        }
        for (Pack $$9 : $$0.getAvailablePacks()) {
            String $$10 = $$9.getId();
            if ($$4.getDisabled().contains($$10)) continue;
            FeatureFlagSet $$11 = $$9.getRequestedFeatures();
            boolean $$12 = $$7.contains($$10);
            if (!$$12 && $$9.getPackSource().shouldAddAutomatically()) {
                if ($$11.isSubsetOf($$6)) {
                    LOGGER.info("Found new data pack {}, loading it automatically", (Object)$$10);
                    $$7.add($$10);
                } else {
                    LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", (Object)$$10, (Object)FeatureFlags.printMissingFlags($$6, $$11));
                }
            }
            if (!$$12 || $$11.isSubsetOf($$6)) continue;
            LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", (Object)$$10, (Object)FeatureFlags.printMissingFlags($$6, $$11));
            $$7.remove($$10);
        }
        if ($$7.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            $$7.add(VANILLA_BRAND);
        }
        return MinecraftServer.configureRepositoryWithSelection($$0, $$7, $$5, true);
    }

    private static WorldDataConfiguration configureRepositoryWithSelection(PackRepository $$0, Collection<String> $$1, FeatureFlagSet $$2, boolean $$3) {
        $$0.setSelected($$1);
        MinecraftServer.enableForcedFeaturePacks($$0, $$2);
        DataPackConfig $$4 = MinecraftServer.getSelectedPacks($$0, $$3);
        FeatureFlagSet $$5 = $$0.getRequestedFeatureFlags().join($$2);
        return new WorldDataConfiguration($$4, $$5);
    }

    private static void enableForcedFeaturePacks(PackRepository $$0, FeatureFlagSet $$1) {
        FeatureFlagSet $$2 = $$0.getRequestedFeatureFlags();
        FeatureFlagSet $$3 = $$1.subtract($$2);
        if ($$3.isEmpty()) {
            return;
        }
        ObjectArraySet $$4 = new ObjectArraySet($$0.getSelectedIds());
        for (Pack $$5 : $$0.getAvailablePacks()) {
            if ($$3.isEmpty()) break;
            if ($$5.getPackSource() != PackSource.FEATURE) continue;
            String $$6 = $$5.getId();
            FeatureFlagSet $$7 = $$5.getRequestedFeatures();
            if ($$7.isEmpty() || !$$7.intersects($$3) || !$$7.isSubsetOf($$1)) continue;
            if (!$$4.add($$6)) {
                throw new IllegalStateException("Tried to force '" + $$6 + "', but it was already enabled");
            }
            LOGGER.info("Found feature pack ('{}') for requested feature, forcing to enabled", (Object)$$6);
            $$3 = $$3.subtract($$7);
        }
        $$0.setSelected((Collection<String>)$$4);
    }

    private static DataPackConfig getSelectedPacks(PackRepository $$0, boolean $$12) {
        Collection<String> $$2 = $$0.getSelectedIds();
        ImmutableList<String> $$3 = ImmutableList.copyOf($$2);
        List $$4 = $$12 ? $$0.getAvailableIds().stream().filter($$1 -> !$$2.contains($$1)).toList() : List.of();
        return new DataPackConfig($$3, $$4);
    }

    public void kickUnlistedPlayers(CommandSourceStack $$0) {
        if (!this.isEnforceWhitelist()) {
            return;
        }
        PlayerList $$1 = $$0.getServer().getPlayerList();
        UserWhiteList $$2 = $$1.getWhiteList();
        ArrayList<ServerPlayer> $$3 = Lists.newArrayList($$1.getPlayers());
        for (ServerPlayer $$4 : $$3) {
            if ($$2.isWhiteListed($$4.getGameProfile())) continue;
            $$4.connection.disconnect(Component.translatable("multiplayer.disconnect.not_whitelisted"));
        }
    }

    public PackRepository getPackRepository() {
        return this.packRepository;
    }

    public Commands getCommands() {
        return this.resources.managers.getCommands();
    }

    public CommandSourceStack createCommandSourceStack() {
        ServerLevel $$0 = this.overworld();
        return new CommandSourceStack(this, $$0 == null ? Vec3.ZERO : Vec3.atLowerCornerOf($$0.getSharedSpawnPos()), Vec2.ZERO, $$0, 4, "Server", Component.literal("Server"), this, null);
    }

    @Override
    public boolean acceptsSuccess() {
        return true;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public abstract boolean shouldInformAdmins();

    public RecipeManager getRecipeManager() {
        return this.resources.managers.getRecipeManager();
    }

    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public CommandStorage getCommandStorage() {
        if (this.commandStorage == null) {
            throw new NullPointerException("Called before server init");
        }
        return this.commandStorage;
    }

    public GameRules getGameRules() {
        return this.overworld().getGameRules();
    }

    public CustomBossEvents getCustomBossEvents() {
        return this.customBossEvents;
    }

    public boolean isEnforceWhitelist() {
        return this.enforceWhitelist;
    }

    public void setEnforceWhitelist(boolean $$0) {
        this.enforceWhitelist = $$0;
    }

    public float getCurrentSmoothedTickTime() {
        return this.smoothedTickTimeMillis;
    }

    public ServerTickRateManager tickRateManager() {
        return this.tickRateManager;
    }

    public long getAverageTickTimeNanos() {
        return this.aggregatedTickTimesNanos / (long)Math.min(100, Math.max(this.tickCount, 1));
    }

    public long[] aR() {
        return this.tickTimesNanos;
    }

    public int getProfilePermissions(GameProfile $$0) {
        if (this.getPlayerList().isOp($$0)) {
            ServerOpListEntry $$1 = (ServerOpListEntry)this.getPlayerList().getOps().get($$0);
            if ($$1 != null) {
                return $$1.getLevel();
            }
            if (this.isSingleplayerOwner($$0)) {
                return 4;
            }
            if (this.isSingleplayer()) {
                return this.getPlayerList().isAllowCommandsForAllPlayers() ? 4 : 0;
            }
            return this.getOperatorUserPermissionLevel();
        }
        return 0;
    }

    public abstract boolean isSingleplayerOwner(GameProfile var1);

    public void dumpServerProperties(Path $$0) throws IOException {
    }

    private void saveDebugReport(Path $$0) {
        Path $$1 = $$0.resolve("levels");
        try {
            for (Map.Entry<ResourceKey<Level>, ServerLevel> $$2 : this.levels.entrySet()) {
                ResourceLocation $$3 = $$2.getKey().location();
                Path $$4 = $$1.resolve($$3.getNamespace()).resolve($$3.getPath());
                Files.createDirectories($$4, new FileAttribute[0]);
                $$2.getValue().saveDebugReport($$4);
            }
            this.dumpGameRules($$0.resolve("gamerules.txt"));
            this.dumpClasspath($$0.resolve("classpath.txt"));
            this.dumpMiscStats($$0.resolve("stats.txt"));
            this.dumpThreads($$0.resolve("threads.txt"));
            this.dumpServerProperties($$0.resolve("server.properties.txt"));
            this.dumpNativeModules($$0.resolve("modules.txt"));
        } catch (IOException $$5) {
            LOGGER.warn("Failed to save debug report", $$5);
        }
    }

    private void dumpMiscStats(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter($$0, new OpenOption[0]);){
            $$1.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getPendingTasksCount()));
            $$1.write(String.format(Locale.ROOT, "average_tick_time: %f\n", Float.valueOf(this.getCurrentSmoothedTickTime())));
            $$1.write(String.format(Locale.ROOT, "tick_times: %s\n", Arrays.toString(this.tickTimesNanos)));
            $$1.write(String.format(Locale.ROOT, "queue: %s\n", Util.backgroundExecutor()));
        }
    }

    private void dumpGameRules(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter($$0, new OpenOption[0]);){
            final ArrayList<String> $$2 = Lists.newArrayList();
            final GameRules $$3 = this.getGameRules();
            $$3.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

                @Override
                public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$1) {
                    $$2.add(String.format(Locale.ROOT, "%s=%s\n", $$0.getId(), $$3.getRule($$0)));
                }
            });
            for (String $$4 : $$2) {
                $$1.write($$4);
            }
        }
    }

    private void dumpClasspath(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter($$0, new OpenOption[0]);){
            String $$2 = System.getProperty("java.class.path");
            String $$3 = System.getProperty("path.separator");
            for (String $$4 : Splitter.on($$3).split($$2)) {
                $$1.write($$4);
                $$1.write("\n");
            }
        }
    }

    private void dumpThreads(Path $$0) throws IOException {
        ThreadMXBean $$1 = ManagementFactory.getThreadMXBean();
        ThreadInfo[] $$2 = $$1.dumpAllThreads(true, true);
        Arrays.sort($$2, Comparator.comparing(ThreadInfo::getThreadName));
        try (BufferedWriter $$3 = Files.newBufferedWriter($$0, new OpenOption[0]);){
            for (ThreadInfo $$4 : $$2) {
                $$3.write($$4.toString());
                ((Writer)$$3).write(10);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private void dumpNativeModules(Path $$02) throws IOException {
        BufferedWriter $$1 = Files.newBufferedWriter($$02, new OpenOption[0]);
        try {
            void $$4;
            try {
                ArrayList<NativeModuleLister.NativeModuleInfo> $$2 = Lists.newArrayList(NativeModuleLister.listModules());
            } catch (Throwable $$3) {
                LOGGER.warn("Failed to list native modules", $$3);
                if ($$1 != null) {
                    ((Writer)$$1).close();
                }
                return;
            }
            $$4.sort(Comparator.comparing($$0 -> $$0.name));
            for (NativeModuleLister.NativeModuleInfo $$5 : $$4) {
                $$1.write($$5.toString());
                ((Writer)$$1).write(10);
            }
        } finally {
            if ($$1 != null) {
                try {
                    ((Writer)$$1).close();
                } catch (Throwable throwable) {
                    Throwable throwable2;
                    throwable2.addSuppressed(throwable);
                }
            }
        }
    }

    private ProfilerFiller createProfiler() {
        if (this.willStartRecordingMetrics) {
            this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()), Util.timeSource, Util.ioPool(), new MetricsPersister("server"), this.onMetricsRecordingStopped, $$0 -> {
                this.executeBlocking(() -> this.saveDebugReport($$0.resolve("server")));
                this.onMetricsRecordingFinished.accept((Path)$$0);
            });
            this.willStartRecordingMetrics = false;
        }
        this.metricsRecorder.startTick();
        return SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
    }

    public void endMetricsRecordingTick() {
        this.metricsRecorder.endTick();
    }

    public boolean isRecordingMetrics() {
        return this.metricsRecorder.isRecording();
    }

    public void startRecordingMetrics(Consumer<ProfileResults> $$0, Consumer<Path> $$12) {
        this.onMetricsRecordingStopped = $$1 -> {
            this.stopRecordingMetrics();
            $$0.accept((ProfileResults)$$1);
        };
        this.onMetricsRecordingFinished = $$12;
        this.willStartRecordingMetrics = true;
    }

    public void stopRecordingMetrics() {
        this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    }

    public void finishRecordingMetrics() {
        this.metricsRecorder.end();
    }

    public void cancelRecordingMetrics() {
        this.metricsRecorder.cancel();
    }

    public Path getWorldPath(LevelResource $$0) {
        return this.storageSource.getLevelPath($$0);
    }

    public boolean forceSynchronousWrites() {
        return true;
    }

    public StructureTemplateManager getStructureManager() {
        return this.structureTemplateManager;
    }

    public WorldData getWorldData() {
        return this.worldData;
    }

    public RegistryAccess.Frozen registryAccess() {
        return this.registries.compositeAccess();
    }

    public LayeredRegistryAccess<RegistryLayer> registries() {
        return this.registries;
    }

    public ReloadableServerRegistries.Holder reloadableRegistries() {
        return this.resources.managers.fullRegistries();
    }

    public TextFilter createTextFilterForPlayer(ServerPlayer $$0) {
        return TextFilter.DUMMY;
    }

    public ServerPlayerGameMode createGameModeForPlayer(ServerPlayer $$0) {
        return this.isDemo() ? new DemoMode($$0) : new ServerPlayerGameMode($$0);
    }

    @Nullable
    public GameType getForcedGameType() {
        return null;
    }

    public ResourceManager getResourceManager() {
        return this.resources.resourceManager;
    }

    public boolean isCurrentlySaving() {
        return this.isSaving;
    }

    public boolean isTimeProfilerRunning() {
        return this.debugCommandProfilerDelayStart || this.debugCommandProfiler != null;
    }

    public void startTimeProfiler() {
        this.debugCommandProfilerDelayStart = true;
    }

    public ProfileResults stopTimeProfiler() {
        if (this.debugCommandProfiler == null) {
            return EmptyProfileResults.EMPTY;
        }
        ProfileResults $$0 = this.debugCommandProfiler.stop(Util.getNanos(), this.tickCount);
        this.debugCommandProfiler = null;
        return $$0;
    }

    public int getMaxChainedNeighborUpdates() {
        return 1000000;
    }

    public void logChatMessage(Component $$0, ChatType.Bound $$1, @Nullable String $$2) {
        String $$3 = $$1.decorate($$0).getString();
        if ($$2 != null) {
            LOGGER.info("[{}] {}", (Object)$$2, (Object)$$3);
        } else {
            LOGGER.info("{}", (Object)$$3);
        }
    }

    public ChatDecorator getChatDecorator() {
        return ChatDecorator.PLAIN;
    }

    public boolean logIPs() {
        return true;
    }

    public void subscribeToDebugSample(ServerPlayer $$0, RemoteDebugSampleType $$1) {
    }

    public void handleCustomClickAction(ResourceLocation $$0, Optional<Tag> $$1) {
        LOGGER.debug("Received custom click action {} with payload {}", (Object)$$0, (Object)$$1.orElse(null));
    }

    public boolean acceptsTransfers() {
        return false;
    }

    private void storeChunkIoError(CrashReport $$0, ChunkPos $$1, RegionStorageInfo $$2) {
        Util.ioPool().execute(() -> {
            try {
                Path $$3 = this.getFile("debug");
                FileUtil.createDirectoriesSafe($$3);
                String $$4 = FileUtil.sanitizeName($$2.level());
                Path $$5 = $$3.resolve("chunk-" + $$4 + "-" + Util.getFilenameFormattedDateTime() + "-server.txt");
                FileStore $$6 = Files.getFileStore($$3);
                long $$7 = $$6.getUsableSpace();
                if ($$7 < 8192L) {
                    LOGGER.warn("Not storing chunk IO report due to low space on drive {}", (Object)$$6.name());
                    return;
                }
                CrashReportCategory $$8 = $$0.addCategory("Chunk Info");
                $$8.setDetail("Level", $$2::level);
                $$8.setDetail("Dimension", () -> $$2.dimension().location().toString());
                $$8.setDetail("Storage", $$2::type);
                $$8.setDetail("Position", $$1::toString);
                $$0.saveToFile($$5, ReportType.CHUNK_IO_ERROR);
                LOGGER.info("Saved details to {}", (Object)$$0.getSaveFile());
            } catch (Exception $$9) {
                LOGGER.warn("Failed to store chunk IO exception", $$9);
            }
        });
    }

    @Override
    public void reportChunkLoadFailure(Throwable $$0, RegionStorageInfo $$1, ChunkPos $$2) {
        LOGGER.error("Failed to load chunk {},{}", $$2.x, $$2.z, $$0);
        this.suppressedExceptions.addEntry("chunk/load", $$0);
        this.storeChunkIoError(CrashReport.forThrowable($$0, "Chunk load failure"), $$2, $$1);
    }

    @Override
    public void reportChunkSaveFailure(Throwable $$0, RegionStorageInfo $$1, ChunkPos $$2) {
        LOGGER.error("Failed to save chunk {},{}", $$2.x, $$2.z, $$0);
        this.suppressedExceptions.addEntry("chunk/save", $$0);
        this.storeChunkIoError(CrashReport.forThrowable($$0, "Chunk save failure"), $$2, $$1);
    }

    public void reportPacketHandlingException(Throwable $$0, PacketType<?> $$1) {
        this.suppressedExceptions.addEntry("packet/" + $$1.toString(), $$0);
    }

    public PotionBrewing potionBrewing() {
        return this.potionBrewing;
    }

    public FuelValues fuelValues() {
        return this.fuelValues;
    }

    public ServerLinks serverLinks() {
        return ServerLinks.EMPTY;
    }

    protected int pauseWhileEmptySeconds() {
        return 0;
    }

    @Override
    public /* synthetic */ void doRunTask(Runnable runnable) {
        this.doRunTask((TickTask)runnable);
    }

    @Override
    public /* synthetic */ boolean shouldRun(Runnable runnable) {
        return this.shouldRun((TickTask)runnable);
    }

    @Override
    public /* synthetic */ Runnable wrapRunnable(Runnable runnable) {
        return this.wrapRunnable(runnable);
    }

    static final class ReloadableResources
    extends Record
    implements AutoCloseable {
        final CloseableResourceManager resourceManager;
        final ReloadableServerResources managers;

        ReloadableResources(CloseableResourceManager $$0, ReloadableServerResources $$1) {
            this.resourceManager = $$0;
            this.managers = $$1;
        }

        @Override
        public void close() {
            this.resourceManager.close();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReloadableResources.class, "resourceManager;managers", "resourceManager", "managers"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReloadableResources.class, "resourceManager;managers", "resourceManager", "managers"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReloadableResources.class, "resourceManager;managers", "resourceManager", "managers"}, this, $$0);
        }

        public CloseableResourceManager resourceManager() {
            return this.resourceManager;
        }

        public ReloadableServerResources managers() {
            return this.managers;
        }
    }

    static class TimeProfiler {
        final long startNanos;
        final int startTick;

        TimeProfiler(long $$0, int $$1) {
            this.startNanos = $$0;
            this.startTick = $$1;
        }

        ProfileResults stop(final long $$0, final int $$1) {
            return new ProfileResults(){

                @Override
                public List<ResultField> getTimes(String $$02) {
                    return Collections.emptyList();
                }

                @Override
                public boolean saveResults(Path $$02) {
                    return false;
                }

                @Override
                public long getStartTimeNano() {
                    return startNanos;
                }

                @Override
                public int getStartTimeTicks() {
                    return startTick;
                }

                @Override
                public long getEndTimeNano() {
                    return $$0;
                }

                @Override
                public int getEndTimeTicks() {
                    return $$1;
                }

                @Override
                public String getProfilerResults() {
                    return "";
                }
            };
        }
    }

    public record ServerResourcePackInfo(UUID id, String url, String hash, boolean isRequired, @Nullable Component prompt) {
        @Nullable
        public Component prompt() {
            return this.prompt;
        }
    }
}

