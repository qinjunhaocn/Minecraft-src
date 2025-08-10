/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
    @Deprecated
    public static int[] a(ResourceManager $$0, ResourceLocation $$1) throws IOException {
        try (InputStream $$2 = $$0.open($$1);){
            NativeImage $$3 = NativeImage.read($$2);
            try {
                int[] nArray = $$3.f();
                if ($$3 != null) {
                    $$3.close();
                }
                return nArray;
            } catch (Throwable throwable) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }
}

