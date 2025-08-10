/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources
implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackLocationInfo location;

    protected AbstractPackResources(PackLocationInfo $$0) {
        this.location = $$0;
    }

    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionType<T> $$0) throws IOException {
        IoSupplier<InputStream> $$1 = this.a("pack.mcmeta");
        if ($$1 == null) {
            return null;
        }
        try (InputStream $$2 = $$1.get();){
            T t = AbstractPackResources.getMetadataFromStream($$0, $$2);
            return t;
        }
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static <T> T getMetadataFromStream(MetadataSectionType<T> $$0, InputStream $$12) {
        void $$6;
        try (BufferedReader $$2 = new BufferedReader(new InputStreamReader($$12, StandardCharsets.UTF_8));){
            JsonObject $$3 = GsonHelper.parse($$2);
        } catch (Exception $$5) {
            LOGGER.error("Couldn't load {} metadata", (Object)$$0.name(), (Object)$$5);
            return null;
        }
        if (!$$6.has($$0.name())) {
            return null;
        }
        return $$0.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)$$6.get($$0.name())).ifError($$1 -> LOGGER.error("Couldn't load {} metadata: {}", (Object)$$0.name(), $$1)).result().orElse(null);
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }
}

