/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String STRUCTURE_RESOURCE_DIRECTORY_NAME = "structure";
    private static final String STRUCTURE_GENERATED_DIRECTORY_NAME = "structures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private ResourceManager resourceManager;
    private final Path generatedDir;
    private final List<Source> sources;
    private final HolderGetter<Block> blockLookup;
    private static final FileToIdConverter RESOURCE_LISTER = new FileToIdConverter("structure", ".nbt");

    public StructureTemplateManager(ResourceManager $$0, LevelStorageSource.LevelStorageAccess $$1, DataFixer $$2, HolderGetter<Block> $$3) {
        this.resourceManager = $$0;
        this.fixerUpper = $$2;
        this.generatedDir = $$1.getLevelPath(LevelResource.GENERATED_DIR).normalize();
        this.blockLookup = $$3;
        ImmutableList.Builder $$4 = ImmutableList.builder();
        $$4.add((Object)new Source(this::loadFromGenerated, this::listGenerated));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            $$4.add((Object)new Source(this::loadFromTestStructures, this::listTestStructures));
        }
        $$4.add((Object)new Source(this::loadFromResource, this::listResources));
        this.sources = $$4.build();
    }

    public StructureTemplate getOrCreate(ResourceLocation $$0) {
        Optional<StructureTemplate> $$1 = this.get($$0);
        if ($$1.isPresent()) {
            return $$1.get();
        }
        StructureTemplate $$2 = new StructureTemplate();
        this.structureRepository.put($$0, Optional.of($$2));
        return $$2;
    }

    public Optional<StructureTemplate> get(ResourceLocation $$0) {
        return this.structureRepository.computeIfAbsent($$0, this::tryLoad);
    }

    public Stream<ResourceLocation> listTemplates() {
        return this.sources.stream().flatMap($$0 -> $$0.lister().get()).distinct();
    }

    private Optional<StructureTemplate> tryLoad(ResourceLocation $$0) {
        for (Source $$1 : this.sources) {
            try {
                Optional<StructureTemplate> $$2 = $$1.loader().apply($$0);
                if (!$$2.isPresent()) continue;
                return $$2;
            } catch (Exception exception) {
            }
        }
        return Optional.empty();
    }

    public void onResourceManagerReload(ResourceManager $$0) {
        this.resourceManager = $$0;
        this.structureRepository.clear();
    }

    private Optional<StructureTemplate> loadFromResource(ResourceLocation $$0) {
        ResourceLocation $$12 = RESOURCE_LISTER.idToFile($$0);
        return this.load(() -> this.resourceManager.open($$12), $$1 -> LOGGER.error("Couldn't load structure {}", (Object)$$0, $$1));
    }

    private Stream<ResourceLocation> listResources() {
        return RESOURCE_LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(RESOURCE_LISTER::fileToId);
    }

    private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation $$0) {
        return this.loadFromSnbt($$0, StructureUtils.testStructuresDir);
    }

    private Stream<ResourceLocation> listTestStructures() {
        if (!Files.isDirectory(StructureUtils.testStructuresDir, new LinkOption[0])) {
            return Stream.empty();
        }
        ArrayList $$0 = new ArrayList();
        this.listFolderContents(StructureUtils.testStructuresDir, "minecraft", STRUCTURE_TEXT_FILE_EXTENSION, $$0::add);
        return $$0.stream();
    }

    private Optional<StructureTemplate> loadFromGenerated(ResourceLocation $$0) {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Optional.empty();
        }
        Path $$12 = this.createAndValidatePathToGeneratedStructure($$0, STRUCTURE_FILE_EXTENSION);
        return this.load(() -> new FileInputStream($$12.toFile()), $$1 -> LOGGER.error("Couldn't load structure from {}", (Object)$$12, $$1));
    }

    private Stream<ResourceLocation> listGenerated() {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Stream.empty();
        }
        try {
            ArrayList $$02 = new ArrayList();
            try (DirectoryStream<Path> $$1 = Files.newDirectoryStream(this.generatedDir, $$0 -> Files.isDirectory($$0, new LinkOption[0]));){
                for (Path $$2 : $$1) {
                    String $$3 = $$2.getFileName().toString();
                    Path $$4 = $$2.resolve(STRUCTURE_GENERATED_DIRECTORY_NAME);
                    this.listFolderContents($$4, $$3, STRUCTURE_FILE_EXTENSION, $$02::add);
                }
            }
            return $$02.stream();
        } catch (IOException $$5) {
            return Stream.empty();
        }
    }

    private void listFolderContents(Path $$0, String $$12, String $$22, Consumer<ResourceLocation> $$3) {
        int $$42 = $$22.length();
        Function<String, String> $$5 = $$1 -> $$1.substring(0, $$1.length() - $$42);
        try (Stream<Path> $$6 = Files.find($$0, Integer.MAX_VALUE, ($$1, $$2) -> $$2.isRegularFile() && $$1.toString().endsWith($$22), new FileVisitOption[0]);){
            $$6.forEach($$4 -> {
                try {
                    $$3.accept(ResourceLocation.fromNamespaceAndPath($$12, (String)$$5.apply(this.relativize($$0, (Path)$$4))));
                } catch (ResourceLocationException $$5) {
                    LOGGER.error("Invalid location while listing folder {} contents", (Object)$$0, (Object)$$5);
                }
            });
        } catch (IOException $$7) {
            LOGGER.error("Failed to list folder {} contents", (Object)$$0, (Object)$$7);
        }
    }

    private String relativize(Path $$0, Path $$1) {
        return $$0.relativize($$1).toString().replace(File.separator, "/");
    }

    private Optional<StructureTemplate> loadFromSnbt(ResourceLocation $$0, Path $$1) {
        Optional<StructureTemplate> optional;
        block10: {
            if (!Files.isDirectory($$1, new LinkOption[0])) {
                return Optional.empty();
            }
            Path $$2 = FileUtil.createPathToResource($$1, $$0.getPath(), STRUCTURE_TEXT_FILE_EXTENSION);
            BufferedReader $$3 = Files.newBufferedReader($$2);
            try {
                String $$4 = IOUtils.toString((Reader)$$3);
                optional = Optional.of(this.readStructure(NbtUtils.snbtToStructure($$4)));
                if ($$3 == null) break block10;
            } catch (Throwable throwable) {
                try {
                    if ($$3 != null) {
                        try {
                            $$3.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (NoSuchFileException $$5) {
                    return Optional.empty();
                } catch (CommandSyntaxException | IOException $$6) {
                    LOGGER.error("Couldn't load structure from {}", (Object)$$2, (Object)$$6);
                    return Optional.empty();
                }
            }
            $$3.close();
        }
        return optional;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private Optional<StructureTemplate> load(InputStreamOpener $$0, Consumer<Throwable> $$1) {
        try (InputStream $$2 = $$0.open();){
            Optional<StructureTemplate> optional;
            try (FastBufferedInputStream $$3 = new FastBufferedInputStream($$2);){
                optional = Optional.of(this.readStructure($$3));
            }
            return optional;
        } catch (FileNotFoundException $$4) {
            return Optional.empty();
        } catch (Throwable $$5) {
            $$1.accept($$5);
            return Optional.empty();
        }
    }

    private StructureTemplate readStructure(InputStream $$0) throws IOException {
        CompoundTag $$1 = NbtIo.readCompressed($$0, NbtAccounter.unlimitedHeap());
        return this.readStructure($$1);
    }

    public StructureTemplate readStructure(CompoundTag $$0) {
        StructureTemplate $$1 = new StructureTemplate();
        int $$2 = NbtUtils.getDataVersion($$0, 500);
        $$1.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, $$0, $$2));
        return $$1;
    }

    public boolean save(ResourceLocation $$0) {
        Optional<StructureTemplate> $$1 = this.structureRepository.get($$0);
        if ($$1.isEmpty()) {
            return false;
        }
        StructureTemplate $$2 = $$1.get();
        Path $$3 = this.createAndValidatePathToGeneratedStructure($$0, STRUCTURE_FILE_EXTENSION);
        Path $$4 = $$3.getParent();
        if ($$4 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists($$4, new LinkOption[0]) ? $$4.toRealPath(new LinkOption[0]) : $$4, new FileAttribute[0]);
        } catch (IOException $$5) {
            LOGGER.error("Failed to create parent directory: {}", (Object)$$4);
            return false;
        }
        CompoundTag $$6 = $$2.save(new CompoundTag());
        try (FileOutputStream $$7 = new FileOutputStream($$3.toFile());){
            NbtIo.writeCompressed($$6, $$7);
        } catch (Throwable $$8) {
            return false;
        }
        return true;
    }

    public Path createAndValidatePathToGeneratedStructure(ResourceLocation $$0, String $$1) {
        if ($$0.getPath().contains("//")) {
            throw new ResourceLocationException("Invalid resource path: " + String.valueOf($$0));
        }
        try {
            Path $$2 = this.generatedDir.resolve($$0.getNamespace());
            Path $$3 = $$2.resolve(STRUCTURE_GENERATED_DIRECTORY_NAME);
            Path $$4 = FileUtil.createPathToResource($$3, $$0.getPath(), $$1);
            if (!($$4.startsWith(this.generatedDir) && FileUtil.isPathNormalized($$4) && FileUtil.isPathPortable($$4))) {
                throw new ResourceLocationException("Invalid resource path: " + String.valueOf($$4));
            }
            return $$4;
        } catch (InvalidPathException $$5) {
            throw new ResourceLocationException("Invalid resource path: " + String.valueOf($$0), $$5);
        }
    }

    public void remove(ResourceLocation $$0) {
        this.structureRepository.remove($$0);
    }

    record Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
    }

    @FunctionalInterface
    static interface InputStreamOpener {
        public InputStream open() throws IOException;
    }
}

