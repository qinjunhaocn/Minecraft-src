/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;

public class ClientPackSource
extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES), Optional.empty());
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
    public static final String HIGH_CONTRAST_PACK = "high_contrast";
    private static final Map<String, Component> SPECIAL_PACK_NAMES = Map.of((Object)"programmer_art", (Object)Component.translatable("resourcePack.programmer_art.name"), (Object)"high_contrast", (Object)Component.translatable("resourcePack.high_contrast.name"));
    private static final PackLocationInfo VANILLA_PACK_INFO = new PackLocationInfo("vanilla", Component.translatable("resourcePack.vanilla.name"), PackSource.BUILT_IN, Optional.of(CORE_PACK_INFO));
    private static final PackSelectionConfig VANILLA_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
    private static final PackSelectionConfig BUILT_IN_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
    private static final ResourceLocation PACKS_DIR = ResourceLocation.withDefaultNamespace("resourcepacks");
    @Nullable
    private final Path externalAssetDir;

    public ClientPackSource(Path $$0, DirectoryValidator $$1) {
        super(PackType.CLIENT_RESOURCES, ClientPackSource.createVanillaPackSource($$0), PACKS_DIR, $$1);
        this.externalAssetDir = this.findExplodedAssetPacks($$0);
    }

    private static PackLocationInfo createBuiltInPackLocation(String $$0, Component $$1) {
        return new PackLocationInfo($$0, $$1, PackSource.BUILT_IN, Optional.of(KnownPack.vanilla($$0)));
    }

    @Nullable
    private Path findExplodedAssetPacks(Path $$0) {
        Path $$1;
        if (SharedConstants.IS_RUNNING_IN_IDE && $$0.getFileSystem() == FileSystems.getDefault() && Files.isDirectory($$1 = $$0.getParent().resolve("resourcepacks"), new LinkOption[0])) {
            return $$1;
        }
        return null;
    }

    private static VanillaPackResources createVanillaPackSource(Path $$0) {
        VanillaPackResourcesBuilder $$1 = new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).a("minecraft", "realms");
        return $$1.applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, $$0).build(VANILLA_PACK_INFO);
    }

    @Override
    protected Component getPackTitle(String $$0) {
        Component $$1 = SPECIAL_PACK_NAMES.get($$0);
        return $$1 != null ? $$1 : Component.literal($$0);
    }

    @Override
    @Nullable
    protected Pack createVanillaPack(PackResources $$0) {
        return Pack.readMetaAndCreate(VANILLA_PACK_INFO, ClientPackSource.fixedResources($$0), PackType.CLIENT_RESOURCES, VANILLA_SELECTION_CONFIG);
    }

    @Override
    @Nullable
    protected Pack createBuiltinPack(String $$0, Pack.ResourcesSupplier $$1, Component $$2) {
        return Pack.readMetaAndCreate(ClientPackSource.createBuiltInPackLocation($$0, $$2), $$1, PackType.CLIENT_RESOURCES, BUILT_IN_SELECTION_CONFIG);
    }

    @Override
    protected void populatePackList(BiConsumer<String, Function<String, Pack>> $$0) {
        super.populatePackList($$0);
        if (this.externalAssetDir != null) {
            this.discoverPacksInPath(this.externalAssetDir, $$0);
        }
    }
}

