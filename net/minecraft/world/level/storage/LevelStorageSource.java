/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtFormatException;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.FileNameDateFormatter;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import net.minecraft.world.level.validation.PathAllowList;
import org.slf4j.Logger;

public class LevelStorageSource {
    static final Logger LOGGER = LogUtils.getLogger();
    static final DateTimeFormatter FORMATTER = FileNameDateFormatter.create();
    public static final String TAG_DATA = "Data";
    private static final PathMatcher NO_SYMLINKS_ALLOWED = $$0 -> false;
    public static final String ALLOWED_SYMLINKS_CONFIG_NAME = "allowed_symlinks.txt";
    private static final int UNCOMPRESSED_NBT_QUOTA = 0x6400000;
    private static final int DISK_SPACE_WARNING_THRESHOLD = 0x4000000;
    private final Path baseDir;
    private final Path backupDir;
    final DataFixer fixerUpper;
    private final DirectoryValidator worldDirValidator;

    public LevelStorageSource(Path $$0, Path $$1, DirectoryValidator $$2, DataFixer $$3) {
        this.fixerUpper = $$3;
        try {
            FileUtil.createDirectoriesSafe($$0);
        } catch (IOException $$4) {
            throw new UncheckedIOException($$4);
        }
        this.baseDir = $$0;
        this.backupDir = $$1;
        this.worldDirValidator = $$2;
    }

    public static DirectoryValidator parseValidator(Path $$0) {
        if (Files.exists($$0, new LinkOption[0])) {
            DirectoryValidator directoryValidator;
            block9: {
                BufferedReader $$1 = Files.newBufferedReader($$0);
                try {
                    directoryValidator = new DirectoryValidator(PathAllowList.readPlain($$1));
                    if ($$1 == null) break block9;
                } catch (Throwable throwable) {
                    try {
                        if ($$1 != null) {
                            try {
                                $$1.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    } catch (Exception $$2) {
                        LOGGER.error("Failed to parse {}, disallowing all symbolic links", (Object)ALLOWED_SYMLINKS_CONFIG_NAME, (Object)$$2);
                    }
                }
                $$1.close();
            }
            return directoryValidator;
        }
        return new DirectoryValidator(NO_SYMLINKS_ALLOWED);
    }

    public static LevelStorageSource createDefault(Path $$0) {
        DirectoryValidator $$1 = LevelStorageSource.parseValidator($$0.resolve(ALLOWED_SYMLINKS_CONFIG_NAME));
        return new LevelStorageSource($$0, $$0.resolve("../backups"), $$1, DataFixers.getDataFixer());
    }

    public static WorldDataConfiguration readDataConfig(Dynamic<?> $$0) {
        return WorldDataConfiguration.CODEC.parse($$0).resultOrPartial(LOGGER::error).orElse(WorldDataConfiguration.DEFAULT);
    }

    public static WorldLoader.PackConfig getPackConfig(Dynamic<?> $$0, PackRepository $$1, boolean $$2) {
        return new WorldLoader.PackConfig($$1, LevelStorageSource.readDataConfig($$0), $$2, false);
    }

    public static LevelDataAndDimensions getLevelDataAndDimensions(Dynamic<?> $$0, WorldDataConfiguration $$1, Registry<LevelStem> $$2, HolderLookup.Provider $$3) {
        Dynamic<?> $$4 = RegistryOps.injectRegistryContext($$0, $$3);
        Dynamic $$5 = $$4.get("WorldGenSettings").orElseEmptyMap();
        WorldGenSettings $$6 = (WorldGenSettings)((Object)WorldGenSettings.CODEC.parse($$5).getOrThrow());
        LevelSettings $$7 = LevelSettings.parse($$4, $$1);
        WorldDimensions.Complete $$8 = $$6.dimensions().bake($$2);
        Lifecycle $$9 = $$8.lifecycle().add($$3.allRegistriesLifecycle());
        PrimaryLevelData $$10 = PrimaryLevelData.parse($$4, $$7, $$8.specialWorldProperty(), $$6.options(), $$9);
        return new LevelDataAndDimensions($$10, $$8);
    }

    public String getName() {
        return "Anvil";
    }

    public LevelCandidates findLevelCandidates() throws LevelStorageException {
        LevelCandidates levelCandidates;
        block9: {
            if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
                throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
            }
            Stream<Path> $$02 = Files.list(this.baseDir);
            try {
                List $$1 = $$02.filter($$0 -> Files.isDirectory($$0, new LinkOption[0])).map(LevelDirectory::new).filter($$0 -> Files.isRegularFile($$0.dataFile(), new LinkOption[0]) || Files.isRegularFile($$0.oldDataFile(), new LinkOption[0])).toList();
                levelCandidates = new LevelCandidates($$1);
                if ($$02 == null) break block9;
            } catch (Throwable throwable) {
                try {
                    if ($$02 != null) {
                        try {
                            $$02.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException $$2) {
                    throw new LevelStorageException(Component.translatable("selectWorld.load_folder_access"));
                }
            }
            $$02.close();
        }
        return levelCandidates;
    }

    public CompletableFuture<List<LevelSummary>> loadLevelSummaries(LevelCandidates $$02) {
        ArrayList<CompletableFuture<LevelSummary>> $$1 = new ArrayList<CompletableFuture<LevelSummary>>($$02.levels.size());
        for (LevelDirectory $$2 : $$02.levels) {
            $$1.add(CompletableFuture.supplyAsync(() -> {
                try {
                    boolean $$1 = DirectoryLock.isLocked($$2.path());
                } catch (Exception $$2) {
                    LOGGER.warn("Failed to read {} lock", (Object)$$2.path(), (Object)$$2);
                    return null;
                }
                try {
                    void $$3;
                    return this.readLevelSummary($$2, (boolean)$$3);
                } catch (OutOfMemoryError $$4) {
                    MemoryReserve.release();
                    String $$5 = "Ran out of memory trying to read summary of world folder \"" + $$2.directoryName() + "\"";
                    LOGGER.error(LogUtils.FATAL_MARKER, $$5);
                    OutOfMemoryError $$6 = new OutOfMemoryError("Ran out of memory reading level data");
                    $$6.initCause($$4);
                    CrashReport $$7 = CrashReport.forThrowable($$6, $$5);
                    CrashReportCategory $$8 = $$7.addCategory("World details");
                    $$8.setDetail("Folder Name", $$2.directoryName());
                    try {
                        long $$9 = Files.size($$2.dataFile());
                        $$8.setDetail("level.dat size", $$9);
                    } catch (IOException $$10) {
                        $$8.setDetailError("level.dat size", $$10);
                    }
                    throw new ReportedException($$7);
                }
            }, Util.backgroundExecutor().forName("loadLevelSummaries")));
        }
        return Util.sequenceFailFastAndCancel($$1).thenApply($$0 -> $$0.stream().filter(Objects::nonNull).sorted().toList());
    }

    private int getStorageVersion() {
        return 19133;
    }

    static CompoundTag readLevelDataTagRaw(Path $$0) throws IOException {
        return NbtIo.readCompressed($$0, NbtAccounter.create(0x6400000L));
    }

    static Dynamic<?> readLevelDataTagFixed(Path $$0, DataFixer $$1) throws IOException {
        CompoundTag $$22 = LevelStorageSource.readLevelDataTagRaw($$0);
        CompoundTag $$3 = $$22.getCompoundOrEmpty(TAG_DATA);
        int $$4 = NbtUtils.getDataVersion($$3, -1);
        Dynamic $$5 = DataFixTypes.LEVEL.updateToCurrentVersion($$1, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$3), $$4);
        $$5 = $$5.update("Player", $$2 -> DataFixTypes.PLAYER.updateToCurrentVersion($$1, $$2, $$4));
        $$5 = $$5.update("WorldGenSettings", $$2 -> DataFixTypes.WORLD_GEN_SETTINGS.updateToCurrentVersion($$1, $$2, $$4));
        return $$5;
    }

    private LevelSummary readLevelSummary(LevelDirectory $$0, boolean $$1) {
        Path $$2 = $$0.dataFile();
        if (Files.exists($$2, new LinkOption[0])) {
            try {
                List<ForbiddenSymlinkInfo> $$3;
                if (Files.isSymbolicLink($$2) && !($$3 = this.worldDirValidator.validateSymlink($$2)).isEmpty()) {
                    LOGGER.warn("{}", (Object)ContentValidationException.getMessage($$2, $$3));
                    return new LevelSummary.SymlinkLevelSummary($$0.directoryName(), $$0.iconFile());
                }
                Tag $$4 = LevelStorageSource.readLightweightData($$2);
                if ($$4 instanceof CompoundTag) {
                    CompoundTag $$5 = (CompoundTag)$$4;
                    CompoundTag $$6 = $$5.getCompoundOrEmpty(TAG_DATA);
                    int $$7 = NbtUtils.getDataVersion($$6, -1);
                    Dynamic $$8 = DataFixTypes.LEVEL_SUMMARY.updateToCurrentVersion(this.fixerUpper, new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$6), $$7);
                    return this.makeLevelSummary($$8, $$0, $$1);
                }
                LOGGER.warn("Invalid root tag in {}", (Object)$$2);
            } catch (Exception $$9) {
                LOGGER.error("Exception reading {}", (Object)$$2, (Object)$$9);
            }
        }
        return new LevelSummary.CorruptedLevelSummary($$0.directoryName(), $$0.iconFile(), LevelStorageSource.getFileModificationTime($$0));
    }

    private static long getFileModificationTime(LevelDirectory $$0) {
        Instant $$1 = LevelStorageSource.getFileModificationTime($$0.dataFile());
        if ($$1 == null) {
            $$1 = LevelStorageSource.getFileModificationTime($$0.oldDataFile());
        }
        return $$1 == null ? -1L : $$1.toEpochMilli();
    }

    @Nullable
    static Instant getFileModificationTime(Path $$0) {
        try {
            return Files.getLastModifiedTime($$0, new LinkOption[0]).toInstant();
        } catch (IOException iOException) {
            return null;
        }
    }

    LevelSummary makeLevelSummary(Dynamic<?> $$0, LevelDirectory $$1, boolean $$2) {
        LevelVersion $$3 = LevelVersion.parse($$0);
        int $$4 = $$3.levelDataVersion();
        if ($$4 == 19132 || $$4 == 19133) {
            boolean $$5 = $$4 != this.getStorageVersion();
            Path $$6 = $$1.iconFile();
            WorldDataConfiguration $$7 = LevelStorageSource.readDataConfig($$0);
            LevelSettings $$8 = LevelSettings.parse($$0, $$7);
            FeatureFlagSet $$9 = LevelStorageSource.parseFeatureFlagsFromSummary($$0);
            boolean $$10 = FeatureFlags.isExperimental($$9);
            return new LevelSummary($$8, $$3, $$1.directoryName(), $$5, $$2, $$10, $$6);
        }
        throw new NbtFormatException("Unknown data version: " + Integer.toHexString($$4));
    }

    private static FeatureFlagSet parseFeatureFlagsFromSummary(Dynamic<?> $$02) {
        Set<ResourceLocation> $$1 = $$02.get("enabled_features").asStream().flatMap($$0 -> $$0.asString().result().map(ResourceLocation::tryParse).stream()).collect(Collectors.toSet());
        return FeatureFlags.REGISTRY.fromNames($$1, $$0 -> {});
    }

    @Nullable
    private static Tag readLightweightData(Path $$0) throws IOException {
        SkipFields $$1 = new SkipFields(new FieldSelector(TAG_DATA, CompoundTag.TYPE, "Player"), new FieldSelector(TAG_DATA, CompoundTag.TYPE, "WorldGenSettings"));
        NbtIo.parseCompressed($$0, (StreamTagVisitor)$$1, NbtAccounter.create(0x6400000L));
        return $$1.getResult();
    }

    public boolean isNewLevelIdAcceptable(String $$0) {
        try {
            Path $$1 = this.getLevelPath($$0);
            Files.createDirectory($$1, new FileAttribute[0]);
            Files.deleteIfExists($$1);
            return true;
        } catch (IOException $$2) {
            return false;
        }
    }

    public boolean levelExists(String $$0) {
        try {
            return Files.isDirectory(this.getLevelPath($$0), new LinkOption[0]);
        } catch (InvalidPathException $$1) {
            return false;
        }
    }

    public Path getLevelPath(String $$0) {
        return this.baseDir.resolve($$0);
    }

    public Path getBaseDir() {
        return this.baseDir;
    }

    public Path getBackupPath() {
        return this.backupDir;
    }

    public LevelStorageAccess validateAndCreateAccess(String $$0) throws IOException, ContentValidationException {
        Path $$1 = this.getLevelPath($$0);
        List<ForbiddenSymlinkInfo> $$2 = this.worldDirValidator.validateDirectory($$1, true);
        if (!$$2.isEmpty()) {
            throw new ContentValidationException($$1, $$2);
        }
        return new LevelStorageAccess($$0, $$1);
    }

    public LevelStorageAccess createAccess(String $$0) throws IOException {
        Path $$1 = this.getLevelPath($$0);
        return new LevelStorageAccess($$0, $$1);
    }

    public DirectoryValidator getWorldDirValidator() {
        return this.worldDirValidator;
    }

    public static final class LevelCandidates
    extends Record
    implements Iterable<LevelDirectory> {
        final List<LevelDirectory> levels;

        public LevelCandidates(List<LevelDirectory> $$0) {
            this.levels = $$0;
        }

        public boolean isEmpty() {
            return this.levels.isEmpty();
        }

        @Override
        public Iterator<LevelDirectory> iterator() {
            return this.levels.iterator();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LevelCandidates.class, "levels", "levels"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LevelCandidates.class, "levels", "levels"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LevelCandidates.class, "levels", "levels"}, this, $$0);
        }

        public List<LevelDirectory> levels() {
            return this.levels;
        }
    }

    public static final class LevelDirectory
    extends Record {
        final Path path;

        public LevelDirectory(Path $$0) {
            this.path = $$0;
        }

        public String directoryName() {
            return this.path.getFileName().toString();
        }

        public Path dataFile() {
            return this.resourcePath(LevelResource.LEVEL_DATA_FILE);
        }

        public Path oldDataFile() {
            return this.resourcePath(LevelResource.OLD_LEVEL_DATA_FILE);
        }

        public Path corruptedDataFile(LocalDateTime $$0) {
            return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_corrupted_" + $$0.format(FORMATTER));
        }

        public Path rawDataFile(LocalDateTime $$0) {
            return this.path.resolve(LevelResource.LEVEL_DATA_FILE.getId() + "_raw_" + $$0.format(FORMATTER));
        }

        public Path iconFile() {
            return this.resourcePath(LevelResource.ICON_FILE);
        }

        public Path lockFile() {
            return this.resourcePath(LevelResource.LOCK_FILE);
        }

        public Path resourcePath(LevelResource $$0) {
            return this.path.resolve($$0.getId());
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LevelDirectory.class, "path", "path"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LevelDirectory.class, "path", "path"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LevelDirectory.class, "path", "path"}, this, $$0);
        }

        public Path path() {
            return this.path;
        }
    }

    public class LevelStorageAccess
    implements AutoCloseable {
        final DirectoryLock lock;
        final LevelDirectory levelDirectory;
        private final String levelId;
        private final Map<LevelResource, Path> resources = Maps.newHashMap();

        LevelStorageAccess(String $$1, Path $$2) throws IOException {
            this.levelId = $$1;
            this.levelDirectory = new LevelDirectory($$2);
            this.lock = DirectoryLock.create($$2);
        }

        public long estimateDiskSpace() {
            try {
                return Files.getFileStore(this.levelDirectory.path).getUsableSpace();
            } catch (Exception $$0) {
                return Long.MAX_VALUE;
            }
        }

        public boolean checkForLowDiskSpace() {
            return this.estimateDiskSpace() < 0x4000000L;
        }

        public void safeClose() {
            try {
                this.close();
            } catch (IOException $$0) {
                LOGGER.warn("Failed to unlock access to level {}", (Object)this.getLevelId(), (Object)$$0);
            }
        }

        public LevelStorageSource parent() {
            return LevelStorageSource.this;
        }

        public LevelDirectory getLevelDirectory() {
            return this.levelDirectory;
        }

        public String getLevelId() {
            return this.levelId;
        }

        public Path getLevelPath(LevelResource $$0) {
            return this.resources.computeIfAbsent($$0, this.levelDirectory::resourcePath);
        }

        public Path getDimensionPath(ResourceKey<Level> $$0) {
            return DimensionType.getStorageFolder($$0, this.levelDirectory.path());
        }

        private void checkLock() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public PlayerDataStorage createPlayerStorage() {
            this.checkLock();
            return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
        }

        public LevelSummary getSummary(Dynamic<?> $$0) {
            this.checkLock();
            return LevelStorageSource.this.makeLevelSummary($$0, this.levelDirectory, false);
        }

        public Dynamic<?> getDataTag() throws IOException {
            return this.getDataTag(false);
        }

        public Dynamic<?> getDataTagFallback() throws IOException {
            return this.getDataTag(true);
        }

        private Dynamic<?> getDataTag(boolean $$0) throws IOException {
            this.checkLock();
            return LevelStorageSource.readLevelDataTagFixed($$0 ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile(), LevelStorageSource.this.fixerUpper);
        }

        public void saveDataTag(RegistryAccess $$0, WorldData $$1) {
            this.saveDataTag($$0, $$1, null);
        }

        public void saveDataTag(RegistryAccess $$0, WorldData $$1, @Nullable CompoundTag $$2) {
            CompoundTag $$3 = $$1.createTag($$0, $$2);
            CompoundTag $$4 = new CompoundTag();
            $$4.put(LevelStorageSource.TAG_DATA, $$3);
            this.saveLevelData($$4);
        }

        private void saveLevelData(CompoundTag $$0) {
            Path $$1 = this.levelDirectory.path();
            try {
                Path $$2 = Files.createTempFile($$1, "level", ".dat", new FileAttribute[0]);
                NbtIo.writeCompressed($$0, $$2);
                Path $$3 = this.levelDirectory.oldDataFile();
                Path $$4 = this.levelDirectory.dataFile();
                Util.safeReplaceFile($$4, $$2, $$3);
            } catch (Exception $$5) {
                LOGGER.error("Failed to save level {}", (Object)$$1, (Object)$$5);
            }
        }

        public Optional<Path> getIconFile() {
            if (!this.lock.isValid()) {
                return Optional.empty();
            }
            return Optional.of(this.levelDirectory.iconFile());
        }

        public void deleteLevel() throws IOException {
            this.checkLock();
            final Path $$0 = this.levelDirectory.lockFile();
            LOGGER.info("Deleting level {}", (Object)this.levelId);
            for (int $$1 = 1; $$1 <= 5; ++$$1) {
                LOGGER.info("Attempt {}...", (Object)$$1);
                try {
                    Files.walkFileTree(this.levelDirectory.path(), (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                        @Override
                        public FileVisitResult visitFile(Path $$02, BasicFileAttributes $$1) throws IOException {
                            if (!$$02.equals($$0)) {
                                LOGGER.debug("Deleting {}", (Object)$$02);
                                Files.delete($$02);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path $$02, @Nullable IOException $$1) throws IOException {
                            if ($$1 != null) {
                                throw $$1;
                            }
                            if ($$02.equals(LevelStorageAccess.this.levelDirectory.path())) {
                                LevelStorageAccess.this.lock.close();
                                Files.deleteIfExists($$0);
                            }
                            Files.delete($$02);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public /* synthetic */ FileVisitResult postVisitDirectory(Object object, @Nullable IOException iOException) throws IOException {
                            return this.postVisitDirectory((Path)object, iOException);
                        }

                        @Override
                        public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                            return this.visitFile((Path)object, basicFileAttributes);
                        }
                    });
                    break;
                } catch (IOException $$2) {
                    if ($$1 < 5) {
                        LOGGER.warn("Failed to delete {}", (Object)this.levelDirectory.path(), (Object)$$2);
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    throw $$2;
                }
            }
        }

        public void renameLevel(String $$0) throws IOException {
            this.modifyLevelDataWithoutDatafix($$1 -> $$1.putString("LevelName", $$0.trim()));
        }

        public void renameAndDropPlayer(String $$0) throws IOException {
            this.modifyLevelDataWithoutDatafix($$1 -> {
                $$1.putString("LevelName", $$0.trim());
                $$1.remove("Player");
            });
        }

        private void modifyLevelDataWithoutDatafix(Consumer<CompoundTag> $$0) throws IOException {
            this.checkLock();
            CompoundTag $$1 = LevelStorageSource.readLevelDataTagRaw(this.levelDirectory.dataFile());
            $$0.accept($$1.getCompoundOrEmpty(LevelStorageSource.TAG_DATA));
            this.saveLevelData($$1);
        }

        public long makeWorldBackup() throws IOException {
            this.checkLock();
            String $$0 = LocalDateTime.now().format(FORMATTER) + "_" + this.levelId;
            Path $$1 = LevelStorageSource.this.getBackupPath();
            try {
                FileUtil.createDirectoriesSafe($$1);
            } catch (IOException $$2) {
                throw new RuntimeException($$2);
            }
            Path $$3 = $$1.resolve(FileUtil.findAvailableName($$1, $$0, ".zip"));
            try (final ZipOutputStream $$4 = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream($$3, new OpenOption[0])));){
                final Path $$5 = Paths.get(this.levelId, new String[0]);
                Files.walkFileTree(this.levelDirectory.path(), (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path $$0, BasicFileAttributes $$1) throws IOException {
                        if ($$0.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String $$2 = $$5.resolve(LevelStorageAccess.this.levelDirectory.path().relativize($$0)).toString().replace('\\', '/');
                        ZipEntry $$3 = new ZipEntry($$2);
                        $$4.putNextEntry($$3);
                        com.google.common.io.Files.asByteSource($$0.toFile()).copyTo($$4);
                        $$4.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                        return this.visitFile((Path)object, basicFileAttributes);
                    }
                });
            }
            return Files.size($$3);
        }

        public boolean hasWorldData() {
            return Files.exists(this.levelDirectory.dataFile(), new LinkOption[0]) || Files.exists(this.levelDirectory.oldDataFile(), new LinkOption[0]);
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }

        public boolean restoreLevelDataFromOld() {
            return Util.safeReplaceOrMoveFile(this.levelDirectory.dataFile(), this.levelDirectory.oldDataFile(), this.levelDirectory.corruptedDataFile(LocalDateTime.now()), true);
        }

        @Nullable
        public Instant getFileModificationTime(boolean $$0) {
            return LevelStorageSource.getFileModificationTime($$0 ? this.levelDirectory.oldDataFile() : this.levelDirectory.dataFile());
        }
    }
}

