/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;

public abstract class TextureAtlasHolder
implements PreparableReloadListener,
AutoCloseable {
    private final TextureAtlas textureAtlas;
    private final ResourceLocation atlasInfoLocation;
    private final Set<MetadataSectionType<?>> metadataSections;

    public TextureAtlasHolder(TextureManager $$0, ResourceLocation $$1, ResourceLocation $$2) {
        this($$0, $$1, $$2, SpriteLoader.DEFAULT_METADATA_SECTIONS);
    }

    public TextureAtlasHolder(TextureManager $$0, ResourceLocation $$1, ResourceLocation $$2, Set<MetadataSectionType<?>> $$3) {
        this.atlasInfoLocation = $$2;
        this.textureAtlas = new TextureAtlas($$1);
        $$0.register(this.textureAtlas.location(), this.textureAtlas);
        this.metadataSections = $$3;
    }

    protected TextureAtlasSprite getSprite(ResourceLocation $$0) {
        return this.textureAtlas.getSprite($$0);
    }

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$1, Executor $$2, Executor $$3) {
        return ((CompletableFuture)((CompletableFuture)SpriteLoader.create(this.textureAtlas).loadAndStitch($$1, this.atlasInfoLocation, 0, $$2, this.metadataSections).thenCompose(SpriteLoader.Preparations::waitForUpload)).thenCompose($$0::wait)).thenAcceptAsync(this::apply, $$3);
    }

    private void apply(SpriteLoader.Preparations $$0) {
        try (Zone $$1 = Profiler.get().zone("upload");){
            this.textureAtlas.upload($$0);
        }
    }

    @Override
    public void close() {
        this.textureAtlas.clearTextureData();
    }
}

