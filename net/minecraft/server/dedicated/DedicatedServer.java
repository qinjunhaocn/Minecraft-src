/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.dedicated.ServerWatchdog;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerTextFilter;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.DebugSampleSubscriptionTracker;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.RemoteSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class DedicatedServer
extends MinecraftServer
implements ServerInterface {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int CONVERSION_RETRY_DELAY_MS = 5000;
    private static final int CONVERSION_RETRIES = 2;
    private final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
    @Nullable
    private QueryThreadGs4 queryThreadGs4;
    private final RconConsoleSource rconConsoleSource;
    @Nullable
    private RconThread rconThread;
    private final DedicatedServerSettings settings;
    @Nullable
    private MinecraftServerGui gui;
    @Nullable
    private final ServerTextFilter serverTextFilter;
    @Nullable
    private RemoteSampleLogger tickTimeLogger;
    @Nullable
    private DebugSampleSubscriptionTracker debugSampleSubscriptionTracker;
    private final ServerLinks serverLinks;

    public DedicatedServer(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, DedicatedServerSettings $$4, DataFixer $$5, Services $$6, ChunkProgressListenerFactory $$7) {
        super($$0, $$1, $$2, $$3, Proxy.NO_PROXY, $$5, $$6, $$7);
        this.settings = $$4;
        this.rconConsoleSource = new RconConsoleSource(this);
        this.serverTextFilter = ServerTextFilter.createFromConfig($$4.getProperties());
        this.serverLinks = DedicatedServer.createServerLinks($$4);
    }

    @Override
    public boolean initServer() throws IOException {
        Thread $$0 = new Thread("Server console handler"){

            @Override
            public void run() {
                BufferedReader $$0 = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                try {
                    String $$1;
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && ($$1 = $$0.readLine()) != null) {
                        DedicatedServer.this.handleConsoleInput($$1, DedicatedServer.this.createCommandSourceStack());
                    }
                } catch (IOException $$2) {
                    LOGGER.error("Exception handling console input", $$2);
                }
            }
        };
        $$0.setDaemon(true);
        $$0.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        $$0.start();
        LOGGER.info("Starting minecraft server version {}", (Object)SharedConstants.getCurrentVersion().name());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }
        LOGGER.info("Loading properties");
        DedicatedServerProperties $$1 = this.settings.getProperties();
        if (this.isSingleplayer()) {
            this.setLocalIp("127.0.0.1");
        } else {
            this.setUsesAuthentication($$1.onlineMode);
            this.setPreventProxyConnections($$1.preventProxyConnections);
            this.setLocalIp($$1.serverIp);
        }
        this.setPvpAllowed($$1.pvp);
        this.setFlightAllowed($$1.allowFlight);
        this.setMotd($$1.motd);
        super.setPlayerIdleTimeout($$1.playerIdleTimeout.get());
        this.setEnforceWhitelist($$1.enforceWhitelist);
        this.worldData.setGameType($$1.gamemode);
        LOGGER.info("Default game type: {}", (Object)$$1.gamemode);
        InetAddress $$2 = null;
        if (!this.getLocalIp().isEmpty()) {
            $$2 = InetAddress.getByName(this.getLocalIp());
        }
        if (this.getPort() < 0) {
            this.setPort($$1.serverPort);
        }
        this.initializeKeyPair();
        LOGGER.info("Starting Minecraft server on {}:{}", (Object)(this.getLocalIp().isEmpty() ? "*" : this.getLocalIp()), (Object)this.getPort());
        try {
            this.getConnection().startTcpServerListener($$2, this.getPort());
        } catch (IOException $$3) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", (Object)$$3.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }
        if (!this.usesAuthentication()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }
        if (this.convertOldUsers()) {
            this.getProfileCache().save();
        }
        if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
            return false;
        }
        this.setPlayerList(new DedicatedPlayerList(this, this.registries(), this.playerDataStorage));
        this.debugSampleSubscriptionTracker = new DebugSampleSubscriptionTracker(this.getPlayerList());
        this.tickTimeLogger = new RemoteSampleLogger(TpsDebugDimensions.values().length, this.debugSampleSubscriptionTracker, RemoteDebugSampleType.TICK_TIME);
        long $$4 = Util.getNanos();
        SkullBlockEntity.setup(this.services, this);
        GameProfileCache.setUsesAuthentication(this.usesAuthentication());
        LOGGER.info("Preparing level \"{}\"", (Object)this.getLevelIdName());
        this.loadLevel();
        long $$5 = Util.getNanos() - $$4;
        String $$6 = String.format(Locale.ROOT, "%.3fs", (double)$$5 / 1.0E9);
        LOGGER.info("Done ({})! For help, type \"help\"", (Object)$$6);
        if ($$1.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set($$1.announcePlayerAchievements, this);
        }
        if ($$1.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
        }
        if ($$1.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
        }
        if (this.getMaxTickLength() > 0L) {
            Thread $$7 = new Thread(new ServerWatchdog(this));
            $$7.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            $$7.setName("Server Watchdog");
            $$7.setDaemon(true);
            $$7.start();
        }
        if ($$1.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
            LOGGER.info("JMX monitoring enabled");
        }
        return true;
    }

    @Override
    public boolean isSpawningMonsters() {
        return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
    }

    @Override
    public DedicatedServerProperties getProperties() {
        return this.settings.getProperties();
    }

    @Override
    public void forceDifficulty() {
        this.setDifficulty(this.getProperties().difficulty, true);
    }

    @Override
    public SystemReport fillServerSystemReport(SystemReport $$0) {
        $$0.setDetail("Is Modded", () -> this.getModdedStatus().fullDescription());
        $$0.setDetail("Type", () -> "Dedicated Server (map_server.txt)");
        return $$0;
    }

    @Override
    public void dumpServerProperties(Path $$0) throws IOException {
        DedicatedServerProperties $$1 = this.getProperties();
        try (BufferedWriter $$2 = Files.newBufferedWriter($$0, new OpenOption[0]);){
            $$2.write(String.format(Locale.ROOT, "sync-chunk-writes=%s%n", $$1.syncChunkWrites));
            $$2.write(String.format(Locale.ROOT, "gamemode=%s%n", $$1.gamemode));
            $$2.write(String.format(Locale.ROOT, "spawn-monsters=%s%n", $$1.spawnMonsters));
            $$2.write(String.format(Locale.ROOT, "entity-broadcast-range-percentage=%d%n", $$1.entityBroadcastRangePercentage));
            $$2.write(String.format(Locale.ROOT, "max-world-size=%d%n", $$1.maxWorldSize));
            $$2.write(String.format(Locale.ROOT, "view-distance=%d%n", $$1.viewDistance));
            $$2.write(String.format(Locale.ROOT, "simulation-distance=%d%n", $$1.simulationDistance));
            $$2.write(String.format(Locale.ROOT, "generate-structures=%s%n", $$1.worldOptions.generateStructures()));
            $$2.write(String.format(Locale.ROOT, "use-native=%s%n", $$1.useNativeTransport));
            $$2.write(String.format(Locale.ROOT, "rate-limit=%d%n", $$1.rateLimitPacketsPerSecond));
        }
    }

    @Override
    public void onServerExit() {
        if (this.serverTextFilter != null) {
            this.serverTextFilter.close();
        }
        if (this.gui != null) {
            this.gui.close();
        }
        if (this.rconThread != null) {
            this.rconThread.stop();
        }
        if (this.queryThreadGs4 != null) {
            this.queryThreadGs4.stop();
        }
    }

    @Override
    public void tickConnection() {
        super.tickConnection();
        this.handleConsoleInputs();
    }

    @Override
    public boolean isLevelEnabled(Level $$0) {
        if ($$0.dimension() == Level.NETHER) {
            return this.getProperties().allowNether;
        }
        return true;
    }

    public void handleConsoleInput(String $$0, CommandSourceStack $$1) {
        this.consoleInput.add(new ConsoleInput($$0, $$1));
    }

    public void handleConsoleInputs() {
        while (!this.consoleInput.isEmpty()) {
            ConsoleInput $$0 = this.consoleInput.remove(0);
            this.getCommands().performPrefixedCommand($$0.source, $$0.msg);
        }
    }

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return this.getProperties().rateLimitPacketsPerSecond;
    }

    @Override
    public boolean isEpollEnabled() {
        return this.getProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList)super.getPlayerList();
    }

    @Override
    public boolean isPublished() {
        return true;
    }

    @Override
    public String getServerIp() {
        return this.getLocalIp();
    }

    @Override
    public int getServerPort() {
        return this.getPort();
    }

    @Override
    public String getServerName() {
        return this.getMotd();
    }

    public void showGui() {
        if (this.gui == null) {
            this.gui = MinecraftServerGui.showFrameFor(this);
        }
    }

    @Override
    public boolean hasGui() {
        return this.gui != null;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return this.getProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }

    @Override
    public boolean isUnderSpawnProtection(ServerLevel $$0, BlockPos $$1, Player $$2) {
        int $$5;
        if ($$0.dimension() != Level.OVERWORLD) {
            return false;
        }
        if (this.getPlayerList().getOps().isEmpty()) {
            return false;
        }
        if (this.getPlayerList().isOp($$2.getGameProfile())) {
            return false;
        }
        if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        }
        BlockPos $$3 = $$0.getSharedSpawnPos();
        int $$4 = Mth.abs($$1.getX() - $$3.getX());
        int $$6 = Math.max($$4, $$5 = Mth.abs($$1.getZ() - $$3.getZ()));
        return $$6 <= this.getSpawnProtectionRadius();
    }

    @Override
    public boolean repliesToStatus() {
        return this.getProperties().enableStatus;
    }

    @Override
    public boolean hidesOnlinePlayers() {
        return this.getProperties().hideOnlinePlayers;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return this.getProperties().functionPermissionLevel;
    }

    @Override
    public void setPlayerIdleTimeout(int $$0) {
        super.setPlayerIdleTimeout($$0);
        this.settings.update($$1 -> (DedicatedServerProperties)$$1.playerIdleTimeout.update(this.registryAccess(), $$0));
    }

    @Override
    public boolean shouldRconBroadcast() {
        return this.getProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getProperties().broadcastConsoleToOps;
    }

    @Override
    public int getAbsoluteMaxWorldSize() {
        return this.getProperties().maxWorldSize;
    }

    @Override
    public int getCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }

    @Override
    public boolean enforceSecureProfile() {
        DedicatedServerProperties $$0 = this.getProperties();
        return $$0.enforceSecureProfile && $$0.onlineMode && this.services.canValidateProfileKeys();
    }

    @Override
    public boolean logIPs() {
        return this.getProperties().logIPs;
    }

    protected boolean convertOldUsers() {
        int $$1;
        boolean $$0 = false;
        for ($$1 = 0; !$$0 && $$1 <= 2; ++$$1) {
            if ($$1 > 0) {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.waitForRetry();
            }
            $$0 = OldUsersConverter.convertUserBanlist(this);
        }
        boolean $$2 = false;
        for ($$1 = 0; !$$2 && $$1 <= 2; ++$$1) {
            if ($$1 > 0) {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.waitForRetry();
            }
            $$2 = OldUsersConverter.convertIpBanlist(this);
        }
        boolean $$3 = false;
        for ($$1 = 0; !$$3 && $$1 <= 2; ++$$1) {
            if ($$1 > 0) {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.waitForRetry();
            }
            $$3 = OldUsersConverter.convertOpsList(this);
        }
        boolean $$4 = false;
        for ($$1 = 0; !$$4 && $$1 <= 2; ++$$1) {
            if ($$1 > 0) {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.waitForRetry();
            }
            $$4 = OldUsersConverter.convertWhiteList(this);
        }
        boolean $$5 = false;
        for ($$1 = 0; !$$5 && $$1 <= 2; ++$$1) {
            if ($$1 > 0) {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.waitForRetry();
            }
            $$5 = OldUsersConverter.convertPlayers(this);
        }
        return $$0 || $$2 || $$3 || $$4 || $$5;
    }

    private void waitForRetry() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException $$0) {
            return;
        }
    }

    public long getMaxTickLength() {
        return this.getProperties().maxTickTime;
    }

    @Override
    public int getMaxChainedNeighborUpdates() {
        return this.getProperties().maxChainedNeighborUpdates;
    }

    @Override
    public String getPluginNames() {
        return "";
    }

    @Override
    public String runCommand(String $$0) {
        this.rconConsoleSource.prepareForCommand();
        this.executeBlocking(() -> this.getCommands().performPrefixedCommand(this.rconConsoleSource.createCommandSourceStack(), $$0));
        return this.rconConsoleSource.getCommandResponse();
    }

    public void storeUsingWhiteList(boolean $$0) {
        this.settings.update($$1 -> (DedicatedServerProperties)$$1.whiteList.update(this.registryAccess(), $$0));
    }

    @Override
    public void stopServer() {
        super.stopServer();
        Util.shutdownExecutors();
        SkullBlockEntity.clear();
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile $$0) {
        return false;
    }

    @Override
    public int getScaledTrackingDistance(int $$0) {
        return this.getProperties().entityBroadcastRangePercentage * $$0 / 100;
    }

    @Override
    public String getLevelIdName() {
        return this.storageSource.getLevelId();
    }

    @Override
    public boolean forceSynchronousWrites() {
        return this.settings.getProperties().syncChunkWrites;
    }

    @Override
    public TextFilter createTextFilterForPlayer(ServerPlayer $$0) {
        if (this.serverTextFilter != null) {
            return this.serverTextFilter.createContext($$0.getGameProfile());
        }
        return TextFilter.DUMMY;
    }

    @Override
    @Nullable
    public GameType getForcedGameType() {
        return this.settings.getProperties().forceGameMode ? this.worldData.getGameType() : null;
    }

    @Override
    public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
        return this.settings.getProperties().serverResourcePackInfo;
    }

    @Override
    public void endMetricsRecordingTick() {
        super.endMetricsRecordingTick();
        this.debugSampleSubscriptionTracker.tick(this.getTickCount());
    }

    @Override
    public SampleLogger getTickTimeLogger() {
        return this.tickTimeLogger;
    }

    @Override
    public boolean isTickTimeLoggingEnabled() {
        return this.debugSampleSubscriptionTracker.shouldLogSamples(RemoteDebugSampleType.TICK_TIME);
    }

    @Override
    public void subscribeToDebugSample(ServerPlayer $$0, RemoteDebugSampleType $$1) {
        this.debugSampleSubscriptionTracker.subscribe($$0, $$1);
    }

    @Override
    public boolean acceptsTransfers() {
        return this.settings.getProperties().acceptsTransfers;
    }

    @Override
    public ServerLinks serverLinks() {
        return this.serverLinks;
    }

    @Override
    public int pauseWhileEmptySeconds() {
        return this.settings.getProperties().pauseWhenEmptySeconds;
    }

    private static ServerLinks createServerLinks(DedicatedServerSettings $$02) {
        Optional<URI> $$1 = DedicatedServer.parseBugReportLink($$02.getProperties());
        return $$1.map($$0 -> new ServerLinks(List.of((Object)((Object)ServerLinks.KnownLinkType.BUG_REPORT.create((URI)$$0))))).orElse(ServerLinks.EMPTY);
    }

    private static Optional<URI> parseBugReportLink(DedicatedServerProperties $$0) {
        String $$1 = $$0.bugReportLink;
        if ($$1.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Util.parseAndValidateUntrustedUri($$1));
        } catch (Exception $$2) {
            LOGGER.warn("Failed to parse bug link {}", (Object)$$1, (Object)$$2);
            return Optional.empty();
        }
    }

    @Override
    public /* synthetic */ PlayerList getPlayerList() {
        return this.getPlayerList();
    }
}

