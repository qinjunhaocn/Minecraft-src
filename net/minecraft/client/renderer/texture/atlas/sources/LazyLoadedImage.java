/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class LazyLoadedImage {
    private final ResourceLocation id;
    private final Resource resource;
    private final AtomicReference<NativeImage> image = new AtomicReference();
    private final AtomicInteger referenceCount;

    public LazyLoadedImage(ResourceLocation $$0, Resource $$1, int $$2) {
        this.id = $$0;
        this.resource = $$1;
        this.referenceCount = new AtomicInteger($$2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NativeImage get() throws IOException {
        NativeImage $$0 = this.image.get();
        if ($$0 == null) {
            LazyLoadedImage lazyLoadedImage = this;
            synchronized (lazyLoadedImage) {
                $$0 = this.image.get();
                if ($$0 == null) {
                    try (InputStream $$1 = this.resource.open();){
                        $$0 = NativeImage.read($$1);
                        this.image.set($$0);
                    } catch (IOException $$2) {
                        throw new IOException("Failed to load image " + String.valueOf(this.id), $$2);
                    }
                }
            }
        }
        return $$0;
    }

    public void release() {
        NativeImage $$1;
        int $$0 = this.referenceCount.decrementAndGet();
        if ($$0 <= 0 && ($$1 = (NativeImage)this.image.getAndSet(null)) != null) {
            $$1.close();
        }
    }
}

