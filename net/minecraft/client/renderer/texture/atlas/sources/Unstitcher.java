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
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public record Unstitcher(ResourceLocation resource, List<Region> regions, double xDivisor, double yDivisor) implements SpriteSource
{
    static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<Unstitcher> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("resource").forGetter(Unstitcher::resource), (App)ExtraCodecs.nonEmptyList(Region.CODEC.listOf()).fieldOf("regions").forGetter(Unstitcher::regions), (App)Codec.DOUBLE.optionalFieldOf("divisor_x", (Object)1.0).forGetter(Unstitcher::xDivisor), (App)Codec.DOUBLE.optionalFieldOf("divisor_y", (Object)1.0).forGetter(Unstitcher::yDivisor)).apply((Applicative)$$0, Unstitcher::new));

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        ResourceLocation $$2 = TEXTURE_ID_CONVERTER.idToFile(this.resource);
        Optional<Resource> $$3 = $$0.getResource($$2);
        if ($$3.isPresent()) {
            LazyLoadedImage $$4 = new LazyLoadedImage($$2, $$3.get(), this.regions.size());
            for (Region $$5 : this.regions) {
                $$1.add($$5.sprite, new RegionInstance($$4, $$5, this.xDivisor, this.yDivisor));
            }
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)$$2);
        }
    }

    public MapCodec<Unstitcher> codec() {
        return MAP_CODEC;
    }

    public static final class Region
    extends Record {
        final ResourceLocation sprite;
        final double x;
        final double y;
        final double width;
        final double height;
        public static final Codec<Region> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("sprite").forGetter(Region::sprite), (App)Codec.DOUBLE.fieldOf("x").forGetter(Region::x), (App)Codec.DOUBLE.fieldOf("y").forGetter(Region::y), (App)Codec.DOUBLE.fieldOf("width").forGetter(Region::width), (App)Codec.DOUBLE.fieldOf("height").forGetter(Region::height)).apply((Applicative)$$0, Region::new));

        public Region(ResourceLocation $$0, double $$1, double $$2, double $$3, double $$4) {
            this.sprite = $$0;
            this.x = $$1;
            this.y = $$2;
            this.width = $$3;
            this.height = $$4;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Region.class, "sprite;x;y;width;height", "sprite", "x", "y", "width", "height"}, this, $$0);
        }

        public ResourceLocation sprite() {
            return this.sprite;
        }

        public double x() {
            return this.x;
        }

        public double y() {
            return this.y;
        }

        public double width() {
            return this.width;
        }

        public double height() {
            return this.height;
        }
    }

    static class RegionInstance
    implements SpriteSource.SpriteSupplier {
        private final LazyLoadedImage image;
        private final Region region;
        private final double xDivisor;
        private final double yDivisor;

        RegionInstance(LazyLoadedImage $$0, Region $$1, double $$2, double $$3) {
            this.image = $$0;
            this.region = $$1;
            this.xDivisor = $$2;
            this.yDivisor = $$3;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SpriteContents apply(SpriteResourceLoader $$0) {
            try {
                NativeImage $$1 = this.image.get();
                double $$2 = (double)$$1.getWidth() / this.xDivisor;
                double $$3 = (double)$$1.getHeight() / this.yDivisor;
                int $$4 = Mth.floor(this.region.x * $$2);
                int $$5 = Mth.floor(this.region.y * $$3);
                int $$6 = Mth.floor(this.region.width * $$2);
                int $$7 = Mth.floor(this.region.height * $$3);
                NativeImage $$8 = new NativeImage(NativeImage.Format.RGBA, $$6, $$7, false);
                $$1.copyRect($$8, $$4, $$5, 0, 0, $$6, $$7, false, false);
                SpriteContents spriteContents = new SpriteContents(this.region.sprite, new FrameSize($$6, $$7), $$8, ResourceMetadata.EMPTY);
                return spriteContents;
            } catch (Exception $$9) {
                LOGGER.error("Failed to unstitch region {}", (Object)this.region.sprite, (Object)$$9);
            } finally {
                this.image.release();
            }
            return MissingTextureAtlasSprite.create();
        }

        @Override
        public void discard() {
            this.image.release();
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((SpriteResourceLoader)object);
        }
    }
}

