/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldCallback;
import net.minecraft.client.gui.screens.worldselection.DataPackReloadCookie;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.ExperimentsScreen;
import net.minecraft.client.gui.screens.worldselection.InitialWorldCreationOptions;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContextMapper;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPresets;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class CreateWorldScreen
extends Screen {
    private static final int GROUP_BOTTOM = 1;
    private static final int TAB_COLUMN_WIDTH = 210;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMP_WORLD_PREFIX = "mcworld-";
    static final Component GAME_MODEL_LABEL = Component.translatable("selectWorld.gameMode");
    static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
    static final Component EXPERIMENTS_LABEL = Component.translatable("selectWorld.experiments");
    static final Component ALLOW_COMMANDS_INFO = Component.translatable("selectWorld.allowCommands.info");
    private static final Component PREPARING_WORLD_DATA = Component.translatable("createWorld.preparing");
    private static final int HORIZONTAL_BUTTON_SPACING = 10;
    private static final int VERTICAL_BUTTON_SPACING = 8;
    public static final ResourceLocation TAB_HEADER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/tab_header_background.png");
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final WorldCreationUiState uiState;
    private final TabManager tabManager = new TabManager($$1 -> {
        AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
    }, $$1 -> this.removeWidget((GuiEventListener)$$1));
    private boolean recreated;
    private final DirectoryValidator packValidator;
    private final CreateWorldCallback createWorldCallback;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private Path tempDataPackDir;
    @Nullable
    private PackRepository tempDataPackRepository;
    @Nullable
    private TabNavigationBar tabNavigationBar;

    public static void openFresh(Minecraft $$02, @Nullable Screen $$12) {
        CreateWorldScreen.openFresh($$02, $$12, ($$0, $$1, $$2, $$3) -> $$0.createNewWorld($$1, $$2));
    }

    public static void openFresh(Minecraft $$02, @Nullable Screen $$12, CreateWorldCallback $$22) {
        WorldCreationContextMapper $$3 = ($$0, $$1, $$2) -> new WorldCreationContext($$2.worldGenSettings(), $$1, $$0, $$2.dataConfiguration());
        Function<WorldLoader.DataLoadContext, WorldGenSettings> $$4 = $$0 -> new WorldGenSettings(WorldOptions.defaultWithRandomSeed(), WorldPresets.createNormalWorldDimensions($$0.datapackWorldgen()));
        CreateWorldScreen.openCreateWorldScreen($$02, $$12, $$4, $$3, WorldPresets.NORMAL, $$22);
    }

    public static void testWorld(Minecraft $$02, @Nullable Screen $$12) {
        WorldCreationContextMapper $$22 = ($$0, $$1, $$2) -> new WorldCreationContext($$2.worldGenSettings().options(), $$2.worldGenSettings().dimensions(), $$1, $$0, $$2.dataConfiguration(), new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.CREATIVE, Set.of(GameRules.RULE_DAYLIGHT, GameRules.RULE_WEATHER_CYCLE, GameRules.RULE_DOMOBSPAWNING), FlatLevelGeneratorPresets.REDSTONE_READY));
        Function<WorldLoader.DataLoadContext, WorldGenSettings> $$32 = $$0 -> new WorldGenSettings(WorldOptions.testWorldWithRandomSeed(), WorldPresets.createFlatWorldDimensions($$0.datapackWorldgen()));
        CreateWorldScreen.openCreateWorldScreen($$02, $$12, $$32, $$22, WorldPresets.FLAT, ($$0, $$1, $$2, $$3) -> $$0.createNewWorld($$1, $$2));
    }

    private static void openCreateWorldScreen(Minecraft $$0, @Nullable Screen $$12, Function<WorldLoader.DataLoadContext, WorldGenSettings> $$22, WorldCreationContextMapper $$32, ResourceKey<WorldPreset> $$42, CreateWorldCallback $$5) {
        CreateWorldScreen.queueLoadScreen($$0, PREPARING_WORLD_DATA);
        PackRepository $$6 = new PackRepository(new ServerPacksSource($$0.directoryValidator()));
        WorldDataConfiguration $$7 = SharedConstants.IS_RUNNING_IN_IDE ? new WorldDataConfiguration(new DataPackConfig(List.of((Object)"vanilla", (Object)"tests"), List.of()), FeatureFlags.DEFAULT_FLAGS) : WorldDataConfiguration.DEFAULT;
        WorldLoader.InitConfig $$8 = CreateWorldScreen.createDefaultLoadConfig($$6, $$7);
        CompletableFuture<WorldCreationContext> $$9 = WorldLoader.load($$8, $$1 -> new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie((WorldGenSettings)((Object)((Object)$$22.apply($$1))), $$1.dataConfiguration()), $$1.datapackDimensions()), ($$1, $$2, $$3, $$4) -> {
            $$1.close();
            return $$32.apply($$2, $$3, (DataPackReloadCookie)((Object)$$4));
        }, Util.backgroundExecutor(), $$0);
        $$0.managedBlock($$9::isDone);
        $$0.setScreen(new CreateWorldScreen($$0, $$12, $$9.join(), Optional.of($$42), OptionalLong.empty(), $$5));
    }

    public static CreateWorldScreen createFromExisting(Minecraft $$02, @Nullable Screen $$12, LevelSettings $$22, WorldCreationContext $$32, @Nullable Path $$4) {
        CreateWorldScreen $$5 = new CreateWorldScreen($$02, $$12, $$32, WorldPresets.fromSettings($$32.selectedDimensions()), OptionalLong.of($$32.options().seed()), ($$0, $$1, $$2, $$3) -> $$0.createNewWorld($$1, $$2));
        $$5.recreated = true;
        $$5.uiState.setName($$22.levelName());
        $$5.uiState.setAllowCommands($$22.allowCommands());
        $$5.uiState.setDifficulty($$22.difficulty());
        $$5.uiState.getGameRules().assignFrom($$22.gameRules(), null);
        if ($$22.hardcore()) {
            $$5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
        } else if ($$22.gameType().isSurvival()) {
            $$5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
        } else if ($$22.gameType().isCreative()) {
            $$5.uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
        }
        $$5.tempDataPackDir = $$4;
        return $$5;
    }

    private CreateWorldScreen(Minecraft $$0, @Nullable Screen $$12, WorldCreationContext $$2, Optional<ResourceKey<WorldPreset>> $$3, OptionalLong $$4, CreateWorldCallback $$5) {
        super(Component.translatable("selectWorld.create"));
        this.lastScreen = $$12;
        this.packValidator = $$0.directoryValidator();
        this.createWorldCallback = $$5;
        this.uiState = new WorldCreationUiState($$0.getLevelSource().getBaseDir(), $$2, $$3, $$4);
    }

    public WorldCreationUiState getUiState() {
        return this.uiState;
    }

    @Override
    protected void init() {
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).a(new GameTab(), new WorldTab(), new MoreTab()).build();
        this.addRenderableWidget(this.tabNavigationBar);
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$02.addChild(Button.builder(Component.translatable("selectWorld.create"), $$0 -> this.onCreate()).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.popScreen()).build());
        this.layout.visitWidgets($$0 -> {
            $$0.setTabOrderGroup(1);
            this.addRenderableWidget($$0);
        });
        this.tabNavigationBar.selectTab(0, false);
        this.uiState.onChanged();
        this.repositionElements();
    }

    @Override
    protected void setInitialFocus() {
    }

    @Override
    public void repositionElements() {
        if (this.tabNavigationBar == null) {
            return;
        }
        this.tabNavigationBar.setWidth(this.width);
        this.tabNavigationBar.arrangeElements();
        int $$0 = this.tabNavigationBar.getRectangle().bottom();
        ScreenRectangle $$1 = new ScreenRectangle(0, $$0, this.width, this.height - this.layout.getFooterHeight() - $$0);
        this.tabManager.setTabArea($$1);
        this.layout.setHeaderHeight($$0);
        this.layout.arrangeElements();
    }

    private static void queueLoadScreen(Minecraft $$0, Component $$1) {
        $$0.forceSetScreen(new GenericMessageScreen($$1));
    }

    private void onCreate() {
        WorldCreationContext $$0 = this.uiState.getSettings();
        WorldDimensions.Complete $$1 = $$0.selectedDimensions().bake($$0.datapackDimensions());
        LayeredRegistryAccess<RegistryLayer> $$2 = $$0.worldgenRegistries().a(RegistryLayer.DIMENSIONS, $$1.dimensionsRegistryAccess());
        Lifecycle $$3 = FeatureFlags.isExperimental($$0.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
        Lifecycle $$4 = $$2.compositeAccess().allRegistriesLifecycle();
        Lifecycle $$5 = $$4.add($$3);
        boolean $$6 = !this.recreated && $$4 == Lifecycle.stable();
        LevelSettings $$7 = this.createLevelSettings($$1.specialWorldProperty() == PrimaryLevelData.SpecialWorldProperty.DEBUG);
        PrimaryLevelData $$8 = new PrimaryLevelData($$7, this.uiState.getSettings().options(), $$1.specialWorldProperty(), $$5);
        WorldOpenFlows.confirmWorldCreation(this.minecraft, this, $$5, () -> this.createWorldAndCleanup($$2, $$8), $$6);
    }

    private void createWorldAndCleanup(LayeredRegistryAccess<RegistryLayer> $$0, PrimaryLevelData $$1) {
        boolean $$2 = this.createWorldCallback.create(this, $$0, $$1, this.tempDataPackDir);
        this.removeTempDataPackDir();
        if (!$$2) {
            this.popScreen();
        }
    }

    private boolean createNewWorld(LayeredRegistryAccess<RegistryLayer> $$0, WorldData $$1) {
        String $$2 = this.uiState.getTargetFolder();
        WorldCreationContext $$3 = this.uiState.getSettings();
        CreateWorldScreen.queueLoadScreen(this.minecraft, PREPARING_WORLD_DATA);
        Optional<LevelStorageSource.LevelStorageAccess> $$4 = CreateWorldScreen.createNewWorldDirectory(this.minecraft, $$2, this.tempDataPackDir);
        if ($$4.isEmpty()) {
            SystemToast.onPackCopyFailure(this.minecraft, $$2);
            return false;
        }
        this.minecraft.createWorldOpenFlows().createLevelFromExistingSettings($$4.get(), $$3.dataPackResources(), $$0, $$1);
        return true;
    }

    private LevelSettings createLevelSettings(boolean $$0) {
        String $$1 = this.uiState.getName().trim();
        if ($$0) {
            GameRules $$2 = new GameRules(WorldDataConfiguration.DEFAULT.enabledFeatures());
            $$2.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            return new LevelSettings($$1, GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, $$2, WorldDataConfiguration.DEFAULT);
        }
        return new LevelSettings($$1, this.uiState.getGameMode().gameType, this.uiState.isHardcore(), this.uiState.getDifficulty(), this.uiState.isAllowCommands(), this.uiState.getGameRules(), this.uiState.getSettings().dataConfiguration());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.tabNavigationBar.keyPressed($$0)) {
            return true;
        }
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            this.onCreate();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        this.popScreen();
    }

    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
        this.removeTempDataPackDir();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics $$0) {
        $$0.blit(RenderPipelines.GUI_TEXTURED, TAB_HEADER_BACKGROUND, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderMenuBackground($$0, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    @Nullable
    private Path getOrCreateTempDataPackDir() {
        if (this.tempDataPackDir == null) {
            try {
                this.tempDataPackDir = Files.createTempDirectory(TEMP_WORLD_PREFIX, new FileAttribute[0]);
            } catch (IOException $$0) {
                LOGGER.warn("Failed to create temporary dir", $$0);
                SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
                this.popScreen();
            }
        }
        return this.tempDataPackDir;
    }

    void openExperimentsScreen(WorldDataConfiguration $$02) {
        Pair<Path, PackRepository> $$1 = this.getDataPackSelectionSettings($$02);
        if ($$1 != null) {
            this.minecraft.setScreen(new ExperimentsScreen(this, (PackRepository)$$1.getSecond(), $$0 -> this.tryApplyNewDataPacks((PackRepository)$$0, false, this::openExperimentsScreen)));
        }
    }

    void openDataPackSelectionScreen(WorldDataConfiguration $$02) {
        Pair<Path, PackRepository> $$1 = this.getDataPackSelectionSettings($$02);
        if ($$1 != null) {
            this.minecraft.setScreen(new PackSelectionScreen((PackRepository)$$1.getSecond(), $$0 -> this.tryApplyNewDataPacks((PackRepository)$$0, true, this::openDataPackSelectionScreen), (Path)$$1.getFirst(), Component.translatable("dataPack.title")));
        }
    }

    private void tryApplyNewDataPacks(PackRepository $$0, boolean $$12, Consumer<WorldDataConfiguration> $$2) {
        List $$4;
        ImmutableList<String> $$32 = ImmutableList.copyOf($$0.getSelectedIds());
        WorldDataConfiguration $$5 = new WorldDataConfiguration(new DataPackConfig($$32, $$4 = (List)$$0.getAvailableIds().stream().filter($$1 -> !$$32.contains($$1)).collect(ImmutableList.toImmutableList())), this.uiState.getSettings().dataConfiguration().enabledFeatures());
        if (this.uiState.tryUpdateDataConfiguration($$5)) {
            this.minecraft.setScreen(this);
            return;
        }
        FeatureFlagSet $$6 = $$0.getRequestedFeatureFlags();
        if (FeatureFlags.isExperimental($$6) && $$12) {
            this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen($$0.getSelectedPacks(), $$3 -> {
                if ($$3) {
                    this.applyNewPackConfig($$0, $$5, $$2);
                } else {
                    $$2.accept(this.uiState.getSettings().dataConfiguration());
                }
            }));
        } else {
            this.applyNewPackConfig($$0, $$5, $$2);
        }
    }

    private void applyNewPackConfig(PackRepository $$03, WorldDataConfiguration $$13, Consumer<WorldDataConfiguration> $$22) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("dataPack.validation.working")));
        WorldLoader.InitConfig $$32 = CreateWorldScreen.createDefaultLoadConfig($$03, $$13);
        ((CompletableFuture)((CompletableFuture)WorldLoader.load($$32, $$02 -> {
            if ($$02.datapackWorldgen().lookupOrThrow(Registries.WORLD_PRESET).listElements().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            }
            if ($$02.datapackWorldgen().lookupOrThrow(Registries.BIOME).listElements().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one biome continue");
            }
            WorldCreationContext $$12 = this.uiState.getSettings();
            RegistryOps $$2 = $$12.worldgenLoadContext().createSerializationContext(JsonOps.INSTANCE);
            DataResult $$3 = WorldGenSettings.encode($$2, $$12.options(), $$12.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps $$4 = $$02.datapackWorldgen().createSerializationContext(JsonOps.INSTANCE);
            WorldGenSettings $$5 = (WorldGenSettings)((Object)((Object)$$3.flatMap($$1 -> WorldGenSettings.CODEC.parse($$4, $$1)).getOrThrow($$0 -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + $$0))));
            return new WorldLoader.DataLoadOutput<DataPackReloadCookie>(new DataPackReloadCookie($$5, $$02.dataConfiguration()), $$02.datapackDimensions());
        }, ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return new WorldCreationContext($$3.worldGenSettings(), $$2, $$1, $$3.dataConfiguration());
        }, Util.backgroundExecutor(), this.minecraft).thenApply($$0 -> {
            $$0.validate();
            return $$0;
        })).thenAcceptAsync(this.uiState::setSettings, (Executor)this.minecraft)).handleAsync(($$12, $$2) -> {
            if ($$2 != null) {
                LOGGER.warn("Failed to validate datapack", (Throwable)$$2);
                this.minecraft.setScreen(new ConfirmScreen($$1 -> {
                    if ($$1) {
                        $$22.accept(this.uiState.getSettings().dataConfiguration());
                    } else {
                        $$22.accept(WorldDataConfiguration.DEFAULT);
                    }
                }, Component.translatable("dataPack.validation.failed"), CommonComponents.EMPTY, Component.translatable("dataPack.validation.back"), Component.translatable("dataPack.validation.reset")));
            } else {
                this.minecraft.setScreen(this);
            }
            return null;
        }, (Executor)this.minecraft);
    }

    private static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository $$0, WorldDataConfiguration $$1) {
        WorldLoader.PackConfig $$2 = new WorldLoader.PackConfig($$0, $$1, false, true);
        return new WorldLoader.InitConfig($$2, Commands.CommandSelection.INTEGRATED, 2);
    }

    private void removeTempDataPackDir() {
        if (this.tempDataPackDir != null && Files.exists(this.tempDataPackDir, new LinkOption[0])) {
            try (Stream<Path> $$02 = Files.walk(this.tempDataPackDir, new FileVisitOption[0]);){
                $$02.sorted(Comparator.reverseOrder()).forEach($$0 -> {
                    try {
                        Files.delete($$0);
                    } catch (IOException $$1) {
                        LOGGER.warn("Failed to remove temporary file {}", $$0, (Object)$$1);
                    }
                });
            } catch (IOException $$1) {
                LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
            }
        }
        this.tempDataPackDir = null;
    }

    private static void copyBetweenDirs(Path $$0, Path $$1, Path $$2) {
        try {
            Util.copyBetweenDirs($$0, $$1, $$2);
        } catch (IOException $$3) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)$$2, (Object)$$1);
            throw new UncheckedIOException($$3);
        }
    }

    /*
     * WARNING - bad return control flow
     */
    private static Optional<LevelStorageSource.LevelStorageAccess> createNewWorldDirectory(Minecraft $$0, String $$12, @Nullable Path $$22) {
        Optional<LevelStorageSource.LevelStorageAccess> optional;
        block12: {
            LevelStorageSource.LevelStorageAccess $$3;
            block11: {
                $$3 = $$0.getLevelSource().createAccess($$12);
                if ($$22 != null) break block11;
                return Optional.of($$3);
            }
            Stream<Path> $$4 = Files.walk($$22, new FileVisitOption[0]);
            try {
                Path $$5 = $$3.getLevelPath(LevelResource.DATAPACK_DIR);
                FileUtil.createDirectoriesSafe($$5);
                $$4.filter($$1 -> !$$1.equals($$22)).forEach($$2 -> CreateWorldScreen.copyBetweenDirs($$22, $$5, $$2));
                optional = Optional.of($$3);
                if ($$4 == null) break block12;
            } catch (Throwable throwable) {
                try {
                    try {
                        if ($$4 != null) {
                            try {
                                $$4.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    } catch (IOException | UncheckedIOException $$6) {
                        LOGGER.warn("Failed to copy datapacks to world {}", (Object)$$12, (Object)$$6);
                        $$3.close();
                    }
                } catch (IOException | UncheckedIOException $$7) {
                    LOGGER.warn("Failed to create access for {}", (Object)$$12, (Object)$$7);
                }
            }
            $$4.close();
        }
        return optional;
        return Optional.empty();
    }

    @Nullable
    public static Path createTempDataPackDirFromExistingWorld(Path $$0, Minecraft $$12) {
        MutableObject $$22 = new MutableObject();
        try (Stream<Path> $$3 = Files.walk($$0, new FileVisitOption[0]);){
            $$3.filter($$1 -> !$$1.equals($$0)).forEach($$2 -> {
                Path $$3 = (Path)$$22.getValue();
                if ($$3 == null) {
                    try {
                        $$3 = Files.createTempDirectory(TEMP_WORLD_PREFIX, new FileAttribute[0]);
                    } catch (IOException $$4) {
                        LOGGER.warn("Failed to create temporary dir");
                        throw new UncheckedIOException($$4);
                    }
                    $$22.setValue($$3);
                }
                CreateWorldScreen.copyBetweenDirs($$0, $$3, $$2);
            });
        } catch (IOException | UncheckedIOException $$4) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)$$0, (Object)$$4);
            SystemToast.onPackCopyFailure($$12, $$0.toString());
            return null;
        }
        return (Path)$$22.getValue();
    }

    @Nullable
    private Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration $$0) {
        Path $$1 = this.getOrCreateTempDataPackDir();
        if ($$1 != null) {
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = ServerPacksSource.createPackRepository($$1, this.packValidator);
                this.tempDataPackRepository.reload();
            }
            this.tempDataPackRepository.setSelected($$0.dataPacks().getEnabled());
            return Pair.of((Object)$$1, (Object)this.tempDataPackRepository);
        }
        return null;
    }

    class GameTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.game.title");
        private static final Component ALLOW_COMMANDS = Component.translatable("selectWorld.allowCommands");
        private final EditBox nameEdit;

        GameTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.rowSpacing(8).createRowHelper(1);
            LayoutSettings $$12 = $$02.newCellSettings();
            this.nameEdit = new EditBox(CreateWorldScreen.this.font, 208, 20, Component.translatable("selectWorld.enterName"));
            this.nameEdit.setValue(CreateWorldScreen.this.uiState.getName());
            this.nameEdit.setResponder(CreateWorldScreen.this.uiState::setName);
            CreateWorldScreen.this.uiState.addListener($$0 -> this.nameEdit.setTooltip(Tooltip.create(Component.a("selectWorld.targetFolder", Component.literal($$0.getTargetFolder()).withStyle(ChatFormatting.ITALIC)))));
            CreateWorldScreen.this.setInitialFocus(this.nameEdit);
            $$02.addChild(CommonLayouts.labeledElement(CreateWorldScreen.this.font, this.nameEdit, NAME_LABEL), $$02.newCellSettings().alignHorizontallyCenter());
            CycleButton<WorldCreationUiState.SelectedGameMode> $$2 = $$02.addChild(CycleButton.builder($$0 -> $$0.displayName).a((WorldCreationUiState.SelectedGameMode[])new WorldCreationUiState.SelectedGameMode[]{WorldCreationUiState.SelectedGameMode.SURVIVAL, WorldCreationUiState.SelectedGameMode.HARDCORE, WorldCreationUiState.SelectedGameMode.CREATIVE}).create(0, 0, 210, 20, GAME_MODEL_LABEL, ($$0, $$1) -> CreateWorldScreen.this.uiState.setGameMode((WorldCreationUiState.SelectedGameMode)((Object)$$1))), $$12);
            CreateWorldScreen.this.uiState.addListener($$1 -> {
                $$2.setValue($$1.getGameMode());
                $$0.active = !$$1.isDebug();
                $$2.setTooltip(Tooltip.create($$1.getGameMode().getInfo()));
            });
            CycleButton<Difficulty> $$3 = $$02.addChild(CycleButton.builder(Difficulty::getDisplayName).a((Difficulty[])Difficulty.values()).create(0, 0, 210, 20, Component.translatable("options.difficulty"), ($$0, $$1) -> CreateWorldScreen.this.uiState.setDifficulty((Difficulty)$$1)), $$12);
            CreateWorldScreen.this.uiState.addListener($$1 -> {
                $$3.setValue(CreateWorldScreen.this.uiState.getDifficulty());
                $$0.active = !CreateWorldScreen.this.uiState.isHardcore();
                $$3.setTooltip(Tooltip.create(CreateWorldScreen.this.uiState.getDifficulty().getInfo()));
            });
            CycleButton<Boolean> $$4 = $$02.addChild(CycleButton.onOffBuilder().withTooltip($$0 -> Tooltip.create(ALLOW_COMMANDS_INFO)).create(0, 0, 210, 20, ALLOW_COMMANDS, ($$0, $$1) -> CreateWorldScreen.this.uiState.setAllowCommands((boolean)$$1)));
            CreateWorldScreen.this.uiState.addListener($$1 -> {
                $$4.setValue(CreateWorldScreen.this.uiState.isAllowCommands());
                $$0.active = !CreateWorldScreen.this.uiState.isDebug() && !CreateWorldScreen.this.uiState.isHardcore();
            });
            if (!SharedConstants.getCurrentVersion().stable()) {
                $$02.addChild(Button.builder(EXPERIMENTS_LABEL, $$0 -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())).width(210).build());
            }
        }
    }

    class WorldTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.world.title");
        private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
        private static final Component GENERATE_STRUCTURES = Component.translatable("selectWorld.mapFeatures");
        private static final Component GENERATE_STRUCTURES_INFO = Component.translatable("selectWorld.mapFeatures.info");
        private static final Component BONUS_CHEST = Component.translatable("selectWorld.bonusItems");
        private static final Component SEED_LABEL = Component.translatable("selectWorld.enterSeed");
        static final Component SEED_EMPTY_HINT = Component.translatable("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY);
        private static final int WORLD_TAB_WIDTH = 310;
        private final EditBox seedEdit;
        private final Button customizeTypeButton;

        WorldTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
            CycleButton<WorldCreationUiState.WorldTypeEntry> $$12 = $$02.addChild(CycleButton.builder(WorldCreationUiState.WorldTypeEntry::describePreset).withValues(this.createWorldTypeValueSupplier()).withCustomNarration(WorldTab::createTypeButtonNarration).create(0, 0, 150, 20, Component.translatable("selectWorld.mapType"), ($$0, $$1) -> CreateWorldScreen.this.uiState.setWorldType((WorldCreationUiState.WorldTypeEntry)((Object)$$1))));
            $$12.setValue(CreateWorldScreen.this.uiState.getWorldType());
            CreateWorldScreen.this.uiState.addListener($$1 -> {
                WorldCreationUiState.WorldTypeEntry $$2 = $$1.getWorldType();
                $$12.setValue($$2);
                if ($$2.isAmplified()) {
                    $$12.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
                } else {
                    $$12.setTooltip(null);
                }
                $$0.active = CreateWorldScreen.this.uiState.getWorldType().preset() != null;
            });
            this.customizeTypeButton = $$02.addChild(Button.builder(Component.translatable("selectWorld.customizeType"), $$0 -> this.openPresetEditor()).build());
            CreateWorldScreen.this.uiState.addListener($$0 -> {
                this.customizeTypeButton.active = !$$0.isDebug() && $$0.getPresetEditor() != null;
            });
            this.seedEdit = new EditBox(this, CreateWorldScreen.this.font, 308, 20, Component.translatable("selectWorld.enterSeed")){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return super.createNarrationMessage().append(CommonComponents.NARRATION_SEPARATOR).append(SEED_EMPTY_HINT);
                }
            };
            this.seedEdit.setHint(SEED_EMPTY_HINT);
            this.seedEdit.setValue(CreateWorldScreen.this.uiState.getSeed());
            this.seedEdit.setResponder($$0 -> CreateWorldScreen.this.uiState.setSeed(this.seedEdit.getValue()));
            $$02.addChild(CommonLayouts.labeledElement(CreateWorldScreen.this.font, this.seedEdit, SEED_LABEL), 2);
            SwitchGrid.Builder $$2 = SwitchGrid.builder(310);
            $$2.addSwitch(GENERATE_STRUCTURES, CreateWorldScreen.this.uiState::isGenerateStructures, CreateWorldScreen.this.uiState::setGenerateStructures).withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isDebug()).withInfo(GENERATE_STRUCTURES_INFO);
            $$2.addSwitch(BONUS_CHEST, CreateWorldScreen.this.uiState::isBonusChest, CreateWorldScreen.this.uiState::setBonusChest).withIsActiveCondition(() -> !CreateWorldScreen.this.uiState.isHardcore() && !CreateWorldScreen.this.uiState.isDebug());
            SwitchGrid $$3 = $$2.build();
            $$02.addChild($$3.layout(), 2);
            CreateWorldScreen.this.uiState.addListener($$1 -> $$3.refreshStates());
        }

        private void openPresetEditor() {
            PresetEditor $$0 = CreateWorldScreen.this.uiState.getPresetEditor();
            if ($$0 != null) {
                CreateWorldScreen.this.minecraft.setScreen($$0.createEditScreen(CreateWorldScreen.this, CreateWorldScreen.this.uiState.getSettings()));
            }
        }

        private CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry> createWorldTypeValueSupplier() {
            return new CycleButton.ValueListSupplier<WorldCreationUiState.WorldTypeEntry>(){

                @Override
                public List<WorldCreationUiState.WorldTypeEntry> getSelectedList() {
                    return CycleButton.DEFAULT_ALT_LIST_SELECTOR.getAsBoolean() ? CreateWorldScreen.this.uiState.getAltPresetList() : CreateWorldScreen.this.uiState.getNormalPresetList();
                }

                @Override
                public List<WorldCreationUiState.WorldTypeEntry> getDefaultList() {
                    return CreateWorldScreen.this.uiState.getNormalPresetList();
                }
            };
        }

        private static MutableComponent createTypeButtonNarration(CycleButton<WorldCreationUiState.WorldTypeEntry> $$0) {
            if ($$0.getValue().isAmplified()) {
                return CommonComponents.a($$0.createDefaultNarrationMessage(), AMPLIFIED_HELP_TEXT);
            }
            return $$0.createDefaultNarrationMessage();
        }
    }

    class MoreTab
    extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("createWorld.tab.more.title");
        private static final Component GAME_RULES_LABEL = Component.translatable("selectWorld.gameRules");
        private static final Component DATA_PACKS_LABEL = Component.translatable("selectWorld.dataPacks");

        MoreTab() {
            super(TITLE);
            GridLayout.RowHelper $$02 = this.layout.rowSpacing(8).createRowHelper(1);
            $$02.addChild(Button.builder(GAME_RULES_LABEL, $$0 -> this.openGameRulesScreen()).width(210).build());
            $$02.addChild(Button.builder(EXPERIMENTS_LABEL, $$0 -> CreateWorldScreen.this.openExperimentsScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())).width(210).build());
            $$02.addChild(Button.builder(DATA_PACKS_LABEL, $$0 -> CreateWorldScreen.this.openDataPackSelectionScreen(CreateWorldScreen.this.uiState.getSettings().dataConfiguration())).width(210).build());
        }

        private void openGameRulesScreen() {
            CreateWorldScreen.this.minecraft.setScreen(new EditGameRulesScreen(CreateWorldScreen.this.uiState.getGameRules().copy(CreateWorldScreen.this.uiState.getSettings().dataConfiguration().enabledFeatures()), $$0 -> {
                CreateWorldScreen.this.minecraft.setScreen(CreateWorldScreen.this);
                $$0.ifPresent(CreateWorldScreen.this.uiState::setGameRules);
            }));
        }
    }
}

