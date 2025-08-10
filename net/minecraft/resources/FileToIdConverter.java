/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class FileToIdConverter {
    private final String prefix;
    private final String extension;

    public FileToIdConverter(String $$0, String $$1) {
        this.prefix = $$0;
        this.extension = $$1;
    }

    public static FileToIdConverter json(String $$0) {
        return new FileToIdConverter($$0, ".json");
    }

    public static FileToIdConverter registry(ResourceKey<? extends Registry<?>> $$0) {
        return FileToIdConverter.json(Registries.elementsDirPath($$0));
    }

    public ResourceLocation idToFile(ResourceLocation $$0) {
        return $$0.withPath(this.prefix + "/" + $$0.getPath() + this.extension);
    }

    public ResourceLocation fileToId(ResourceLocation $$0) {
        String $$1 = $$0.getPath();
        return $$0.withPath($$1.substring(this.prefix.length() + 1, $$1.length() - this.extension.length()));
    }

    public Map<ResourceLocation, Resource> listMatchingResources(ResourceManager $$02) {
        return $$02.listResources(this.prefix, $$0 -> $$0.getPath().endsWith(this.extension));
    }

    public Map<ResourceLocation, List<Resource>> listMatchingResourceStacks(ResourceManager $$02) {
        return $$02.listResourceStacks(this.prefix, $$0 -> $$0.getPath().endsWith(this.extension));
    }
}

