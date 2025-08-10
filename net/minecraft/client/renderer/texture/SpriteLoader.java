/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.texture;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.slf4j.Logger;

public class SpriteLoader {
    public static final Set<MetadataSectionType<?>> DEFAULT_METADATA_SECTIONS = Set.of(AnimationMetadataSection.TYPE);
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;
    private final int minWidth;
    private final int minHeight;

    public SpriteLoader(ResourceLocation $$0, int $$1, int $$2, int $$3) {
        this.location = $$0;
        this.maxSupportedTextureSize = $$1;
        this.minWidth = $$2;
        this.minHeight = $$3;
    }

    public static SpriteLoader create(TextureAtlas $$0) {
        return new SpriteLoader($$0.location(), $$0.maxSupportedTextureSize(), $$0.getWidth(), $$0.getHeight());
    }

    public Preparations stitch(List<SpriteContents> $$02, int $$1, Executor $$2) {
        try (Zone $$3 = Profiler.get().zone(() -> "stitch " + String.valueOf(this.location));){
            CompletableFuture<Object> $$22;
            int $$13;
            int $$4 = this.maxSupportedTextureSize;
            Stitcher<SpriteContents> $$5 = new Stitcher<SpriteContents>($$4, $$4, $$1);
            int $$6 = Integer.MAX_VALUE;
            int $$7 = 1 << $$1;
            for (SpriteContents $$8 : $$02) {
                $$6 = Math.min($$6, Math.min($$8.width(), $$8.height()));
                int $$9 = Math.min(Integer.lowestOneBit($$8.width()), Integer.lowestOneBit($$8.height()));
                if ($$9 < $$7) {
                    LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", $$8.name(), $$8.width(), $$8.height(), Mth.log2($$7), Mth.log2($$9));
                    $$7 = $$9;
                }
                $$5.registerSprite($$8);
            }
            int $$10 = Math.min($$6, $$7);
            int $$11 = Mth.log2($$10);
            if ($$11 < $$1) {
                LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.location, $$1, $$11, $$10);
                int $$12 = $$11;
            } else {
                $$13 = $$1;
            }
            try {
                $$5.stitch();
            } catch (StitcherException $$14) {
                CrashReport $$15 = CrashReport.forThrowable($$14, "Stitching");
                CrashReportCategory $$16 = $$15.addCategory("Stitcher");
                $$16.setDetail("Sprites", $$14.getAllSprites().stream().map($$0 -> String.format(Locale.ROOT, "%s[%dx%d]", $$0.name(), $$0.width(), $$0.height())).collect(Collectors.joining(",")));
                $$16.setDetail("Max Texture Size", $$4);
                throw new ReportedException($$15);
            }
            int $$17 = Math.max($$5.getWidth(), this.minWidth);
            int $$18 = Math.max($$5.getHeight(), this.minHeight);
            Map<ResourceLocation, TextureAtlasSprite> $$19 = this.getStitchedSprites($$5, $$17, $$18);
            TextureAtlasSprite $$20 = $$19.get(MissingTextureAtlasSprite.getLocation());
            if ($$13 > 0) {
                CompletableFuture<Void> $$21 = CompletableFuture.runAsync(() -> $$19.values().forEach($$1 -> $$1.contents().increaseMipLevel($$13)), $$2);
            } else {
                $$22 = CompletableFuture.completedFuture(null);
            }
            Preparations preparations = new Preparations($$17, $$18, $$13, $$20, $$19, $$22);
            return preparations;
        }
    }

    public static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(SpriteResourceLoader $$02, List<Function<SpriteResourceLoader, SpriteContents>> $$1, Executor $$22) {
        List $$3 = $$1.stream().map($$2 -> CompletableFuture.supplyAsync(() -> (SpriteContents)$$2.apply($$02), $$22)).toList();
        return Util.sequence($$3).thenApply($$0 -> $$0.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<Preparations> loadAndStitch(ResourceManager $$0, ResourceLocation $$1, int $$2, Executor $$3) {
        return this.loadAndStitch($$0, $$1, $$2, $$3, DEFAULT_METADATA_SECTIONS);
    }

    public CompletableFuture<Preparations> loadAndStitch(ResourceManager $$0, ResourceLocation $$1, int $$22, Executor $$3, Collection<MetadataSectionType<?>> $$4) {
        SpriteResourceLoader $$5 = SpriteResourceLoader.create($$4);
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> SpriteSourceList.load($$0, $$1).list($$0), $$3).thenCompose($$2 -> SpriteLoader.runSpriteSuppliers($$5, $$2, $$3))).thenApply($$2 -> this.stitch((List<SpriteContents>)$$2, $$22, $$3));
    }

    private Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> $$0, int $$1, int $$2) {
        HashMap<ResourceLocation, TextureAtlasSprite> $$32 = new HashMap<ResourceLocation, TextureAtlasSprite>();
        $$0.gatherSprites(($$3, $$4, $$5) -> $$32.put($$3.name(), new TextureAtlasSprite(this.location, (SpriteContents)$$3, $$1, $$2, $$4, $$5)));
        return $$32;
    }

    public record Preparations(int width, int height, int mipLevel, TextureAtlasSprite missing, Map<ResourceLocation, TextureAtlasSprite> regions, CompletableFuture<Void> readyForUpload) {
        public CompletableFuture<Preparations> waitForUpload() {
            return this.readyForUpload.thenApply($$0 -> this);
        }
    }
}

