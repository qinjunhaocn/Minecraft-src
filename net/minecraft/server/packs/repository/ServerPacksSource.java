/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.repository;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;

public class ServerPacksSource
extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("dataPack.vanilla.description"), SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA), Optional.empty());
    private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION);
    private static final PackLocationInfo VANILLA_PACK_INFO = new PackLocationInfo("vanilla", Component.translatable("dataPack.vanilla.name"), PackSource.BUILT_IN, Optional.of(CORE_PACK_INFO));
    private static final PackSelectionConfig VANILLA_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.BOTTOM, false);
    private static final PackSelectionConfig FEATURE_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
    private static final ResourceLocation PACKS_DIR = ResourceLocation.withDefaultNamespace("datapacks");

    public ServerPacksSource(DirectoryValidator $$0) {
        super(PackType.SERVER_DATA, ServerPacksSource.createVanillaPackSource(), PACKS_DIR, $$0);
    }

    private static PackLocationInfo createBuiltInPackLocation(String $$0, Component $$1) {
        return new PackLocationInfo($$0, $$1, PackSource.FEATURE, Optional.of(KnownPack.vanilla($$0)));
    }

    @VisibleForTesting
    public static VanillaPackResources createVanillaPackSource() {
        return new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).a("minecraft").applyDevelopmentConfig().pushJarResources().build(VANILLA_PACK_INFO);
    }

    @Override
    protected Component getPackTitle(String $$0) {
        return Component.literal($$0);
    }

    @Override
    @Nullable
    protected Pack createVanillaPack(PackResources $$0) {
        return Pack.readMetaAndCreate(VANILLA_PACK_INFO, ServerPacksSource.fixedResources($$0), PackType.SERVER_DATA, VANILLA_SELECTION_CONFIG);
    }

    @Override
    @Nullable
    protected Pack createBuiltinPack(String $$0, Pack.ResourcesSupplier $$1, Component $$2) {
        return Pack.readMetaAndCreate(ServerPacksSource.createBuiltInPackLocation($$0, $$2), $$1, PackType.SERVER_DATA, FEATURE_SELECTION_CONFIG);
    }

    public static PackRepository createPackRepository(Path $$0, DirectoryValidator $$1) {
        return new PackRepository(new ServerPacksSource($$1), new FolderRepositorySource($$0, PackType.SERVER_DATA, PackSource.WORLD, $$1));
    }

    public static PackRepository createVanillaTrustedRepository() {
        return new PackRepository(new ServerPacksSource(new DirectoryValidator($$0 -> true)));
    }

    public static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess $$0) {
        return ServerPacksSource.createPackRepository($$0.getLevelPath(LevelResource.DATAPACK_DIR), $$0.parent().getWorldDirValidator());
    }
}

