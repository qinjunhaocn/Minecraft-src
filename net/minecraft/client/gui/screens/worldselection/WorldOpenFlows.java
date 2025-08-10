/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.RecoverWorldDataScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.InitialWorldCreationOptions;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.util.MemoryReserve;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.slf4j.Logger;

public class WorldOpenFlows {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final UUID WORLD_PACK_ID = UUID.fromString("640a6a92-b6cb-48a0-b391-831586500359");
    private final Minecraft minecraft;
    private final LevelStorageSource levelSource;

    public WorldOpenFlows(Minecraft $$0, LevelStorageSource $$1) {
        this.minecraft = $$0;
        this.levelSource = $$1;
    }

    public void createFreshLevel(String $$0, LevelSettings $$1, WorldOptions $$2, Function<HolderLookup.Provider, WorldDimensions> $$32, Screen $$4) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        LevelStorageSource.LevelStorageAccess $$5 = this.createWorldAccess($$0);
        if ($$5 == null) {
            return;
        }
        PackRepository $$6 = ServerPacksSource.createPackRepository($$5);
        WorldDataConfiguration $$7 = $$1.getDataConfiguration();
        try {
            WorldLoader.PackConfig $$8 = new WorldLoader.PackConfig($$6, $$7, false, false);
            WorldStem $$9 = this.loadWorldDataBlocking($$8, $$3 -> {
                WorldDimensions.Complete $$4 = ((WorldDimensions)((Object)((Object)$$32.apply($$3.datapackWorldgen())))).bake((Registry<LevelStem>)$$3.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM));
                return new WorldLoader.DataLoadOutput<PrimaryLevelData>(new PrimaryLevelData($$1, $$2, $$4.specialWorldProperty(), $$4.lifecycle()), $$4.dimensionsRegistryAccess());
            }, WorldStem::new);
            this.minecraft.doWorldLoad($$5, $$6, $$9, true);
        } catch (Exception $$10) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", $$10);
            $$5.safeClose();
            this.minecraft.setScreen($$4);
        }
    }

    @Nullable
    private LevelStorageSource.LevelStorageAccess createWorldAccess(String $$0) {
        try {
            return this.levelSource.validateAndCreateAccess($$0);
        } catch (IOException $$1) {
            LOGGER.warn("Failed to read level {} data", (Object)$$0, (Object)$$1);
            SystemToast.onWorldAccessFailure(this.minecraft, $$0);
            this.minecraft.setScreen(null);
            return null;
        } catch (ContentValidationException $$2) {
            LOGGER.warn("{}", (Object)$$2.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(null)));
            return null;
        }
    }

    public void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess $$0, ReloadableServerResources $$1, LayeredRegistryAccess<RegistryLayer> $$2, WorldData $$3) {
        PackRepository $$4 = ServerPacksSource.createPackRepository($$0);
        CloseableResourceManager $$5 = (CloseableResourceManager)new WorldLoader.PackConfig($$4, $$3.getDataConfiguration(), false, false).createResourceManager().getSecond();
        this.minecraft.doWorldLoad($$0, $$4, new WorldStem($$5, $$1, $$2, $$3), true);
    }

    public WorldStem loadWorldStem(Dynamic<?> $$0, boolean $$12, PackRepository $$2) throws Exception {
        WorldLoader.PackConfig $$3 = LevelStorageSource.getPackConfig($$0, $$2, $$12);
        return this.loadWorldDataBlocking($$3, $$1 -> {
            HolderLookup.RegistryLookup $$2 = $$1.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM);
            LevelDataAndDimensions $$3 = LevelStorageSource.getLevelDataAndDimensions($$0, $$1.dataConfiguration(), (Registry<LevelStem>)$$2, $$1.datapackWorldgen());
            return new WorldLoader.DataLoadOutput<WorldData>($$3.worldData(), $$3.dimensions().dimensionsRegistryAccess());
        }, WorldStem::new);
    }

    public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess $$02) throws Exception {
        final class Data
        extends Record {
            final LevelSettings levelSettings;
            final WorldOptions options;
            final Registry<LevelStem> existingDimensions;

            Data(LevelSettings $$0, WorldOptions $$1, Registry<LevelStem> $$2) {
                this.levelSettings = $$0;
                this.options = $$1;
                this.existingDimensions = $$2;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "levelSettings;options;existingDimensions", "levelSettings", "options", "existingDimensions"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "levelSettings;options;existingDimensions", "levelSettings", "options", "existingDimensions"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "levelSettings;options;existingDimensions", "levelSettings", "options", "existingDimensions"}, this, $$0);
            }

            public LevelSettings levelSettings() {
                return this.levelSettings;
            }

            public WorldOptions options() {
                return this.options;
            }

            public Registry<LevelStem> existingDimensions() {
                return this.existingDimensions;
            }
        }
        PackRepository $$12 = ServerPacksSource.createPackRepository($$02);
        Dynamic<?> $$22 = $$02.getDataTag();
        WorldLoader.PackConfig $$32 = LevelStorageSource.getPackConfig($$22, $$12, false);
        return this.loadWorldDataBlocking($$32, $$1 -> {
            Registry<LevelStem> $$2 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
            LevelDataAndDimensions $$3 = LevelStorageSource.getLevelDataAndDimensions($$22, $$1.dataConfiguration(), $$2, $$1.datapackWorldgen());
            return new WorldLoader.DataLoadOutput<Data>(new Data($$3.worldData().getLevelSettings(), $$3.worldData().worldGenOptions(), $$3.dimensions().dimensions()), $$1.datapackDimensions());
        }, ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            InitialWorldCreationOptions $$4 = new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.SURVIVAL, Set.of(), null);
            return Pair.of((Object)$$3.levelSettings, (Object)((Object)new WorldCreationContext($$3.options, new WorldDimensions($$3.existingDimensions), $$2, $$1, $$3.levelSettings.getDataConfiguration(), $$4)));
        });
    }

    private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig $$0, WorldLoader.WorldDataSupplier<D> $$1, WorldLoader.ResultFactory<D, R> $$2) throws Exception {
        WorldLoader.InitConfig $$3 = new WorldLoader.InitConfig($$0, Commands.CommandSelection.INTEGRATED, 2);
        CompletableFuture<R> $$4 = WorldLoader.load($$3, $$1, $$2, Util.backgroundExecutor(), this.minecraft);
        this.minecraft.managedBlock($$4::isDone);
        return $$4.get();
    }

    private void askForBackup(LevelStorageSource.LevelStorageAccess $$0, boolean $$1, Runnable $$22, Runnable $$32) {
        MutableComponent $$7;
        MutableComponent $$6;
        if ($$1) {
            MutableComponent $$4 = Component.translatable("selectWorld.backupQuestion.customized");
            MutableComponent $$5 = Component.translatable("selectWorld.backupWarning.customized");
        } else {
            $$6 = Component.translatable("selectWorld.backupQuestion.experimental");
            $$7 = Component.translatable("selectWorld.backupWarning.experimental");
        }
        this.minecraft.setScreen(new BackupConfirmScreen($$32, ($$2, $$3) -> {
            if ($$2) {
                EditWorldScreen.makeBackupAndShowToast($$0);
            }
            $$22.run();
        }, $$6, $$7, false));
    }

    public static void confirmWorldCreation(Minecraft $$0, CreateWorldScreen $$1, Lifecycle $$2, Runnable $$32, boolean $$4) {
        BooleanConsumer $$5 = $$3 -> {
            if ($$3) {
                $$32.run();
            } else {
                $$0.setScreen($$1);
            }
        };
        if ($$4 || $$2 == Lifecycle.stable()) {
            $$32.run();
        } else if ($$2 == Lifecycle.experimental()) {
            $$0.setScreen(new ConfirmScreen($$5, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")));
        } else {
            $$0.setScreen(new ConfirmScreen($$5, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")));
        }
    }

    public void openWorld(String $$0, Runnable $$1) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        LevelStorageSource.LevelStorageAccess $$2 = this.createWorldAccess($$0);
        if ($$2 == null) {
            return;
        }
        this.openWorldLoadLevelData($$2, $$1);
    }

    /*
     * WARNING - void declaration
     */
    private void openWorldLoadLevelData(LevelStorageSource.LevelStorageAccess $$0, Runnable $$1) {
        void $$10;
        void $$11;
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        try {
            Dynamic<?> $$22 = $$0.getDataTag();
            LevelSummary $$3 = $$0.getSummary($$22);
        } catch (IOException | NbtException | ReportedNbtException $$4) {
            this.minecraft.setScreen(new RecoverWorldDataScreen(this.minecraft, $$2 -> {
                if ($$2) {
                    this.openWorldLoadLevelData($$0, $$1);
                } else {
                    $$0.safeClose();
                    $$1.run();
                }
            }, $$0));
            return;
        } catch (OutOfMemoryError $$5) {
            MemoryReserve.release();
            String $$6 = "Ran out of memory trying to read level data of world folder \"" + $$0.getLevelId() + "\"";
            LOGGER.error(LogUtils.FATAL_MARKER, $$6);
            OutOfMemoryError $$7 = new OutOfMemoryError("Ran out of memory reading level data");
            $$7.initCause($$5);
            CrashReport $$8 = CrashReport.forThrowable($$7, $$6);
            CrashReportCategory $$9 = $$8.addCategory("World details");
            $$9.setDetail("World folder", $$0.getLevelId());
            throw new ReportedException($$8);
        }
        this.openWorldCheckVersionCompatibility($$0, (LevelSummary)$$11, (Dynamic<?>)$$10, $$1);
    }

    private void openWorldCheckVersionCompatibility(LevelStorageSource.LevelStorageAccess $$0, LevelSummary $$1, Dynamic<?> $$2, Runnable $$32) {
        if (!$$1.isCompatible()) {
            $$0.safeClose();
            this.minecraft.setScreen(new AlertScreen($$32, Component.translatable("selectWorld.incompatible.title").withColor(-65536), Component.a("selectWorld.incompatible.description", $$1.getWorldVersionName())));
            return;
        }
        LevelSummary.BackupStatus $$42 = $$1.backupStatus();
        if ($$42.shouldBackup()) {
            String $$5 = "selectWorld.backupQuestion." + $$42.getTranslationKey();
            String $$6 = "selectWorld.backupWarning." + $$42.getTranslationKey();
            MutableComponent $$7 = Component.translatable($$5);
            if ($$42.isSevere()) {
                $$7.withColor(-2142128);
            }
            MutableComponent $$8 = Component.a($$6, $$1.getWorldVersionName(), SharedConstants.getCurrentVersion().name());
            this.minecraft.setScreen(new BackupConfirmScreen(() -> {
                $$0.safeClose();
                $$32.run();
            }, ($$3, $$4) -> {
                if ($$3) {
                    EditWorldScreen.makeBackupAndShowToast($$0);
                }
                this.openWorldLoadLevelStem($$0, $$2, false, $$32);
            }, $$7, $$8, false));
        } else {
            this.openWorldLoadLevelStem($$0, $$2, false, $$32);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void openWorldLoadLevelStem(LevelStorageSource.LevelStorageAccess $$0, Dynamic<?> $$1, boolean $$2, Runnable $$3) {
        void $$8;
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.resource_load")));
        PackRepository $$4 = ServerPacksSource.createPackRepository($$0);
        try {
            WorldStem $$5 = this.loadWorldStem($$1, $$2, $$4);
            Iterator iterator = $$5.registries().compositeAccess().lookupOrThrow(Registries.LEVEL_STEM).iterator();
            while (iterator.hasNext()) {
                LevelStem $$6 = (LevelStem)((Object)iterator.next());
                $$6.generator().validate();
            }
        } catch (Exception $$7) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", $$7);
            if (!$$2) {
                this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
                    $$0.safeClose();
                    $$3.run();
                }, () -> this.openWorldLoadLevelStem($$0, $$1, true, $$3)));
            } else {
                $$0.safeClose();
                this.minecraft.setScreen(new AlertScreen($$3, Component.translatable("datapackFailure.safeMode.failed.title"), Component.translatable("datapackFailure.safeMode.failed.description"), CommonComponents.GUI_BACK, true));
            }
            return;
        }
        this.openWorldCheckWorldStemCompatibility($$0, (WorldStem)$$8, $$4, $$3);
    }

    private void openWorldCheckWorldStemCompatibility(LevelStorageSource.LevelStorageAccess $$0, WorldStem $$1, PackRepository $$2, Runnable $$3) {
        boolean $$6;
        WorldData $$4 = $$1.worldData();
        boolean $$5 = $$4.worldGenOptions().isOldCustomizedWorld();
        boolean bl = $$6 = $$4.worldGenSettingsLifecycle() != Lifecycle.stable();
        if ($$5 || $$6) {
            this.askForBackup($$0, $$5, () -> this.openWorldLoadBundledResourcePack($$0, $$1, $$2, $$3), () -> {
                $$1.close();
                $$0.safeClose();
                $$3.run();
            });
            return;
        }
        this.openWorldLoadBundledResourcePack($$0, $$1, $$2, $$3);
    }

    private void openWorldLoadBundledResourcePack(LevelStorageSource.LevelStorageAccess $$02, WorldStem $$1, PackRepository $$2, Runnable $$3) {
        DownloadedPackSource $$4 = this.minecraft.getDownloadedPackSource();
        ((CompletableFuture)this.loadBundledResourcePack($$4, $$02).thenApply($$0 -> true).exceptionallyComposeAsync($$0 -> {
            LOGGER.warn("Failed to load pack: ", (Throwable)$$0);
            return this.promptBundledPackLoadFailure();
        }, this.minecraft).thenAcceptAsync($$5 -> {
            if ($$5.booleanValue()) {
                this.openWorldCheckDiskSpace($$02, $$1, $$4, $$2, $$3);
            } else {
                $$4.popAll();
                $$1.close();
                $$02.safeClose();
                $$3.run();
            }
        }, (Executor)this.minecraft)).exceptionally($$0 -> {
            this.minecraft.delayCrash(CrashReport.forThrowable($$0, "Load world"));
            return null;
        });
    }

    private void openWorldCheckDiskSpace(LevelStorageSource.LevelStorageAccess $$0, WorldStem $$1, DownloadedPackSource $$2, PackRepository $$3, Runnable $$4) {
        if ($$0.checkForLowDiskSpace()) {
            this.minecraft.setScreen(new ConfirmScreen($$5 -> {
                if ($$5) {
                    this.openWorldDoLoad($$0, $$1, $$3);
                } else {
                    $$2.popAll();
                    $$1.close();
                    $$0.safeClose();
                    $$4.run();
                }
            }, Component.translatable("selectWorld.warning.lowDiskSpace.title").withStyle(ChatFormatting.RED), Component.translatable("selectWorld.warning.lowDiskSpace.description"), CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
        } else {
            this.openWorldDoLoad($$0, $$1, $$3);
        }
    }

    private void openWorldDoLoad(LevelStorageSource.LevelStorageAccess $$0, WorldStem $$1, PackRepository $$2) {
        this.minecraft.doWorldLoad($$0, $$2, $$1, false);
    }

    private CompletableFuture<Void> loadBundledResourcePack(DownloadedPackSource $$0, LevelStorageSource.LevelStorageAccess $$1) {
        Path $$2 = $$1.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
        if (Files.exists($$2, new LinkOption[0]) && !Files.isDirectory($$2, new LinkOption[0])) {
            $$0.configureForLocalWorld();
            CompletableFuture<Void> $$3 = $$0.waitForPackFeedback(WORLD_PACK_ID);
            $$0.pushLocalPack(WORLD_PACK_ID, $$2);
            return $$3;
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
        CompletableFuture<Boolean> $$0 = new CompletableFuture<Boolean>();
        this.minecraft.setScreen(new ConfirmScreen($$0::complete, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
        return $$0;
    }
}

