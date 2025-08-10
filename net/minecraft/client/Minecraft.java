/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserFlag
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileActionType
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.authlib.yggdrasil.ServicesKeyType
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.jtracy.DiscontinuousFrame
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  java.lang.runtime.SwitchBootstraps
 *  org.apache.commons.io.FileUtils
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.ClientShutdownWatchdog;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.FramerateLimitTracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.TimerQuery;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.management.ManagementFactory;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.Optionull;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.CommandHistory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.PeriodicNotificationManager;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.Screenshot;
import net.minecraft.client.User;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.BanNoticeScreens;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.profiling.ClientMetricsSamplersProvider;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.DryFoliageColorReloadListener;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.WaypointStyleManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.KeybindResolver;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DialogTags;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.FileZipper;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.ModCheck;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.io.FileUtils;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class Minecraft
extends ReentrantBlockableEventLoop<Runnable>
implements WindowEventHandler {
    static Minecraft instance;
    private static final Logger LOGGER;
    public static final boolean ON_OSX;
    private static final int MAX_TICKS_PER_UPDATE = 10;
    public static final ResourceLocation DEFAULT_FONT;
    public static final ResourceLocation UNIFORM_FONT;
    public static final ResourceLocation ALT_FONT;
    private static final ResourceLocation REGIONAL_COMPLIANCIES;
    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK;
    private static final Component SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    private static final Component SAVING_LEVEL;
    public static final String UPDATE_DRIVERS_ADVICE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    private final long canary = Double.doubleToLongBits(Math.PI);
    private final Path resourcePackDirectory;
    private final CompletableFuture<ProfileResult> profileFuture;
    private final TextureManager textureManager;
    private final ShaderManager shaderManager;
    private final DataFixer fixerUpper;
    private final VirtualScreen virtualScreen;
    private final Window window;
    private final DeltaTracker.Timer deltaTracker = new DeltaTracker.Timer(20.0f, 0L, this::getTickTargetMillis);
    private final RenderBuffers renderBuffers;
    public final LevelRenderer levelRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemModelResolver itemModelResolver;
    private final ItemRenderer itemRenderer;
    private final MapRenderer mapRenderer;
    public final ParticleEngine particleEngine;
    private final User user;
    public final Font font;
    public final Font fontFilterFishy;
    public final GameRenderer gameRenderer;
    public final DebugRenderer debugRenderer;
    private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference();
    public final Gui gui;
    public final Options options;
    private final HotbarManager hotbarManager;
    public final MouseHandler mouseHandler;
    public final KeyboardHandler keyboardHandler;
    private InputType lastInputType = InputType.NONE;
    public final File gameDirectory;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private final LevelStorageSource levelSource;
    private final boolean demo;
    private final boolean allowsMultiplayer;
    private final boolean allowsChat;
    private final ReloadableResourceManager resourceManager;
    private final VanillaPackResources vanillaPackResources;
    private final DownloadedPackSource downloadedPackSource;
    private final PackRepository resourcePackRepository;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final RenderTarget mainRenderTarget;
    @Nullable
    private final TracyFrameCapture tracyFrameCapture;
    private final SoundManager soundManager;
    private final MusicManager musicManager;
    private final FontManager fontManager;
    private final SplashManager splashManager;
    private final GpuWarnlistManager gpuWarnlistManager;
    private final PeriodicNotificationManager regionalCompliancies = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES, (Object2BooleanFunction<String>)((Object2BooleanFunction)Minecraft::countryEqualsISO3));
    private final YggdrasilAuthenticationService authenticationService;
    private final MinecraftSessionService minecraftSessionService;
    private final UserApiService userApiService;
    private final CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
    private final SkinManager skinManager;
    private final ModelManager modelManager;
    private final BlockRenderDispatcher blockRenderer;
    private final PaintingTextureManager paintingTextures;
    private final MapTextureManager mapTextureManager;
    private final MapDecorationTextureManager mapDecorationTextures;
    private final GuiSpriteManager guiSprites;
    private final WaypointStyleManager waypointStyles;
    private final ToastManager toastManager;
    private final Tutorial tutorial;
    private final PlayerSocialManager playerSocialManager;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final ClientTelemetryManager telemetryManager;
    private final ProfileKeyPairManager profileKeyPairManager;
    private final RealmsDataFetcher realmsDataFetcher;
    private final QuickPlayLog quickPlayLog;
    @Nullable
    public MultiPlayerGameMode gameMode;
    @Nullable
    public ClientLevel level;
    @Nullable
    public LocalPlayer player;
    @Nullable
    private IntegratedServer singleplayerServer;
    @Nullable
    private Connection pendingConnection;
    private boolean isLocalServer;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity crosshairPickEntity;
    @Nullable
    public HitResult hitResult;
    private int rightClickDelay;
    protected int missTime;
    private volatile boolean pause;
    private long lastNanoTime = Util.getNanos();
    private long lastTime;
    private int frames;
    public boolean noRender;
    @Nullable
    public Screen screen;
    @Nullable
    private Overlay overlay;
    private boolean clientLevelTeardownInProgress;
    Thread gameThread;
    private volatile boolean running;
    @Nullable
    private Supplier<CrashReport> delayedCrash;
    private static int fps;
    public String fpsString = "";
    private long frameTimeNs;
    private final FramerateLimitTracker framerateLimitTracker;
    public boolean wireframe;
    public boolean sectionPath;
    public boolean sectionVisibility;
    public boolean smartCull = true;
    private boolean windowActive;
    private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> pendingReload;
    @Nullable
    private TutorialToast socialInteractionsToast;
    private int fpsPieRenderTicks;
    private final ContinuousProfiler fpsPieProfiler;
    private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    private final ResourceLoadStateTracker reloadStateTracker = new ResourceLoadStateTracker();
    private long savedCpuDuration;
    private double gpuUtilization;
    @Nullable
    private TimerQuery.FrameProfile currentFrameProfile;
    private final GameNarrator narrator;
    private final ChatListener chatListener;
    private ReportingContext reportingContext;
    private final CommandHistory commandHistory;
    private final DirectoryValidator directoryValidator;
    private boolean gameLoadFinished;
    private final long clientStartTimeMs;
    private long clientTickCount;

    public Minecraft(final GameConfig $$02) {
        super("Client");
        instance = this;
        this.clientStartTimeMs = System.currentTimeMillis();
        this.gameDirectory = $$02.location.gameDirectory;
        File $$13 = $$02.location.assetDirectory;
        this.resourcePackDirectory = $$02.location.resourcePackDirectory.toPath();
        this.launchedVersion = $$02.game.launchVersion;
        this.versionType = $$02.game.versionType;
        Path $$2 = this.gameDirectory.toPath();
        this.directoryValidator = LevelStorageSource.parseValidator($$2.resolve("allowed_symlinks.txt"));
        ClientPackSource $$3 = new ClientPackSource($$02.location.getExternalAssetSource(), this.directoryValidator);
        this.downloadedPackSource = new DownloadedPackSource(this, $$2.resolve("downloads"), $$02.user);
        FolderRepositorySource $$4 = new FolderRepositorySource(this.resourcePackDirectory, PackType.CLIENT_RESOURCES, PackSource.DEFAULT, this.directoryValidator);
        this.resourcePackRepository = new PackRepository($$3, this.downloadedPackSource.createRepositorySource(), $$4);
        this.vanillaPackResources = $$3.getVanillaPack();
        this.proxy = $$02.user.proxy;
        this.authenticationService = new YggdrasilAuthenticationService(this.proxy);
        this.minecraftSessionService = this.authenticationService.createMinecraftSessionService();
        this.user = $$02.user.user;
        this.profileFuture = CompletableFuture.supplyAsync(() -> this.minecraftSessionService.fetchProfile(this.user.getProfileId(), true), Util.nonCriticalIoPool());
        this.userApiService = this.createUserApiService(this.authenticationService, $$02);
        this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return this.userApiService.fetchProperties();
            } catch (AuthenticationException $$0) {
                LOGGER.error("Failed to fetch user properties", $$0);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, Util.nonCriticalIoPool());
        LOGGER.info("Setting user: {}", (Object)this.user.getName());
        LOGGER.debug("(Session ID is {})", (Object)this.user.getSessionId());
        this.demo = $$02.game.demo;
        this.allowsMultiplayer = !$$02.game.disableMultiplayer;
        this.allowsChat = !$$02.game.disableChat;
        this.singleplayerServer = null;
        KeybindResolver.setKeyResolver(KeyMapping::createNameSupplier);
        this.fixerUpper = DataFixers.getDataFixer();
        this.gameThread = Thread.currentThread();
        this.options = new Options(this, this.gameDirectory);
        this.toastManager = new ToastManager(this, this.options);
        boolean $$5 = this.options.startedCleanly;
        this.options.startedCleanly = false;
        this.options.save();
        this.running = true;
        this.tutorial = new Tutorial(this, this.options);
        this.hotbarManager = new HotbarManager($$2, this.fixerUpper);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        DisplayData $$6 = $$02.display;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            $$6 = $$02.display.withSize(this.options.overrideWidth, this.options.overrideHeight);
        }
        if (!$$5) {
            $$6 = $$6.withFullscreen(false);
            this.options.fullscreenVideoModeString = null;
            LOGGER.warn("Detected unexpected shutdown during last game startup: resetting fullscreen mode");
        }
        Util.timeSource = RenderSystem.initBackendSystem();
        this.virtualScreen = new VirtualScreen(this);
        this.window = this.virtualScreen.newWindow($$6, this.options.fullscreenVideoModeString, this.createTitle());
        this.setWindowActive(true);
        this.window.setWindowCloseCallback(new Runnable(){
            private boolean threadStarted;

            @Override
            public void run() {
                if (!this.threadStarted) {
                    this.threadStarted = true;
                    ClientShutdownWatchdog.startShutdownWatchdog($$02.location.gameDirectory, Minecraft.this.gameThread.threadId());
                }
            }
        });
        GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS);
        try {
            this.window.setIcon(this.vanillaPackResources, SharedConstants.getCurrentVersion().stable() ? IconSet.RELEASE : IconSet.SNAPSHOT);
        } catch (IOException $$7) {
            LOGGER.error("Couldn't set icon", $$7);
        }
        this.mouseHandler = new MouseHandler(this);
        this.mouseHandler.setup(this.window.getWindow());
        this.keyboardHandler = new KeyboardHandler(this);
        this.keyboardHandler.setup(this.window.getWindow());
        RenderSystem.initRenderer(this.window.getWindow(), this.options.glDebugVerbosity, false, ($$0, $$1) -> this.getShaderManager().getShader((ResourceLocation)$$0, (ShaderType)((Object)$$1)), $$02.game.renderDebugLabels);
        LOGGER.info("Using optional rendering extensions: {}", (Object)String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        this.mainRenderTarget = new MainTarget(this.window.getWidth(), this.window.getHeight());
        this.resourceManager = new ReloadableResourceManager(PackType.CLIENT_RESOURCES);
        this.resourcePackRepository.reload();
        this.options.loadSelectedResourcePacks(this.resourcePackRepository);
        this.languageManager = new LanguageManager(this.options.languageCode, $$0 -> {
            if (this.player != null) {
                this.player.connection.updateSearchTrees();
            }
        });
        this.resourceManager.registerReloadListener(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloadListener(this.textureManager);
        this.shaderManager = new ShaderManager(this.textureManager, this::triggerResourcePackRecovery);
        this.resourceManager.registerReloadListener(this.shaderManager);
        this.skinManager = new SkinManager($$13.toPath().resolve("skins"), this.minecraftSessionService, this);
        this.levelSource = new LevelStorageSource($$2.resolve("saves"), $$2.resolve("backups"), this.directoryValidator, this.fixerUpper);
        this.commandHistory = new CommandHistory($$2);
        this.musicManager = new MusicManager(this);
        this.soundManager = new SoundManager(this.options, this.musicManager);
        this.resourceManager.registerReloadListener(this.soundManager);
        this.splashManager = new SplashManager(this.user);
        this.resourceManager.registerReloadListener(this.splashManager);
        this.fontManager = new FontManager(this.textureManager);
        this.font = this.fontManager.createFont();
        this.fontFilterFishy = this.fontManager.createFontFilterFishy();
        this.resourceManager.registerReloadListener(this.fontManager);
        this.updateFontOptions();
        this.resourceManager.registerReloadListener(new GrassColorReloadListener());
        this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.resourceManager.registerReloadListener(new DryFoliageColorReloadListener());
        this.window.setErrorSection("Startup");
        RenderSystem.setupDefaultState();
        this.window.setErrorSection("Post startup");
        this.blockColors = BlockColors.createDefault();
        this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels().get());
        this.resourceManager.registerReloadListener(this.modelManager);
        EquipmentAssetManager $$8 = new EquipmentAssetManager();
        this.resourceManager.registerReloadListener($$8);
        this.itemModelResolver = new ItemModelResolver(this.modelManager);
        this.itemRenderer = new ItemRenderer(this.itemModelResolver);
        this.mapTextureManager = new MapTextureManager(this.textureManager);
        this.mapDecorationTextures = new MapDecorationTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.mapDecorationTextures);
        this.mapRenderer = new MapRenderer(this.mapDecorationTextures, this.mapTextureManager);
        try {
            int $$9 = Runtime.getRuntime().availableProcessors();
            Tesselator.init();
            this.renderBuffers = new RenderBuffers($$9);
        } catch (OutOfMemoryError $$10) {
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)("Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: " + String.valueOf(CommonLinks.GENERAL_HELP)), (CharSequence)"ok", (CharSequence)"error", (boolean)true);
            throw new SilentInitException("Unable to allocate render buffers", $$10);
        }
        this.playerSocialManager = new PlayerSocialManager(this, this.userApiService);
        this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), this.modelManager.specialBlockModelRenderer(), this.blockColors);
        this.resourceManager.registerReloadListener(this.blockRenderer);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this, this.textureManager, this.itemModelResolver, this.itemRenderer, this.mapRenderer, this.blockRenderer, this.font, this.options, this.modelManager.entityModels(), $$8);
        this.resourceManager.registerReloadListener(this.entityRenderDispatcher);
        this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.font, this.modelManager.entityModels(), this.blockRenderer, this.itemModelResolver, this.itemRenderer, this.entityRenderDispatcher);
        this.resourceManager.registerReloadListener(this.blockEntityRenderDispatcher);
        this.particleEngine = new ParticleEngine(this.level, this.textureManager);
        this.resourceManager.registerReloadListener(this.particleEngine);
        this.paintingTextures = new PaintingTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.paintingTextures);
        this.guiSprites = new GuiSpriteManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.guiSprites);
        this.waypointStyles = new WaypointStyleManager();
        this.resourceManager.registerReloadListener(this.waypointStyles);
        this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getItemInHandRenderer(), this.renderBuffers);
        this.levelRenderer = new LevelRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.renderBuffers);
        this.resourceManager.registerReloadListener(this.levelRenderer);
        this.resourceManager.registerReloadListener(this.levelRenderer.getCloudRenderer());
        this.gpuWarnlistManager = new GpuWarnlistManager();
        this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
        this.resourceManager.registerReloadListener(this.regionalCompliancies);
        this.gui = new Gui(this);
        this.debugRenderer = new DebugRenderer(this);
        RealmsClient $$11 = RealmsClient.getOrCreate(this);
        this.realmsDataFetcher = new RealmsDataFetcher($$11);
        RenderSystem.setErrorCallback(this::onFullscreenError);
        if (this.mainRenderTarget.width != this.window.getWidth() || this.mainRenderTarget.height != this.window.getHeight()) {
            StringBuilder $$122 = new StringBuilder("Recovering from unsupported resolution (" + this.window.getWidth() + "x" + this.window.getHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            try {
                GpuDevice $$132 = RenderSystem.getDevice();
                List<String> $$14 = $$132.getLastDebugMessages();
                if (!$$14.isEmpty()) {
                    $$122.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", $$14));
                }
            } catch (Throwable $$132) {
                // empty catch block
            }
            this.window.setWindowed(this.mainRenderTarget.width, this.mainRenderTarget.height);
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)$$122.toString(), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        } else if (this.options.fullscreen().get().booleanValue() && !this.window.isFullscreen()) {
            if ($$5) {
                this.window.toggleFullScreen();
                this.options.fullscreen().set(this.window.isFullscreen());
            } else {
                this.options.fullscreen().set(false);
            }
        }
        this.window.updateVsync(this.options.enableVsync().get());
        this.window.updateRawMouseInput(this.options.rawMouseInput().get());
        this.window.setDefaultErrorCallback();
        this.resizeDisplay();
        this.gameRenderer.preloadUiShader(this.vanillaPackResources.asProvider());
        this.telemetryManager = new ClientTelemetryManager(this, this.userApiService, this.user);
        this.profileKeyPairManager = ProfileKeyPairManager.create(this.userApiService, this.user, $$2);
        this.narrator = new GameNarrator(this);
        this.narrator.checkStatus(this.options.narrator().get() != NarratorStatus.OFF);
        this.chatListener = new ChatListener(this);
        this.chatListener.setMessageDelay(this.options.chatDelay().get());
        this.reportingContext = ReportingContext.create(ReportEnvironment.local(), this.userApiService);
        TitleScreen.registerTextures(this.textureManager);
        LoadingOverlay.registerTextures(this.textureManager);
        this.gameRenderer.getPanorama().registerTextures(this.textureManager);
        this.setScreen(new GenericMessageScreen(Component.translatable("gui.loadingMinecraft")));
        List<PackResources> $$15 = this.resourcePackRepository.openAllSelected();
        this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.INITIAL, $$15);
        ReloadInstance $$16 = this.resourceManager.createReload(Util.backgroundExecutor().forName("resourceLoad"), this, RESOURCE_RELOAD_INITIAL_TASK, $$15);
        GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        GameLoadCookie $$17 = new GameLoadCookie($$11, $$02.quickPlay);
        this.setOverlay(new LoadingOverlay(this, $$16, $$12 -> Util.ifElse($$12, $$1 -> this.rollbackResourcePacks((Throwable)$$1, $$17), () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                this.selfTest();
            }
            this.reloadStateTracker.finishReload();
            this.onResourceLoadFinished($$17);
        }), false));
        this.quickPlayLog = QuickPlayLog.of($$02.quickPlay.logPath());
        this.framerateLimitTracker = new FramerateLimitTracker(this.options, this);
        this.fpsPieProfiler = new ContinuousProfiler(Util.timeSource, () -> this.fpsPieRenderTicks, this.framerateLimitTracker::isHeavilyThrottled);
        this.tracyFrameCapture = TracyClient.isAvailable() && $$02.game.captureTracyImages ? new TracyFrameCapture() : null;
    }

    private void onResourceLoadFinished(@Nullable GameLoadCookie $$0) {
        if (!this.gameLoadFinished) {
            this.gameLoadFinished = true;
            this.onGameLoadFinished($$0);
        }
    }

    private void onGameLoadFinished(@Nullable GameLoadCookie $$0) {
        Runnable $$1 = this.buildInitialScreens($$0);
        GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS);
        GameLoadTimesEvent.INSTANCE.send(this.telemetryManager.getOutsideSessionSender());
        $$1.run();
        this.options.startedCleanly = true;
        this.options.save();
    }

    public boolean isGameLoadFinished() {
        return this.gameLoadFinished;
    }

    private Runnable buildInitialScreens(@Nullable GameLoadCookie $$0) {
        ArrayList<Function<Runnable, Screen>> $$1 = new ArrayList<Function<Runnable, Screen>>();
        boolean $$2 = this.addInitialScreens($$1);
        Runnable $$3 = () -> {
            if ($$0 != null && $$0.quickPlayData.isEnabled()) {
                QuickPlay.connect(this, $$0.quickPlayData.variant(), $$0.realmsClient());
            } else {
                this.setScreen(new TitleScreen(true, new LogoRenderer($$2)));
            }
        };
        for (Function<Runnable, Screen> $$4 : Lists.reverse($$1)) {
            Screen $$5 = $$4.apply($$3);
            $$3 = () -> this.setScreen($$5);
        }
        return $$3;
    }

    private boolean addInitialScreens(List<Function<Runnable, Screen>> $$02) {
        ProfileResult $$3;
        boolean $$13 = false;
        if (this.options.onboardAccessibility) {
            $$02.add($$0 -> new AccessibilityOnboardingScreen(this.options, (Runnable)$$0));
            $$13 = true;
        }
        BanDetails $$2 = this.multiplayerBan();
        if ($$2 != null) {
            $$02.add($$12 -> BanNoticeScreens.create($$1 -> {
                if ($$1) {
                    Util.getPlatform().openUri(CommonLinks.SUSPENSION_HELP);
                }
                $$12.run();
            }, $$2));
        }
        if (($$3 = this.profileFuture.join()) != null) {
            GameProfile $$4 = $$3.profile();
            Set $$5 = $$3.actions();
            if ($$5.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
                $$02.add($$1 -> BanNoticeScreens.createNameBan($$4.getName(), $$1));
            }
            if ($$5.contains(ProfileActionType.USING_BANNED_SKIN)) {
                $$02.add(BanNoticeScreens::createSkinBan);
            }
        }
        return $$13;
    }

    private static boolean countryEqualsISO3(Object $$0) {
        try {
            return Locale.getDefault().getISO3Country().equals($$0);
        } catch (MissingResourceException $$1) {
            return false;
        }
    }

    public void updateTitle() {
        this.window.setTitle(this.createTitle());
    }

    private String createTitle() {
        StringBuilder $$0 = new StringBuilder("Minecraft");
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$0.append("*");
        }
        $$0.append(" ");
        $$0.append(SharedConstants.getCurrentVersion().name());
        ClientPacketListener $$1 = this.getConnection();
        if ($$1 != null && $$1.getConnection().isConnected()) {
            $$0.append(" - ");
            ServerData $$2 = this.getCurrentServer();
            if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
                $$0.append(I18n.a("title.singleplayer", new Object[0]));
            } else if ($$2 != null && $$2.isRealm()) {
                $$0.append(I18n.a("title.multiplayer.realms", new Object[0]));
            } else if (this.singleplayerServer != null || $$2 != null && $$2.isLan()) {
                $$0.append(I18n.a("title.multiplayer.lan", new Object[0]));
            } else {
                $$0.append(I18n.a("title.multiplayer.other", new Object[0]));
            }
        }
        return $$0.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService $$0, GameConfig $$1) {
        if ($$1.user.user.getType() != User.Type.MSA) {
            return UserApiService.OFFLINE;
        }
        return $$0.createUserApiService($$1.user.user.getAccessToken());
    }

    public static ModCheck checkModStatus() {
        return ModCheck.identify("vanilla", ClientBrandRetriever::getClientModName, "Client", Minecraft.class);
    }

    private void rollbackResourcePacks(Throwable $$0, @Nullable GameLoadCookie $$1) {
        if (this.resourcePackRepository.getSelectedIds().size() > 1) {
            this.clearResourcePacksOnError($$0, null, $$1);
        } else {
            Util.throwAsRuntime($$0);
        }
    }

    public void clearResourcePacksOnError(Throwable $$0, @Nullable Component $$1, @Nullable GameLoadCookie $$2) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", $$0);
        this.reloadStateTracker.startRecovery($$0);
        this.downloadedPackSource.onRecovery();
        this.resourcePackRepository.setSelected(Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.save();
        this.reloadResourcePacks(true, $$2).thenRunAsync(() -> this.addResourcePackLoadFailToast($$1), this);
    }

    private void abortResourcePackRecovery() {
        this.setOverlay(null);
        if (this.level != null) {
            this.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
            this.disconnectWithProgressScreen();
        }
        this.setScreen(new TitleScreen());
        this.addResourcePackLoadFailToast(null);
    }

    private void addResourcePackLoadFailToast(@Nullable Component $$0) {
        ToastManager $$1 = this.getToastManager();
        SystemToast.addOrUpdate($$1, SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.translatable("resourcePack.load_fail"), $$0);
    }

    public void triggerResourcePackRecovery(Exception $$0) {
        if (!this.resourcePackRepository.isAbleToClearAnyPack()) {
            if (this.resourcePackRepository.getSelectedIds().size() <= 1) {
                LOGGER.error(LogUtils.FATAL_MARKER, $$0.getMessage(), $$0);
                this.emergencySaveAndCrash(new CrashReport($$0.getMessage(), $$0));
            } else {
                this.schedule(this::abortResourcePackRecovery);
            }
            return;
        }
        this.clearResourcePacksOnError($$0, Component.translatable("resourcePack.runtime_failure"), null);
    }

    public void run() {
        this.gameThread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.gameThread.setPriority(10);
        }
        DiscontinuousFrame $$0 = TracyClient.createDiscontinuousFrame((String)"Client Tick");
        try {
            boolean $$1 = false;
            while (this.running) {
                this.handleDelayedCrash();
                try {
                    SingleTickProfiler $$2 = SingleTickProfiler.createTickProfiler("Renderer");
                    boolean $$3 = this.getDebugOverlay().showProfilerChart();
                    try (Profiler.Scope $$4 = Profiler.use(this.constructProfiler($$3, $$2));){
                        this.metricsRecorder.startTick();
                        $$0.start();
                        this.runTick(!$$1);
                        $$0.end();
                        this.metricsRecorder.endTick();
                    }
                    this.finishProfilers($$3, $$2);
                } catch (OutOfMemoryError $$5) {
                    if ($$1) {
                        throw $$5;
                    }
                    this.emergencySave();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", $$5);
                    $$1 = true;
                }
            }
        } catch (ReportedException $$6) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", $$6);
            this.emergencySaveAndCrash($$6.getReport());
        } catch (Throwable $$7) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", $$7);
            this.emergencySaveAndCrash(new CrashReport("Unexpected error", $$7));
        }
    }

    void updateFontOptions() {
        this.fontManager.updateOptions(this.options);
    }

    private void onFullscreenError(int $$0, long $$1) {
        this.options.enableVsync().set(false);
        this.options.save();
    }

    public RenderTarget getMainRenderTarget() {
        return this.mainRenderTarget;
    }

    public String getLaunchedVersion() {
        return this.launchedVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public void delayCrash(CrashReport $$0) {
        this.delayedCrash = () -> this.fillReport($$0);
    }

    public void delayCrashRaw(CrashReport $$0) {
        this.delayedCrash = () -> $$0;
    }

    private void handleDelayedCrash() {
        if (this.delayedCrash != null) {
            Minecraft.crash(this, this.gameDirectory, this.delayedCrash.get());
        }
    }

    public void emergencySaveAndCrash(CrashReport $$0) {
        MemoryReserve.release();
        CrashReport $$1 = this.fillReport($$0);
        this.emergencySave();
        Minecraft.crash(this, this.gameDirectory, $$1);
    }

    public static int saveReport(File $$0, CrashReport $$1) {
        Path $$2 = $$0.toPath().resolve("crash-reports");
        Path $$3 = $$2.resolve("crash-" + Util.getFilenameFormattedDateTime() + "-client.txt");
        Bootstrap.realStdoutPrintln($$1.getFriendlyReport(ReportType.CRASH));
        if ($$1.getSaveFile() != null) {
            Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf($$1.getSaveFile().toAbsolutePath()));
            return -1;
        }
        if ($$1.saveToFile($$3, ReportType.CRASH)) {
            Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf($$3.toAbsolutePath()));
            return -1;
        }
        Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
        return -2;
    }

    public static void crash(@Nullable Minecraft $$0, File $$1, CrashReport $$2) {
        int $$3 = Minecraft.saveReport($$1, $$2);
        if ($$0 != null) {
            $$0.soundManager.emergencyShutdown();
        }
        System.exit($$3);
    }

    public boolean isEnforceUnicode() {
        return this.options.forceUnicodeFont().get();
    }

    public CompletableFuture<Void> reloadResourcePacks() {
        return this.reloadResourcePacks(false, null);
    }

    private CompletableFuture<Void> reloadResourcePacks(boolean $$0, @Nullable GameLoadCookie $$1) {
        if (this.pendingReload != null) {
            return this.pendingReload;
        }
        CompletableFuture<Void> $$2 = new CompletableFuture<Void>();
        if (!$$0 && this.overlay instanceof LoadingOverlay) {
            this.pendingReload = $$2;
            return $$2;
        }
        this.resourcePackRepository.reload();
        List<PackResources> $$32 = this.resourcePackRepository.openAllSelected();
        if (!$$0) {
            this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.MANUAL, $$32);
        }
        this.setOverlay(new LoadingOverlay(this, this.resourceManager.createReload(Util.backgroundExecutor().forName("resourceLoad"), this, RESOURCE_RELOAD_INITIAL_TASK, $$32), $$3 -> Util.ifElse($$3, $$2 -> {
            if ($$0) {
                this.downloadedPackSource.onRecoveryFailure();
                this.abortResourcePackRecovery();
            } else {
                this.rollbackResourcePacks((Throwable)$$2, $$1);
            }
        }, () -> {
            this.levelRenderer.allChanged();
            this.reloadStateTracker.finishReload();
            this.downloadedPackSource.onReloadSuccess();
            $$2.complete(null);
            this.onResourceLoadFinished($$1);
        }), !$$0));
        return $$2;
    }

    private void selfTest() {
        boolean $$02 = false;
        BlockModelShaper $$1 = this.getBlockRenderer().getBlockModelShaper();
        BlockStateModel $$2 = $$1.getModelManager().getMissingBlockStateModel();
        for (Block $$3 : BuiltInRegistries.BLOCK) {
            for (BlockState $$4 : $$3.getStateDefinition().getPossibleStates()) {
                BlockStateModel $$5;
                if ($$4.getRenderShape() != RenderShape.MODEL || ($$5 = $$1.getBlockModel($$4)) != $$2) continue;
                LOGGER.debug("Missing model for: {}", (Object)$$4);
                $$02 = true;
            }
        }
        TextureAtlasSprite $$6 = $$2.particleIcon();
        for (Block $$7 : BuiltInRegistries.BLOCK) {
            for (BlockState $$8 : $$7.getStateDefinition().getPossibleStates()) {
                TextureAtlasSprite $$9 = $$1.getParticleIcon($$8);
                if ($$8.isAir() || $$9 != $$6) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)$$8);
            }
        }
        BuiltInRegistries.ITEM.listElements().forEach($$0 -> {
            Item $$1 = (Item)$$0.value();
            String $$2 = $$1.getDescriptionId();
            String $$3 = Component.translatable($$2).getString();
            if ($$3.toLowerCase(Locale.ROOT).equals($$1.getDescriptionId())) {
                LOGGER.debug("Missing translation for: {} {} {}", $$0.key().location(), $$2, $$1);
            }
        });
        $$02 |= MenuScreens.selfTest();
        if ($$02 |= EntityRenderers.validateRegistrations()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorageSource getLevelSource() {
        return this.levelSource;
    }

    private void openChatScreen(String $$02) {
        ChatStatus $$1 = this.getChatStatus();
        if (!$$1.isChatAllowed(this.isLocalServer())) {
            if (this.gui.isShowingChatDisabledByPlayer()) {
                this.gui.setChatDisabledByPlayerShown(false);
                this.setScreen(new ConfirmLinkScreen($$0 -> {
                    if ($$0) {
                        Util.getPlatform().openUri(CommonLinks.ACCOUNT_SETTINGS);
                    }
                    this.setScreen(null);
                }, ChatStatus.INFO_DISABLED_BY_PROFILE, CommonLinks.ACCOUNT_SETTINGS, true));
            } else {
                Component $$2 = $$1.getMessage();
                this.gui.setOverlayMessage($$2, false);
                this.narrator.saySystemNow($$2);
                this.gui.setChatDisabledByPlayerShown($$1 == ChatStatus.DISABLED_BY_PROFILE);
            }
        } else {
            this.setScreen(new ChatScreen($$02));
        }
    }

    public void setScreen(@Nullable Screen $$0) {
        if (SharedConstants.IS_RUNNING_IN_IDE && Thread.currentThread() != this.gameThread) {
            LOGGER.error("setScreen called from non-game thread");
        }
        if (this.screen != null) {
            this.screen.removed();
        } else {
            this.setLastInputType(InputType.NONE);
        }
        if ($$0 == null && this.clientLevelTeardownInProgress) {
            throw new IllegalStateException("Trying to return to in-game GUI during disconnection");
        }
        if ($$0 == null && this.level == null) {
            $$0 = new TitleScreen();
        } else if ($$0 == null && this.player.isDeadOrDying()) {
            if (this.player.shouldShowDeathScreen()) {
                $$0 = new DeathScreen(null, this.level.getLevelData().isHardcore());
            } else {
                this.player.respawn();
            }
        }
        this.screen = $$0;
        if (this.screen != null) {
            this.screen.added();
        }
        if ($$0 != null) {
            this.mouseHandler.releaseMouse();
            KeyMapping.releaseAll();
            $$0.init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
            this.noRender = false;
        } else {
            this.soundManager.resume();
            this.mouseHandler.grabMouse();
        }
        this.updateTitle();
    }

    public void setOverlay(@Nullable Overlay $$0) {
        this.overlay = $$0;
    }

    public void destroy() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.narrator.destroy();
            } catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.level != null) {
                    this.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
                }
                this.disconnectWithProgressScreen();
            } catch (Throwable throwable) {
                // empty catch block
            }
            if (this.screen != null) {
                this.screen.removed();
            }
            this.close();
        } finally {
            Util.timeSource = System::nanoTime;
            if (this.delayedCrash == null) {
                System.exit(0);
            }
        }
    }

    @Override
    public void close() {
        if (this.currentFrameProfile != null) {
            this.currentFrameProfile.cancel();
        }
        try {
            this.telemetryManager.close();
            this.regionalCompliancies.close();
            this.modelManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.shaderManager.close();
            this.levelRenderer.close();
            this.soundManager.destroy();
            this.particleEngine.close();
            this.paintingTextures.close();
            this.mapDecorationTextures.close();
            this.guiSprites.close();
            this.mapTextureManager.close();
            this.textureManager.close();
            this.resourceManager.close();
            if (this.tracyFrameCapture != null) {
                this.tracyFrameCapture.close();
            }
            FreeTypeUtil.destroy();
            Util.shutdownExecutors();
            RenderSystem.getDevice().close();
        } catch (Throwable $$0) {
            LOGGER.error("Shutdown failure!", $$0);
            throw $$0;
        } finally {
            this.virtualScreen.close();
            this.window.close();
        }
    }

    private void runTick(boolean $$02) {
        boolean $$8;
        Runnable $$2;
        this.window.setErrorSection("Pre render");
        if (this.window.shouldClose()) {
            this.stop();
        }
        if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
            CompletableFuture<Void> $$1 = this.pendingReload;
            this.pendingReload = null;
            this.reloadResourcePacks().thenRun(() -> $$1.complete(null));
        }
        while (($$2 = this.progressTasks.poll()) != null) {
            $$2.run();
        }
        int $$3 = this.deltaTracker.advanceTime(Util.getMillis(), $$02);
        ProfilerFiller $$4 = Profiler.get();
        if ($$02) {
            $$4.push("scheduledExecutables");
            this.runAllTasks();
            $$4.pop();
            $$4.push("tick");
            for (int $$5 = 0; $$5 < Math.min(10, $$3); ++$$5) {
                $$4.incrementCounter("clientTick");
                this.tick();
            }
            $$4.pop();
        }
        this.window.setErrorSection("Render");
        $$4.push("gpuAsync");
        RenderSystem.executePendingTasks();
        $$4.popPush("sound");
        this.soundManager.updateSource(this.gameRenderer.getMainCamera());
        $$4.popPush("toasts");
        this.toastManager.update();
        $$4.popPush("render");
        long $$6 = Util.getNanos();
        if (this.getDebugOverlay().showDebugScreen() || this.metricsRecorder.isRecording()) {
            boolean $$7;
            boolean bl = $$7 = this.currentFrameProfile == null || this.currentFrameProfile.isDone();
            if ($$7) {
                TimerQuery.getInstance().ifPresent(TimerQuery::beginProfile);
            }
        } else {
            $$8 = false;
            this.gpuUtilization = 0.0;
        }
        RenderTarget $$9 = this.getMainRenderTarget();
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures($$9.getColorTexture(), 0, $$9.getDepthTexture(), 1.0);
        $$4.push("mouse");
        this.mouseHandler.handleAccumulatedMovement();
        $$4.pop();
        if (!this.noRender) {
            $$4.popPush("gameRenderer");
            this.gameRenderer.render(this.deltaTracker, $$02);
            $$4.pop();
        }
        $$4.push("blit");
        if (!this.window.isMinimized()) {
            $$9.blitToScreen();
        }
        this.frameTimeNs = Util.getNanos() - $$6;
        if ($$8) {
            TimerQuery.getInstance().ifPresent($$0 -> {
                this.currentFrameProfile = $$0.endProfile();
            });
        }
        $$4.popPush("updateDisplay");
        if (this.tracyFrameCapture != null) {
            this.tracyFrameCapture.upload();
            this.tracyFrameCapture.capture($$9);
        }
        this.window.updateDisplay(this.tracyFrameCapture);
        int $$10 = this.framerateLimitTracker.getFramerateLimit();
        if ($$10 < 260) {
            RenderSystem.limitDisplayFPS($$10);
        }
        $$4.popPush("yield");
        Thread.yield();
        $$4.pop();
        this.window.setErrorSection("Post render");
        ++this.frames;
        boolean $$11 = this.pause;
        boolean bl = this.pause = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
        if (!$$11 && this.pause) {
            this.soundManager.a(SoundSource.MUSIC, SoundSource.UI);
        }
        this.deltaTracker.updatePauseState(this.pause);
        this.deltaTracker.updateFrozenState(!this.isLevelRunningNormally());
        long $$12 = Util.getNanos();
        long $$13 = $$12 - this.lastNanoTime;
        if ($$8) {
            this.savedCpuDuration = $$13;
        }
        this.getDebugOverlay().logFrameDuration($$13);
        this.lastNanoTime = $$12;
        $$4.push("fpsUpdate");
        if (this.currentFrameProfile != null && this.currentFrameProfile.isDone()) {
            this.gpuUtilization = (double)this.currentFrameProfile.get() * 100.0 / (double)this.savedCpuDuration;
        }
        while (Util.getMillis() >= this.lastTime + 1000L) {
            String $$15;
            if (this.gpuUtilization > 0.0) {
                String $$14 = " GPU: " + (this.gpuUtilization > 100.0 ? String.valueOf(ChatFormatting.RED) + "100%" : Math.round(this.gpuUtilization) + "%");
            } else {
                $$15 = "";
            }
            fps = this.frames;
            this.fpsString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", fps, $$10 == 260 ? "inf" : Integer.valueOf($$10), this.options.enableVsync().get() != false ? " vsync " : " ", this.options.graphicsMode().get(), this.options.cloudStatus().get() == CloudStatus.OFF ? "" : (this.options.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius().get(), $$15);
            this.lastTime += 1000L;
            this.frames = 0;
        }
        $$4.pop();
    }

    private ProfilerFiller constructProfiler(boolean $$0, @Nullable SingleTickProfiler $$1) {
        ProfilerFiller $$3;
        if (!$$0) {
            this.fpsPieProfiler.disable();
            if (!this.metricsRecorder.isRecording() && $$1 == null) {
                return InactiveProfiler.INSTANCE;
            }
        }
        if ($$0) {
            if (!this.fpsPieProfiler.isEnabled()) {
                this.fpsPieRenderTicks = 0;
                this.fpsPieProfiler.enable();
            }
            ++this.fpsPieRenderTicks;
            ProfilerFiller $$2 = this.fpsPieProfiler.getFiller();
        } else {
            $$3 = InactiveProfiler.INSTANCE;
        }
        if (this.metricsRecorder.isRecording()) {
            $$3 = ProfilerFiller.combine($$3, this.metricsRecorder.getProfiler());
        }
        return SingleTickProfiler.decorateFiller($$3, $$1);
    }

    private void finishProfilers(boolean $$0, @Nullable SingleTickProfiler $$1) {
        if ($$1 != null) {
            $$1.endTick();
        }
        ProfilerPieChart $$2 = this.getDebugOverlay().getProfilerPieChart();
        if ($$0) {
            $$2.setPieChartResults(this.fpsPieProfiler.getResults());
        } else {
            $$2.setPieChartResults(null);
        }
    }

    @Override
    public void resizeDisplay() {
        int $$0 = this.window.calculateScale(this.options.guiScale().get(), this.isEnforceUnicode());
        this.window.setGuiScale($$0);
        if (this.screen != null) {
            this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
        }
        RenderTarget $$1 = this.getMainRenderTarget();
        $$1.resize(this.window.getWidth(), this.window.getHeight());
        this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
        this.mouseHandler.setIgnoreFirstMove();
    }

    @Override
    public void cursorEntered() {
        this.mouseHandler.cursorEntered();
    }

    public int getFps() {
        return fps;
    }

    public long getFrameTimeNs() {
        return this.frameTimeNs;
    }

    private void emergencySave() {
        MemoryReserve.release();
        try {
            if (this.isLocalServer && this.singleplayerServer != null) {
                this.singleplayerServer.halt(true);
            }
            this.disconnectWithSavingScreen();
        } catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean debugClientMetricsStart(Consumer<Component> $$02) {
        Consumer<Path> $$8;
        if (this.metricsRecorder.isRecording()) {
            this.debugClientMetricsStop();
            return false;
        }
        Consumer<ProfileResults> $$13 = $$1 -> {
            if ($$1 == EmptyProfileResults.EMPTY) {
                return;
            }
            int $$2 = $$1.getTickDuration();
            double $$3 = (double)$$1.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
            this.execute(() -> $$02.accept(Component.a("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", $$3), $$2, String.format(Locale.ROOT, "%.2f", (double)$$2 / $$3)})));
        };
        Consumer<Path> $$22 = $$12 -> {
            MutableComponent $$2 = Component.literal($$12.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle($$1 -> $$1.withClickEvent(new ClickEvent.OpenFile($$12.getParent())));
            this.execute(() -> $$02.accept(Component.a("debug.profiling.stop", $$2)));
        };
        SystemReport $$3 = Minecraft.fillSystemReport(new SystemReport(), this, this.languageManager, this.launchedVersion, this.options);
        Consumer<List> $$4 = $$2 -> {
            Path $$3 = this.archiveProfilingReport($$3, (List<Path>)$$2);
            $$22.accept($$3);
        };
        if (this.singleplayerServer == null) {
            Consumer<Path> $$5 = $$1 -> $$4.accept(ImmutableList.of($$1));
        } else {
            this.singleplayerServer.fillSystemReport($$3);
            CompletableFuture $$6 = new CompletableFuture();
            CompletableFuture $$7 = new CompletableFuture();
            CompletableFuture.allOf($$6, $$7).thenRunAsync(() -> $$4.accept(ImmutableList.of((Path)$$6.join(), (Path)$$7.join())), Util.ioPool());
            this.singleplayerServer.startRecordingMetrics($$0 -> {}, $$7::complete);
            $$8 = $$6::complete;
        }
        this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ClientMetricsSamplersProvider(Util.timeSource, this.levelRenderer), Util.timeSource, Util.ioPool(), new MetricsPersister("client"), $$1 -> {
            this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
            $$13.accept((ProfileResults)$$1);
        }, $$8);
        return true;
    }

    private void debugClientMetricsStop() {
        this.metricsRecorder.end();
        if (this.singleplayerServer != null) {
            this.singleplayerServer.finishRecordingMetrics();
        }
    }

    private void debugClientMetricsCancel() {
        this.metricsRecorder.cancel();
        if (this.singleplayerServer != null) {
            this.singleplayerServer.cancelRecordingMetrics();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private Path archiveProfilingReport(SystemReport $$0, List<Path> $$1) {
        void $$9;
        String $$4;
        if (this.isLocalServer()) {
            String $$2 = this.getSingleplayerServer().getWorldData().getLevelName();
        } else {
            ServerData $$3 = this.getCurrentServer();
            $$4 = $$3 != null ? $$3.name : "unknown";
        }
        try {
            String $$5 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFilenameFormattedDateTime(), $$4, SharedConstants.getCurrentVersion().id());
            String $$6 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, $$5, ".zip");
            Path $$7 = MetricsPersister.PROFILING_RESULTS_DIR.resolve($$6);
        } catch (IOException $$8) {
            throw new UncheckedIOException($$8);
        }
        try (FileZipper $$10 = new FileZipper((Path)$$9);){
            $$10.add(Paths.get("system.txt", new String[0]), $$0.toLineSeparatedString());
            $$10.add(Paths.get("client", new String[0]).resolve(this.options.getFile().getName()), this.options.dumpOptionsForReport());
            $$1.forEach($$10::add);
        } finally {
            for (Path $$11 : $$1) {
                try {
                    FileUtils.forceDelete((File)$$11.toFile());
                } catch (IOException $$12) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)$$11, (Object)$$12);
                }
            }
        }
        return $$9;
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void pauseGame(boolean $$0) {
        boolean $$1;
        if (this.screen != null) {
            return;
        }
        boolean bl = $$1 = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
        if ($$1) {
            this.setScreen(new PauseScreen(!$$0));
        } else {
            this.setScreen(new PauseScreen(true));
        }
    }

    private void continueAttack(boolean $$0) {
        if (!$$0) {
            this.missTime = 0;
        }
        if (this.missTime > 0 || this.player.isUsingItem()) {
            return;
        }
        if ($$0 && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            Direction $$3;
            BlockHitResult $$1 = (BlockHitResult)this.hitResult;
            BlockPos $$2 = $$1.getBlockPos();
            if (!this.level.getBlockState($$2).isAir() && this.gameMode.continueDestroyBlock($$2, $$3 = $$1.getDirection())) {
                this.particleEngine.crack($$2, $$3);
                this.player.swing(InteractionHand.MAIN_HAND);
            }
            return;
        }
        this.gameMode.stopDestroyBlock();
    }

    private boolean startAttack() {
        if (this.missTime > 0) {
            return false;
        }
        if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
                this.missTime = 10;
            }
            return false;
        }
        if (this.player.isHandsBusy()) {
            return false;
        }
        ItemStack $$0 = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!$$0.isItemEnabled(this.level.enabledFeatures())) {
            return false;
        }
        boolean $$1 = false;
        switch (this.hitResult.getType()) {
            case ENTITY: {
                this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult $$2 = (BlockHitResult)this.hitResult;
                BlockPos $$3 = $$2.getBlockPos();
                if (!this.level.getBlockState($$3).isAir()) {
                    this.gameMode.startDestroyBlock($$3, $$2.getDirection());
                    if (!this.level.getBlockState($$3).isAir()) break;
                    $$1 = true;
                    break;
                }
            }
            case MISS: {
                if (this.gameMode.hasMissTime()) {
                    this.missTime = 10;
                }
                this.player.resetAttackStrengthTicker();
            }
        }
        this.player.swing(InteractionHand.MAIN_HAND);
        return $$1;
    }

    private void startUseItem() {
        if (this.gameMode.isDestroying()) {
            return;
        }
        this.rightClickDelay = 4;
        if (this.player.isHandsBusy()) {
            return;
        }
        if (this.hitResult == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (InteractionHand $$0 : InteractionHand.values()) {
            InteractionResult $$10;
            ItemStack $$1 = this.player.getItemInHand($$0);
            if (!$$1.isItemEnabled(this.level.enabledFeatures())) {
                return;
            }
            if (this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY: {
                        EntityHitResult $$2 = (EntityHitResult)this.hitResult;
                        Entity $$3 = $$2.getEntity();
                        if (!this.level.getWorldBorder().isWithinBounds($$3.blockPosition())) {
                            return;
                        }
                        InteractionResult $$4 = this.gameMode.interactAt(this.player, $$3, $$2, $$0);
                        if (!$$4.consumesAction()) {
                            $$4 = this.gameMode.interact(this.player, $$3, $$0);
                        }
                        if (!($$4 instanceof InteractionResult.Success)) break;
                        InteractionResult.Success $$5 = (InteractionResult.Success)$$4;
                        if ($$5.swingSource() == InteractionResult.SwingSource.CLIENT) {
                            this.player.swing($$0);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult $$6 = (BlockHitResult)this.hitResult;
                        int $$7 = $$1.getCount();
                        InteractionResult $$8 = this.gameMode.useItemOn(this.player, $$0, $$6);
                        if ($$8 instanceof InteractionResult.Success) {
                            InteractionResult.Success $$9 = (InteractionResult.Success)$$8;
                            if ($$9.swingSource() == InteractionResult.SwingSource.CLIENT) {
                                this.player.swing($$0);
                                if (!$$1.isEmpty() && ($$1.getCount() != $$7 || this.player.hasInfiniteMaterials())) {
                                    this.gameRenderer.itemInHandRenderer.itemUsed($$0);
                                }
                            }
                            return;
                        }
                        if (!($$8 instanceof InteractionResult.Fail)) break;
                        return;
                    }
                }
            }
            if ($$1.isEmpty() || !(($$10 = this.gameMode.useItem(this.player, $$0)) instanceof InteractionResult.Success)) continue;
            InteractionResult.Success $$11 = (InteractionResult.Success)$$10;
            if ($$11.swingSource() == InteractionResult.SwingSource.CLIENT) {
                this.player.swing($$0);
            }
            this.gameRenderer.itemInHandRenderer.itemUsed($$0);
            return;
        }
    }

    public MusicManager getMusicManager() {
        return this.musicManager;
    }

    public void tick() {
        ++this.clientTickCount;
        if (this.level != null && !this.pause) {
            this.level.tickRateManager().tick();
        }
        if (this.rightClickDelay > 0) {
            --this.rightClickDelay;
        }
        ProfilerFiller $$0 = Profiler.get();
        $$0.push("gui");
        this.chatListener.tick();
        this.gui.tick(this.pause);
        $$0.pop();
        this.gameRenderer.pick(1.0f);
        this.tutorial.onLookAt(this.level, this.hitResult);
        $$0.push("gameMode");
        if (!this.pause && this.level != null) {
            this.gameMode.tick();
        }
        $$0.popPush("textures");
        if (this.isLevelRunningNormally()) {
            this.textureManager.tick();
        }
        if (this.screen == null && this.player != null) {
            if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.level != null) {
                this.setScreen(new InBedChatScreen());
            }
        } else {
            Screen screen = this.screen;
            if (screen instanceof InBedChatScreen) {
                InBedChatScreen $$1 = (InBedChatScreen)screen;
                if (!this.player.isSleeping()) {
                    $$1.onPlayerWokeUp();
                }
            }
        }
        if (this.screen != null) {
            this.missTime = 10000;
        }
        if (this.screen != null) {
            try {
                this.screen.tick();
            } catch (Throwable $$2) {
                CrashReport $$3 = CrashReport.forThrowable($$2, "Ticking screen");
                this.screen.fillCrashDetails($$3);
                throw new ReportedException($$3);
            }
        }
        if (!this.getDebugOverlay().showDebugScreen()) {
            this.gui.clearCache();
        }
        if (this.overlay == null && this.screen == null) {
            $$0.popPush("Keybindings");
            this.handleKeybinds();
            if (this.missTime > 0) {
                --this.missTime;
            }
        }
        if (this.level != null) {
            $$0.popPush("gameRenderer");
            if (!this.pause) {
                this.gameRenderer.tick();
            }
            $$0.popPush("levelRenderer");
            if (!this.pause) {
                this.levelRenderer.tick();
            }
            $$0.popPush("level");
            if (!this.pause) {
                this.level.tickEntities();
            }
        } else if (this.gameRenderer.currentPostEffect() != null) {
            this.gameRenderer.clearPostEffect();
        }
        this.musicManager.tick();
        this.soundManager.tick(this.pause);
        if (this.level != null) {
            ClientPacketListener $$9;
            if (!this.pause) {
                if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
                    MutableComponent $$4 = Component.translatable("tutorial.socialInteractions.title");
                    MutableComponent $$5 = Component.a("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
                    this.socialInteractionsToast = new TutorialToast(this.font, TutorialToast.Icons.SOCIAL_INTERACTIONS, $$4, $$5, true, 8000);
                    this.toastManager.addToast(this.socialInteractionsToast);
                    this.options.joinedFirstServer = true;
                    this.options.save();
                }
                this.tutorial.tick();
                try {
                    this.level.tick(() -> true);
                } catch (Throwable $$6) {
                    CrashReport $$7 = CrashReport.forThrowable($$6, "Exception in world tick");
                    if (this.level == null) {
                        CrashReportCategory $$8 = $$7.addCategory("Affected level");
                        $$8.setDetail("Problem", "Level is null!");
                    } else {
                        this.level.fillReportDetails($$7);
                    }
                    throw new ReportedException($$7);
                }
            }
            $$0.popPush("animateTick");
            if (!this.pause && this.isLevelRunningNormally()) {
                this.level.animateTick(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            $$0.popPush("particles");
            if (!this.pause && this.isLevelRunningNormally()) {
                this.particleEngine.tick();
            }
            if (($$9 = this.getConnection()) != null && !this.pause) {
                $$9.send(ServerboundClientTickEndPacket.INSTANCE);
            }
        } else if (this.pendingConnection != null) {
            $$0.popPush("pendingConnection");
            this.pendingConnection.tick();
        }
        $$0.popPush("keyboard");
        this.keyboardHandler.tick();
        $$0.pop();
    }

    private boolean isLevelRunningNormally() {
        return this.level == null || this.level.tickRateManager().runsNormally();
    }

    private boolean isMultiplayerServer() {
        return !this.isLocalServer || this.singleplayerServer != null && this.singleplayerServer.isPublished();
    }

    private void handleKeybinds() {
        while (this.options.keyTogglePerspective.consumeClick()) {
            CameraType $$02 = this.options.getCameraType();
            this.options.setCameraType(this.options.getCameraType().cycle());
            if ($$02.isFirstPerson() != this.options.getCameraType().isFirstPerson()) {
                this.gameRenderer.checkEntityPostEffect(this.options.getCameraType().isFirstPerson() ? this.getCameraEntity() : null);
            }
            this.levelRenderer.needsUpdate();
        }
        while (this.options.keySmoothCamera.consumeClick()) {
            this.options.smoothCamera = !this.options.smoothCamera;
        }
        for (int $$1 = 0; $$1 < 9; ++$$1) {
            boolean $$2 = this.options.keySaveHotbarActivator.isDown();
            boolean $$3 = this.options.keyLoadHotbarActivator.isDown();
            if (!this.options.keyHotbarSlots[$$1].consumeClick()) continue;
            if (this.player.isSpectator()) {
                this.gui.getSpectatorGui().onHotbarSelected($$1);
                continue;
            }
            if (this.player.hasInfiniteMaterials() && this.screen == null && ($$3 || $$2)) {
                CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, $$1, $$3, $$2);
                continue;
            }
            this.player.getInventory().setSelectedSlot($$1);
        }
        while (this.options.keySocialInteractions.consumeClick()) {
            if (!this.isMultiplayerServer()) {
                this.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                this.narrator.saySystemNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.socialInteractionsToast.hide();
                this.socialInteractionsToast = null;
            }
            this.setScreen(new SocialInteractionsScreen());
        }
        while (this.options.keyInventory.consumeClick()) {
            if (this.gameMode.isServerControlledInventory()) {
                this.player.sendOpenInventory();
                continue;
            }
            this.tutorial.onOpenInventory();
            this.setScreen(new InventoryScreen(this.player));
        }
        while (this.options.keyAdvancements.consumeClick()) {
            this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
        }
        while (this.options.keyQuickActions.consumeClick()) {
            this.getQuickActionsDialog().ifPresent($$0 -> this.player.connection.showDialog((Holder<Dialog>)$$0, this.screen));
        }
        while (this.options.keySwapOffhand.consumeClick()) {
            if (this.player.isSpectator()) continue;
            this.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
        }
        while (this.options.keyDrop.consumeClick()) {
            if (this.player.isSpectator() || !this.player.drop(Screen.hasControlDown())) continue;
            this.player.swing(InteractionHand.MAIN_HAND);
        }
        while (this.options.keyChat.consumeClick()) {
            this.openChatScreen("");
        }
        if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
            this.openChatScreen("/");
        }
        boolean $$4 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isDown()) {
                this.gameMode.releaseUsingItem(this.player);
            }
            while (this.options.keyAttack.consumeClick()) {
            }
            while (this.options.keyUse.consumeClick()) {
            }
            while (this.options.keyPickItem.consumeClick()) {
            }
        } else {
            while (this.options.keyAttack.consumeClick()) {
                $$4 |= this.startAttack();
            }
            while (this.options.keyUse.consumeClick()) {
                this.startUseItem();
            }
            while (this.options.keyPickItem.consumeClick()) {
                this.pickBlock();
            }
        }
        if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
            this.startUseItem();
        }
        this.continueAttack(this.screen == null && !$$4 && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
    }

    private Optional<Holder<Dialog>> getQuickActionsDialog() {
        HolderLookup.RegistryLookup $$0 = this.player.connection.registryAccess().lookupOrThrow(Registries.DIALOG);
        return $$0.get(DialogTags.QUICK_ACTIONS).flatMap(arg_0 -> Minecraft.lambda$getQuickActionsDialog$36((Registry)$$0, arg_0));
    }

    public ClientTelemetryManager getTelemetryManager() {
        return this.telemetryManager;
    }

    public double getGpuUtilization() {
        return this.gpuUtilization;
    }

    public ProfileKeyPairManager getProfileKeyPairManager() {
        return this.profileKeyPairManager;
    }

    public WorldOpenFlows createWorldOpenFlows() {
        return new WorldOpenFlows(this, this.levelSource);
    }

    public void doWorldLoad(LevelStorageSource.LevelStorageAccess $$02, PackRepository $$1, WorldStem $$2, boolean $$3) {
        this.disconnectWithProgressScreen();
        this.progressListener.set(null);
        Instant $$42 = Instant.now();
        try {
            $$02.saveDataTag($$2.registries().compositeAccess(), $$2.worldData());
            Services $$5 = Services.create(this.authenticationService, this.gameDirectory);
            $$5.profileCache().setExecutor(this);
            SkullBlockEntity.setup($$5, this);
            GameProfileCache.setUsesAuthentication(false);
            this.singleplayerServer = MinecraftServer.spin($$4 -> new IntegratedServer((Thread)$$4, this, $$02, $$1, $$2, $$5, $$0 -> {
                PackRepository $$1 = StoringChunkProgressListener.createFromGameruleRadius($$0 + 0);
                this.progressListener.set((StoringChunkProgressListener)((Object)$$1));
                return ProcessorChunkProgressListener.createStarted($$1, this.progressTasks::add);
            }));
            this.isLocalServer = true;
            this.updateReportEnvironment(ReportEnvironment.local());
            this.quickPlayLog.setWorldData(QuickPlayLog.Type.SINGLEPLAYER, $$02.getLevelId(), $$2.worldData().getLevelName());
        } catch (Throwable $$6) {
            CrashReport $$7 = CrashReport.forThrowable($$6, "Starting integrated server");
            CrashReportCategory $$8 = $$7.addCategory("Starting integrated server");
            $$8.setDetail("Level ID", $$02.getLevelId());
            $$8.setDetail("Level Name", () -> $$2.worldData().getLevelName());
            throw new ReportedException($$7);
        }
        while (this.progressListener.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen $$9 = new LevelLoadingScreen(this.progressListener.get());
        ProfilerFiller $$10 = Profiler.get();
        this.setScreen($$9);
        $$10.push("waitForServer");
        while (!this.singleplayerServer.isReady() || this.overlay != null) {
            $$9.tick();
            this.runTick(false);
            try {
                Thread.sleep(16L);
            } catch (InterruptedException $$8) {
                // empty catch block
            }
            this.handleDelayedCrash();
        }
        GameTestTicker.SINGLETON.startTicking();
        $$10.pop();
        Duration $$11 = Duration.between($$42, Instant.now());
        SocketAddress $$12 = this.singleplayerServer.getConnection().startMemoryChannel();
        Connection $$13 = Connection.connectToLocalServer($$12);
        $$13.initiateServerboundPlayConnection($$12.toString(), 0, new ClientHandshakePacketListenerImpl($$13, this, null, null, $$3, $$11, $$0 -> {}, null));
        $$13.send(new ServerboundHelloPacket(this.getUser().getName(), this.getUser().getProfileId()));
        this.pendingConnection = $$13;
    }

    public void setLevel(ClientLevel $$0, ReceivingLevelScreen.Reason $$1) {
        this.updateScreenAndTick(new ReceivingLevelScreen(() -> false, $$1));
        this.level = $$0;
        this.updateLevelInEngines($$0);
        if (!this.isLocalServer) {
            Services $$2 = Services.create(this.authenticationService, this.gameDirectory);
            $$2.profileCache().setExecutor(this);
            SkullBlockEntity.setup($$2, this);
            GameProfileCache.setUsesAuthentication(false);
        }
    }

    public void disconnectWithSavingScreen() {
        this.disconnect(new GenericMessageScreen(SAVING_LEVEL), false);
    }

    public void disconnectWithProgressScreen() {
        this.disconnect(new ProgressScreen(true), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnect(Screen $$0, boolean $$1) {
        ClientPacketListener $$2 = this.getConnection();
        if ($$2 != null) {
            this.dropAllTasks();
            $$2.close();
            if (!$$1) {
                this.clearDownloadedResourcePacks();
            }
        }
        this.playerSocialManager.stopOnlineMode();
        if (this.metricsRecorder.isRecording()) {
            this.debugClientMetricsCancel();
        }
        IntegratedServer $$3 = this.singleplayerServer;
        this.singleplayerServer = null;
        this.gameRenderer.resetData();
        this.gameMode = null;
        this.narrator.clear();
        this.clientLevelTeardownInProgress = true;
        try {
            this.updateScreenAndTick($$0);
            if (this.level != null) {
                if ($$3 != null) {
                    ProfilerFiller $$4 = Profiler.get();
                    $$4.push("waitForServer");
                    while (!$$3.isShutdown()) {
                        this.runTick(false);
                    }
                    $$4.pop();
                }
                this.gui.onDisconnected();
                this.isLocalServer = false;
            }
            this.level = null;
            this.updateLevelInEngines(null);
            this.player = null;
        } finally {
            this.clientLevelTeardownInProgress = false;
        }
        SkullBlockEntity.clear();
    }

    public void clearDownloadedResourcePacks() {
        this.downloadedPackSource.cleanupAfterDisconnect();
        this.runAllTasks();
    }

    public void clearClientLevel(Screen $$0) {
        ClientPacketListener $$1 = this.getConnection();
        if ($$1 != null) {
            $$1.clearLevel();
        }
        if (this.metricsRecorder.isRecording()) {
            this.debugClientMetricsCancel();
        }
        this.gameRenderer.resetData();
        this.gameMode = null;
        this.narrator.clear();
        this.clientLevelTeardownInProgress = true;
        try {
            this.updateScreenAndTick($$0);
            this.gui.onDisconnected();
            this.level = null;
            this.updateLevelInEngines(null);
            this.player = null;
        } finally {
            this.clientLevelTeardownInProgress = false;
        }
        SkullBlockEntity.clear();
    }

    private void updateScreenAndTick(Screen $$0) {
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("forcedTick");
        this.soundManager.stop();
        this.cameraEntity = null;
        this.pendingConnection = null;
        this.setScreen($$0);
        this.runTick(false);
        $$1.pop();
    }

    public void forceSetScreen(Screen $$0) {
        try (Zone $$1 = Profiler.get().zone("forcedTick");){
            this.setScreen($$0);
            this.runTick(false);
        }
    }

    private void updateLevelInEngines(@Nullable ClientLevel $$0) {
        this.levelRenderer.setLevel($$0);
        this.particleEngine.setLevel($$0);
        this.blockEntityRenderDispatcher.setLevel($$0);
        this.gameRenderer.setLevel($$0);
        this.updateTitle();
    }

    private UserApiService.UserProperties userProperties() {
        return this.userPropertiesFuture.join();
    }

    public boolean telemetryOptInExtra() {
        return this.extraTelemetryAvailable() && this.options.telemetryOptInExtra().get() != false;
    }

    public boolean extraTelemetryAvailable() {
        return this.allowsTelemetry() && this.userProperties().flag(UserApiService.UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
    }

    public boolean allowsTelemetry() {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return false;
        }
        return this.userProperties().flag(UserApiService.UserFlag.TELEMETRY_ENABLED);
    }

    public boolean allowsMultiplayer() {
        return this.allowsMultiplayer && this.userProperties().flag(UserApiService.UserFlag.SERVERS_ALLOWED) && this.multiplayerBan() == null && !this.isNameBanned();
    }

    public boolean allowsRealms() {
        return this.userProperties().flag(UserApiService.UserFlag.REALMS_ALLOWED) && this.multiplayerBan() == null;
    }

    @Nullable
    public BanDetails multiplayerBan() {
        return (BanDetails)this.userProperties().bannedScopes().get("MULTIPLAYER");
    }

    public boolean isNameBanned() {
        ProfileResult $$0 = this.profileFuture.getNow(null);
        return $$0 != null && $$0.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
    }

    public boolean isBlocked(UUID $$0) {
        if (!this.getChatStatus().isChatAllowed(false)) {
            return (this.player == null || !$$0.equals(this.player.getUUID())) && !$$0.equals(Util.NIL_UUID);
        }
        return this.playerSocialManager.shouldHideMessageFrom($$0);
    }

    public ChatStatus getChatStatus() {
        if (this.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
            return ChatStatus.DISABLED_BY_OPTIONS;
        }
        if (!this.allowsChat) {
            return ChatStatus.DISABLED_BY_LAUNCHER;
        }
        if (!this.userProperties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatStatus.DISABLED_BY_PROFILE;
        }
        return ChatStatus.ENABLED;
    }

    public final boolean isDemo() {
        return this.demo;
    }

    @Nullable
    public ClientPacketListener getConnection() {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean renderNames() {
        return !Minecraft.instance.options.hideGui;
    }

    public static boolean useFancyGraphics() {
        return Minecraft.instance.options.graphicsMode().get().getId() >= GraphicsStatus.FANCY.getId();
    }

    public static boolean useShaderTransparency() {
        return !Minecraft.instance.gameRenderer.isPanoramicMode() && Minecraft.instance.options.graphicsMode().get().getId() >= GraphicsStatus.FABULOUS.getId();
    }

    public static boolean useAmbientOcclusion() {
        return Minecraft.instance.options.ambientOcclusion().get();
    }

    private void pickBlock() {
        if (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean $$0 = Screen.hasControlDown();
        HitResult hitResult = this.hitResult;
        Objects.requireNonNull(hitResult);
        HitResult hitResult2 = hitResult;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BlockHitResult.class, EntityHitResult.class}, (Object)hitResult2, (int)n)) {
            case 0: {
                BlockHitResult $$1 = (BlockHitResult)hitResult2;
                this.gameMode.handlePickItemFromBlock($$1.getBlockPos(), $$0);
                break;
            }
            case 1: {
                EntityHitResult $$2 = (EntityHitResult)hitResult2;
                this.gameMode.handlePickItemFromEntity($$2.getEntity(), $$0);
                break;
            }
        }
    }

    public CrashReport fillReport(CrashReport $$0) {
        SystemReport $$1 = $$0.getSystemReport();
        try {
            Minecraft.fillSystemReport($$1, this, this.languageManager, this.launchedVersion, this.options);
            this.fillUptime($$0.addCategory("Uptime"));
            if (this.level != null) {
                this.level.fillReportDetails($$0);
            }
            if (this.singleplayerServer != null) {
                this.singleplayerServer.fillSystemReport($$1);
            }
            this.reloadStateTracker.fillCrashReport($$0);
        } catch (Throwable $$2) {
            LOGGER.error("Failed to collect details", $$2);
        }
        return $$0;
    }

    public static void fillReport(@Nullable Minecraft $$0, @Nullable LanguageManager $$1, String $$2, @Nullable Options $$3, CrashReport $$4) {
        SystemReport $$5 = $$4.getSystemReport();
        Minecraft.fillSystemReport($$5, $$0, $$1, $$2, $$3);
    }

    private static String formatSeconds(double $$0) {
        return String.format(Locale.ROOT, "%.3fs", $$0);
    }

    private void fillUptime(CrashReportCategory $$0) {
        $$0.setDetail("JVM uptime", () -> Minecraft.formatSeconds((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0));
        $$0.setDetail("Wall uptime", () -> Minecraft.formatSeconds((double)(System.currentTimeMillis() - this.clientStartTimeMs) / 1000.0));
        $$0.setDetail("High-res time", () -> Minecraft.formatSeconds((double)Util.getMillis() / 1000.0));
        $$0.setDetail("Client ticks", () -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.clientTickCount, (double)this.clientTickCount / 20.0));
    }

    private static SystemReport fillSystemReport(SystemReport $$0, @Nullable Minecraft $$1, @Nullable LanguageManager $$2, String $$3, @Nullable Options $$4) {
        $$0.setDetail("Launched Version", () -> $$3);
        String $$5 = Minecraft.getLauncherBrand();
        if ($$5 != null) {
            $$0.setDetail("Launcher name", $$5);
        }
        $$0.setDetail("Backend library", RenderSystem::getBackendDescription);
        $$0.setDetail("Backend API", RenderSystem::getApiDescription);
        $$0.setDetail("Window size", () -> $$1 != null ? $$0.window.getWidth() + "x" + $$0.window.getHeight() : "<not initialized>");
        $$0.setDetail("GFLW Platform", Window::getPlatform);
        $$0.setDetail("Render Extensions", () -> String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        $$0.setDetail("GL debug messages", () -> {
            GpuDevice $$0 = RenderSystem.tryGetDevice();
            if ($$0 == null) {
                return "<no renderer available>";
            }
            if ($$0.isDebuggingEnabled()) {
                return String.join((CharSequence)"\n", $$0.getLastDebugMessages());
            }
            return "<debugging unavailable>";
        });
        $$0.setDetail("Is Modded", () -> Minecraft.checkModStatus().fullDescription());
        $$0.setDetail("Universe", () -> $$1 != null ? Long.toHexString($$0.canary) : "404");
        $$0.setDetail("Type", "Client (map_client.txt)");
        if ($$4 != null) {
            String $$6;
            if ($$1 != null && ($$6 = $$1.getGpuWarnlistManager().getAllWarnings()) != null) {
                $$0.setDetail("GPU Warnings", $$6);
            }
            $$0.setDetail("Graphics mode", $$4.graphicsMode().get().toString());
            $$0.setDetail("Render Distance", $$4.getEffectiveRenderDistance() + "/" + String.valueOf($$4.renderDistance().get()) + " chunks");
        }
        if ($$1 != null) {
            $$0.setDetail("Resource Packs", () -> PackRepository.displayPackList($$1.getResourcePackRepository().getSelectedPacks()));
        }
        if ($$2 != null) {
            $$0.setDetail("Current Language", () -> $$2.getSelected());
        }
        $$0.setDetail("Locale", String.valueOf(Locale.getDefault()));
        $$0.setDetail("System encoding", () -> System.getProperty("sun.jnu.encoding", "<not set>"));
        $$0.setDetail("File encoding", () -> System.getProperty("file.encoding", "<not set>"));
        $$0.setDetail("CPU", GLX::_getCpuInfo);
        return $$0;
    }

    public static Minecraft getInstance() {
        return instance;
    }

    public CompletableFuture<Void> delayTextureReload() {
        return this.submit(this::reloadResourcePacks).thenCompose($$0 -> $$0);
    }

    public void updateReportEnvironment(ReportEnvironment $$0) {
        if (!this.reportingContext.matches($$0)) {
            this.reportingContext = ReportingContext.create($$0, this.userApiService);
        }
    }

    @Nullable
    public ServerData getCurrentServer() {
        return Optionull.map(this.getConnection(), ClientPacketListener::getServerData);
    }

    public boolean isLocalServer() {
        return this.isLocalServer;
    }

    public boolean hasSingleplayerServer() {
        return this.isLocalServer && this.singleplayerServer != null;
    }

    @Nullable
    public IntegratedServer getSingleplayerServer() {
        return this.singleplayerServer;
    }

    public boolean isSingleplayer() {
        IntegratedServer $$0 = this.getSingleplayerServer();
        return $$0 != null && !$$0.isPublished();
    }

    public boolean isLocalPlayer(UUID $$0) {
        return $$0.equals(this.getUser().getProfileId());
    }

    public User getUser() {
        return this.user;
    }

    public GameProfile getGameProfile() {
        ProfileResult $$0 = this.profileFuture.join();
        if ($$0 != null) {
            return $$0.profile();
        }
        return new GameProfile(this.user.getProfileId(), this.user.getName());
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public PackRepository getResourcePackRepository() {
        return this.resourcePackRepository;
    }

    public VanillaPackResources getVanillaPackResources() {
        return this.vanillaPackResources;
    }

    public DownloadedPackSource getDownloadedPackSource() {
        return this.downloadedPackSource;
    }

    public Path getResourcePackDirectory() {
        return this.resourcePackDirectory;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation $$0) {
        return this.modelManager.getAtlas($$0)::getSprite;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public GpuWarnlistManager getGpuWarnlistManager() {
        return this.gpuWarnlistManager;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public MusicInfo getSituationalMusic() {
        Music $$0 = Optionull.map(this.screen, Screen::getBackgroundMusic);
        if ($$0 != null) {
            return new MusicInfo($$0);
        }
        if (this.player != null) {
            Level $$1 = this.player.level();
            if ($$1.dimension() == Level.END) {
                if (this.gui.getBossOverlay().shouldPlayMusic()) {
                    return new MusicInfo(Musics.END_BOSS);
                }
                return new MusicInfo(Musics.END);
            }
            Holder<Biome> $$2 = $$1.getBiome(this.player.blockPosition());
            Biome $$3 = $$2.value();
            float $$4 = $$3.getBackgroundMusicVolume();
            Optional<WeightedList<Music>> $$5 = $$3.getBackgroundMusic();
            if ($$5.isPresent()) {
                Optional<Music> $$6 = $$5.get().getRandom($$1.random);
                return new MusicInfo($$6.orElse(null), $$4);
            }
            if (this.musicManager.isPlayingMusic(Musics.UNDER_WATER) || this.player.isUnderWater() && $$2.is(BiomeTags.PLAYS_UNDERWATER_MUSIC)) {
                return new MusicInfo(Musics.UNDER_WATER, $$4);
            }
            if ($$1.dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly) {
                return new MusicInfo(Musics.CREATIVE, $$4);
            }
            return new MusicInfo(Musics.GAME, $$4);
        }
        return new MusicInfo(Musics.MENU);
    }

    public MinecraftSessionService getMinecraftSessionService() {
        return this.minecraftSessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(Entity $$0) {
        this.cameraEntity = $$0;
        this.gameRenderer.checkEntityPostEffect($$0);
    }

    public boolean shouldEntityAppearGlowing(Entity $$0) {
        return $$0.isCurrentlyGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && $$0.getType() == EntityType.PLAYER;
    }

    @Override
    protected Thread getRunningThread() {
        return this.gameThread;
    }

    @Override
    public Runnable wrapRunnable(Runnable $$0) {
        return $$0;
    }

    @Override
    protected boolean shouldRun(Runnable $$0) {
        return true;
    }

    public BlockRenderDispatcher getBlockRenderer() {
        return this.blockRenderer;
    }

    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return this.entityRenderDispatcher;
    }

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderDispatcher;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }

    public DeltaTracker getDeltaTracker() {
        return this.deltaTracker;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean showOnlyReducedInfo() {
        return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo().get() != false;
    }

    public ToastManager getToastManager() {
        return this.toastManager;
    }

    public Tutorial getTutorial() {
        return this.tutorial;
    }

    public boolean isWindowActive() {
        return this.windowActive;
    }

    public HotbarManager getHotbarManager() {
        return this.hotbarManager;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public PaintingTextureManager getPaintingTextures() {
        return this.paintingTextures;
    }

    public MapTextureManager getMapTextureManager() {
        return this.mapTextureManager;
    }

    public MapDecorationTextureManager getMapDecorationTextures() {
        return this.mapDecorationTextures;
    }

    public GuiSpriteManager getGuiSprites() {
        return this.guiSprites;
    }

    public WaypointStyleManager getWaypointStyles() {
        return this.waypointStyles;
    }

    @Override
    public void setWindowActive(boolean $$0) {
        this.windowActive = $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Component grabPanoramixScreenshot(File $$02) {
        int $$12 = 4;
        int $$2 = 4096;
        int $$3 = 4096;
        int $$4 = this.window.getWidth();
        int $$5 = this.window.getHeight();
        RenderTarget $$6 = this.getMainRenderTarget();
        float $$7 = this.player.getXRot();
        float $$8 = this.player.getYRot();
        float $$9 = this.player.xRotO;
        float $$10 = this.player.yRotO;
        this.gameRenderer.setRenderBlockOutline(false);
        try {
            this.gameRenderer.setPanoramicMode(true);
            this.window.setWidth(4096);
            this.window.setHeight(4096);
            $$6.resize(4096, 4096);
            for (int $$11 = 0; $$11 < 6; ++$$11) {
                switch ($$11) {
                    case 0: {
                        this.player.setYRot($$8);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYRot(($$8 + 90.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYRot(($$8 + 180.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYRot(($$8 - 90.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYRot($$8);
                        this.player.setXRot(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYRot($$8);
                        this.player.setXRot(90.0f);
                    }
                }
                this.player.yRotO = this.player.getYRot();
                this.player.xRotO = this.player.getXRot();
                this.gameRenderer.renderLevel(DeltaTracker.ONE);
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Screenshot.grab($$02, "panorama_" + $$11 + ".png", $$6, 4, $$0 -> {});
            }
            MutableComponent $$122 = Component.literal($$02.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle($$1 -> $$1.withClickEvent(new ClickEvent.OpenFile($$02.getAbsoluteFile())));
            MutableComponent mutableComponent = Component.a("screenshot.success", $$122);
            return mutableComponent;
        } catch (Exception $$13) {
            LOGGER.error("Couldn't save image", $$13);
            MutableComponent mutableComponent = Component.a("screenshot.failure", $$13.getMessage());
            return mutableComponent;
        } finally {
            this.player.setXRot($$7);
            this.player.setYRot($$8);
            this.player.xRotO = $$9;
            this.player.yRotO = $$10;
            this.gameRenderer.setRenderBlockOutline(true);
            this.window.setWidth($$4);
            this.window.setHeight($$5);
            $$6.resize($$4, $$5);
            this.gameRenderer.setPanoramicMode(false);
        }
    }

    @Nullable
    public StoringChunkProgressListener getProgressListener() {
        return this.progressListener.get();
    }

    public SplashManager getSplashManager() {
        return this.splashManager;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    public PlayerSocialManager getPlayerSocialManager() {
        return this.playerSocialManager;
    }

    public Window getWindow() {
        return this.window;
    }

    public FramerateLimitTracker getFramerateLimitTracker() {
        return this.framerateLimitTracker;
    }

    public DebugScreenOverlay getDebugOverlay() {
        return this.gui.getDebugOverlay();
    }

    public RenderBuffers renderBuffers() {
        return this.renderBuffers;
    }

    public void updateMaxMipLevel(int $$0) {
        this.modelManager.updateMaxMipLevel($$0);
    }

    public EntityModelSet getEntityModels() {
        return this.modelManager.entityModels().get();
    }

    public boolean isTextFilteringEnabled() {
        return this.userProperties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void prepareForMultiplayer() {
        this.playerSocialManager.startOnlineMode();
        this.getProfileKeyPairManager().prepareKeyPair();
    }

    @Nullable
    public SignatureValidator getProfileKeySignatureValidator() {
        return SignatureValidator.from(this.authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
    }

    public boolean canValidateProfileKeys() {
        return !this.authenticationService.getServicesKeySet().keys(ServicesKeyType.PROFILE_KEY).isEmpty();
    }

    public InputType getLastInputType() {
        return this.lastInputType;
    }

    public void setLastInputType(InputType $$0) {
        this.lastInputType = $$0;
    }

    public GameNarrator getNarrator() {
        return this.narrator;
    }

    public ChatListener getChatListener() {
        return this.chatListener;
    }

    public ReportingContext getReportingContext() {
        return this.reportingContext;
    }

    public RealmsDataFetcher realmsDataFetcher() {
        return this.realmsDataFetcher;
    }

    public QuickPlayLog quickPlayLog() {
        return this.quickPlayLog;
    }

    public CommandHistory commandHistory() {
        return this.commandHistory;
    }

    public DirectoryValidator directoryValidator() {
        return this.directoryValidator;
    }

    private float getTickTargetMillis(float $$0) {
        TickRateManager $$1;
        if (this.level != null && ($$1 = this.level.tickRateManager()).runsNormally()) {
            return Math.max($$0, $$1.millisecondsPerTick());
        }
        return $$0;
    }

    public ItemModelResolver getItemModelResolver() {
        return this.itemModelResolver;
    }

    @Nullable
    public static String getLauncherBrand() {
        return System.getProperty("minecraft.launcher.brand");
    }

    private static /* synthetic */ Optional lambda$getQuickActionsDialog$36(Registry $$0, HolderSet.Named $$1) {
        if ($$1.size() == 0) {
            return Optional.empty();
        }
        if ($$1.size() == 1) {
            return Optional.of($$1.get(0));
        }
        return $$0.get(Dialogs.QUICK_ACTIONS);
    }

    static {
        LOGGER = LogUtils.getLogger();
        ON_OSX = Util.getPlatform() == Util.OS.OSX;
        DEFAULT_FONT = ResourceLocation.withDefaultNamespace("default");
        UNIFORM_FONT = ResourceLocation.withDefaultNamespace("uniform");
        ALT_FONT = ResourceLocation.withDefaultNamespace("alt");
        REGIONAL_COMPLIANCIES = ResourceLocation.withDefaultNamespace("regional_compliancies.json");
        RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = Component.translatable("multiplayer.socialInteractions.not_available");
        SAVING_LEVEL = Component.translatable("menu.savingLevel");
    }

    static final class GameLoadCookie
    extends Record {
        private final RealmsClient realmsClient;
        final GameConfig.QuickPlayData quickPlayData;

        GameLoadCookie(RealmsClient $$0, GameConfig.QuickPlayData $$1) {
            this.realmsClient = $$0;
            this.quickPlayData = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameLoadCookie.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameLoadCookie.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameLoadCookie.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this, $$0);
        }

        public RealmsClient realmsClient() {
            return this.realmsClient;
        }

        public GameConfig.QuickPlayData quickPlayData() {
            return this.quickPlayData;
        }
    }

    public static abstract sealed class ChatStatus
    extends Enum<ChatStatus> {
        public static final /* enum */ ChatStatus ENABLED = new ChatStatus(CommonComponents.EMPTY){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return true;
            }
        };
        public static final /* enum */ ChatStatus DISABLED_BY_OPTIONS = new ChatStatus(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return false;
            }
        };
        public static final /* enum */ ChatStatus DISABLED_BY_LAUNCHER = new ChatStatus(Component.translatable("chat.disabled.launcher").withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return $$0;
            }
        };
        public static final /* enum */ ChatStatus DISABLED_BY_PROFILE = new ChatStatus(Component.a("chat.disabled.profile", Component.keybind(Minecraft.instance.options.keyChat.getName())).withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return $$0;
            }
        };
        static final Component INFO_DISABLED_BY_PROFILE;
        private final Component message;
        private static final /* synthetic */ ChatStatus[] $VALUES;

        public static ChatStatus[] values() {
            return (ChatStatus[])$VALUES.clone();
        }

        public static ChatStatus valueOf(String $$0) {
            return Enum.valueOf(ChatStatus.class, $$0);
        }

        ChatStatus(Component $$0) {
            this.message = $$0;
        }

        public Component getMessage() {
            return this.message;
        }

        public abstract boolean isChatAllowed(boolean var1);

        private static /* synthetic */ ChatStatus[] b() {
            return new ChatStatus[]{ENABLED, DISABLED_BY_OPTIONS, DISABLED_BY_LAUNCHER, DISABLED_BY_PROFILE};
        }

        static {
            $VALUES = ChatStatus.b();
            INFO_DISABLED_BY_PROFILE = Component.translatable("chat.disabled.profile.moreInfo");
        }
    }
}

