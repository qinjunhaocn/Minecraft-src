/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasSet
implements AutoCloseable {
    private final Map<ResourceLocation, AtlasEntry> atlases;

    public AtlasSet(Map<ResourceLocation, ResourceLocation> $$0, TextureManager $$12) {
        this.atlases = $$0.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$1 -> {
            TextureAtlas $$2 = new TextureAtlas((ResourceLocation)$$1.getKey());
            $$12.register((ResourceLocation)$$1.getKey(), $$2);
            return new AtlasEntry($$2, (ResourceLocation)$$1.getValue());
        }));
    }

    public TextureAtlas getAtlas(ResourceLocation $$0) {
        return this.atlases.get($$0).atlas();
    }

    @Override
    public void close() {
        this.atlases.values().forEach(AtlasEntry::close);
        this.atlases.clear();
    }

    public Map<ResourceLocation, CompletableFuture<StitchResult>> scheduleLoad(ResourceManager $$0, int $$1, Executor $$2) {
        return Util.mapValues(this.atlases, $$3 -> SpriteLoader.create($$3.atlas).loadAndStitch($$0, $$3.atlasInfoLocation, $$1, $$2).thenApply($$1 -> new StitchResult($$0.atlas, (SpriteLoader.Preparations)((Object)((Object)$$1)))));
    }

    static final class AtlasEntry
    extends Record
    implements AutoCloseable {
        final TextureAtlas atlas;
        final ResourceLocation atlasInfoLocation;

        AtlasEntry(TextureAtlas $$0, ResourceLocation $$1) {
            this.atlas = $$0;
            this.atlasInfoLocation = $$1;
        }

        @Override
        public void close() {
            this.atlas.clearTextureData();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{AtlasEntry.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AtlasEntry.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AtlasEntry.class, "atlas;atlasInfoLocation", "atlas", "atlasInfoLocation"}, this, $$0);
        }

        public TextureAtlas atlas() {
            return this.atlas;
        }

        public ResourceLocation atlasInfoLocation() {
            return this.atlasInfoLocation;
        }
    }

    public static class StitchResult {
        private final TextureAtlas atlas;
        private final SpriteLoader.Preparations preparations;

        public StitchResult(TextureAtlas $$0, SpriteLoader.Preparations $$1) {
            this.atlas = $$0;
            this.preparations = $$1;
        }

        @Nullable
        public TextureAtlasSprite getSprite(ResourceLocation $$0) {
            return this.preparations.regions().get($$0);
        }

        public TextureAtlasSprite missing() {
            return this.preparations.missing();
        }

        public CompletableFuture<Void> readyForUpload() {
            return this.preparations.readyForUpload();
        }

        public void upload() {
            this.atlas.upload(this.preparations);
        }
    }
}

