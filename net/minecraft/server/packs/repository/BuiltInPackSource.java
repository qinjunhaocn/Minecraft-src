/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource
implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_ID = "vanilla";
    public static final String TESTS_ID = "tests";
    public static final KnownPack CORE_PACK_INFO = KnownPack.vanilla("core");
    private final PackType packType;
    private final VanillaPackResources vanillaPack;
    private final ResourceLocation packDir;
    private final DirectoryValidator validator;

    public BuiltInPackSource(PackType $$0, VanillaPackResources $$1, ResourceLocation $$2, DirectoryValidator $$3) {
        this.packType = $$0;
        this.vanillaPack = $$1;
        this.packDir = $$2;
        this.validator = $$3;
    }

    @Override
    public void loadPacks(Consumer<Pack> $$0) {
        Pack $$1 = this.createVanillaPack(this.vanillaPack);
        if ($$1 != null) {
            $$0.accept($$1);
        }
        this.listBundledPacks($$0);
    }

    @Nullable
    protected abstract Pack createVanillaPack(PackResources var1);

    protected abstract Component getPackTitle(String var1);

    public VanillaPackResources getVanillaPack() {
        return this.vanillaPack;
    }

    private void listBundledPacks(Consumer<Pack> $$0) {
        HashMap<String, Function> $$12 = new HashMap<String, Function>();
        this.populatePackList($$12::put);
        $$12.forEach(($$1, $$2) -> {
            Pack $$3 = (Pack)$$2.apply($$1);
            if ($$3 != null) {
                $$0.accept($$3);
            }
        });
    }

    protected void populatePackList(BiConsumer<String, Function<String, Pack>> $$0) {
        this.vanillaPack.listRawPaths(this.packType, this.packDir, $$1 -> this.discoverPacksInPath((Path)$$1, $$0));
    }

    protected void discoverPacksInPath(@Nullable Path $$0, BiConsumer<String, Function<String, Pack>> $$1) {
        if ($$0 != null && Files.isDirectory($$0, new LinkOption[0])) {
            try {
                FolderRepositorySource.discoverPacks($$0, this.validator, ($$12, $$2) -> $$1.accept(BuiltInPackSource.pathToId($$12), $$1 -> this.createBuiltinPack((String)$$1, (Pack.ResourcesSupplier)$$2, this.getPackTitle((String)$$1))));
            } catch (IOException $$22) {
                LOGGER.warn("Failed to discover packs in {}", (Object)$$0, (Object)$$22);
            }
        }
    }

    private static String pathToId(Path $$0) {
        return StringUtils.removeEnd($$0.getFileName().toString(), ".zip");
    }

    @Nullable
    protected abstract Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3);

    protected static Pack.ResourcesSupplier fixedResources(final PackResources $$0) {
        return new Pack.ResourcesSupplier(){

            @Override
            public PackResources openPrimary(PackLocationInfo $$02) {
                return $$0;
            }

            @Override
            public PackResources openFull(PackLocationInfo $$02, Pack.Metadata $$1) {
                return $$0;
            }
        };
    }
}

