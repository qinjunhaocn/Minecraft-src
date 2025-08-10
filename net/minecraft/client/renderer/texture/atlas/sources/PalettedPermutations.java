/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public record PalettedPermutations(List<ResourceLocation> textures, ResourceLocation paletteKey, Map<String, ResourceLocation> permutations, String separator) implements SpriteSource
{
    static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_SEPARATOR = "_";
    public static final MapCodec<PalettedPermutations> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter(PalettedPermutations::textures), (App)ResourceLocation.CODEC.fieldOf("palette_key").forGetter(PalettedPermutations::paletteKey), (App)Codec.unboundedMap((Codec)Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter(PalettedPermutations::permutations), (App)Codec.STRING.optionalFieldOf("separator", (Object)DEFAULT_SEPARATOR).forGetter(PalettedPermutations::separator)).apply((Applicative)$$0, PalettedPermutations::new));

    public PalettedPermutations(List<ResourceLocation> $$0, ResourceLocation $$1, Map<String, ResourceLocation> $$2) {
        this($$0, $$1, $$2, DEFAULT_SEPARATOR);
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        Supplier<int[]> $$2 = Suppliers.memoize(() -> PalettedPermutations.a($$0, this.paletteKey));
        HashMap $$32 = new HashMap();
        this.permutations.forEach(($$3, $$4) -> $$32.put($$3, Suppliers.memoize(() -> PalettedPermutations.lambda$run$2((java.util.function.Supplier)$$2, $$0, $$4))));
        for (ResourceLocation $$42 : this.textures) {
            ResourceLocation $$5 = TEXTURE_ID_CONVERTER.idToFile($$42);
            Optional<Resource> $$6 = $$0.getResource($$5);
            if ($$6.isEmpty()) {
                LOGGER.warn("Unable to find texture {}", (Object)$$5);
                continue;
            }
            LazyLoadedImage $$7 = new LazyLoadedImage($$5, $$6.get(), $$32.size());
            for (Map.Entry $$8 : $$32.entrySet()) {
                ResourceLocation $$9 = $$42.withSuffix(this.separator + (String)$$8.getKey());
                $$1.add($$9, new PalettedSpriteSupplier($$7, (java.util.function.Supplier)$$8.getValue(), $$9));
            }
        }
    }

    private static IntUnaryOperator a(int[] $$0, int[] $$1) {
        if ($$1.length != $$0.length) {
            LOGGER.warn("Palette mapping has different sizes: {} and {}", (Object)$$0.length, (Object)$$1.length);
            throw new IllegalArgumentException();
        }
        Int2IntOpenHashMap $$2 = new Int2IntOpenHashMap($$1.length);
        for (int $$3 = 0; $$3 < $$0.length; ++$$3) {
            int $$4 = $$0[$$3];
            if (ARGB.alpha($$4) == 0) continue;
            $$2.put(ARGB.transparent($$4), $$1[$$3]);
        }
        return arg_0 -> PalettedPermutations.lambda$createPaletteMapping$4((Int2IntMap)$$2, arg_0);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static int[] a(ResourceManager $$0, ResourceLocation $$1) {
        Optional<Resource> $$2 = $$0.getResource(TEXTURE_ID_CONVERTER.idToFile($$1));
        if ($$2.isEmpty()) {
            LOGGER.error("Failed to load palette image {}", (Object)$$1);
            throw new IllegalArgumentException();
        }
        try (InputStream $$3 = $$2.get().open();){
            NativeImage $$4 = NativeImage.read($$3);
            try {
                int[] nArray = $$4.e();
                if ($$4 != null) {
                    $$4.close();
                }
                return nArray;
            } catch (Throwable throwable) {
                if ($$4 != null) {
                    try {
                        $$4.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        } catch (Exception $$5) {
            LOGGER.error("Couldn't load texture {}", (Object)$$1, (Object)$$5);
            throw new IllegalArgumentException();
        }
    }

    public MapCodec<PalettedPermutations> codec() {
        return MAP_CODEC;
    }

    private static /* synthetic */ int lambda$createPaletteMapping$4(Int2IntMap $$0, int $$1) {
        int $$2 = ARGB.alpha($$1);
        if ($$2 == 0) {
            return $$1;
        }
        int $$3 = ARGB.transparent($$1);
        int $$4 = $$0.getOrDefault($$3, ARGB.opaque($$3));
        int $$5 = ARGB.alpha($$4);
        return ARGB.color($$2 * $$5 / 255, $$4);
    }

    private static /* synthetic */ IntUnaryOperator lambda$run$2(java.util.function.Supplier $$0, ResourceManager $$1, ResourceLocation $$2) {
        return PalettedPermutations.a((int[])$$0.get(), PalettedPermutations.a($$1, $$2));
    }

    record PalettedSpriteSupplier(LazyLoadedImage baseImage, java.util.function.Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier
    {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public SpriteContents apply(SpriteResourceLoader $$0) {
            try {
                NativeImage $$1 = this.baseImage.get().mappedCopy(this.palette.get());
                SpriteContents spriteContents = new SpriteContents(this.permutationLocation, new FrameSize($$1.getWidth(), $$1.getHeight()), $$1, ResourceMetadata.EMPTY);
                return spriteContents;
            } catch (IOException | IllegalArgumentException $$2) {
                LOGGER.error("unable to apply palette to {}", (Object)this.permutationLocation, (Object)$$2);
                SpriteContents spriteContents = null;
                return spriteContents;
            } finally {
                this.baseImage.release();
            }
        }

        @Override
        public void discard() {
            this.baseImage.release();
        }

        @Override
        @Nullable
        public /* synthetic */ Object apply(Object object) {
            return this.apply((SpriteResourceLoader)object);
        }
    }
}

