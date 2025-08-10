/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ARGB;

public class OverlayTexture
implements AutoCloseable {
    private static final int SIZE = 16;
    public static final int NO_WHITE_U = 0;
    public static final int RED_OVERLAY_V = 3;
    public static final int WHITE_OVERLAY_V = 10;
    public static final int NO_OVERLAY = OverlayTexture.pack(0, 10);
    private final DynamicTexture texture = new DynamicTexture("Entity Color Overlay", 16, 16, false);

    public OverlayTexture() {
        NativeImage $$0 = this.texture.getPixels();
        for (int $$1 = 0; $$1 < 16; ++$$1) {
            for (int $$2 = 0; $$2 < 16; ++$$2) {
                if ($$1 < 8) {
                    $$0.setPixel($$2, $$1, -1291911168);
                    continue;
                }
                int $$3 = (int)((1.0f - (float)$$2 / 15.0f * 0.75f) * 255.0f);
                $$0.setPixel($$2, $$1, ARGB.color($$3, -1));
            }
        }
        this.texture.setClamp(true);
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void setupOverlayColor() {
        RenderSystem.setupOverlayColor(this.texture.getTextureView());
    }

    public static int u(float $$0) {
        return (int)($$0 * 15.0f);
    }

    public static int v(boolean $$0) {
        return $$0 ? 3 : 10;
    }

    public static int pack(int $$0, int $$1) {
        return $$0 | $$1 << 16;
    }

    public static int pack(float $$0, boolean $$1) {
        return OverlayTexture.pack(OverlayTexture.u($$0), OverlayTexture.v($$1));
    }

    public void teardownOverlayColor() {
        RenderSystem.teardownOverlayColor();
    }
}

