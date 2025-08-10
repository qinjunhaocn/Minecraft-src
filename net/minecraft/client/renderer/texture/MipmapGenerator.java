/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.util.ARGB;

public class MipmapGenerator {
    private static final int ALPHA_CUTOUT_CUTOFF = 96;
    private static final float[] POW22 = Util.make(new float[256], $$0 -> {
        for (int $$1 = 0; $$1 < ((float[])$$0).length; ++$$1) {
            $$0[$$1] = (float)Math.pow((float)$$1 / 255.0f, 2.2);
        }
    });

    private MipmapGenerator() {
    }

    public static NativeImage[] a(NativeImage[] $$0, int $$1) {
        if ($$1 + 1 <= $$0.length) {
            return $$0;
        }
        NativeImage[] $$2 = new NativeImage[$$1 + 1];
        $$2[0] = $$0[0];
        boolean $$3 = MipmapGenerator.hasTransparentPixel($$2[0]);
        for (int $$4 = 1; $$4 <= $$1; ++$$4) {
            if ($$4 < $$0.length) {
                $$2[$$4] = $$0[$$4];
                continue;
            }
            NativeImage $$5 = $$2[$$4 - 1];
            NativeImage $$6 = new NativeImage($$5.getWidth() >> 1, $$5.getHeight() >> 1, false);
            int $$7 = $$6.getWidth();
            int $$8 = $$6.getHeight();
            for (int $$9 = 0; $$9 < $$7; ++$$9) {
                for (int $$10 = 0; $$10 < $$8; ++$$10) {
                    $$6.setPixel($$9, $$10, MipmapGenerator.alphaBlend($$5.getPixel($$9 * 2 + 0, $$10 * 2 + 0), $$5.getPixel($$9 * 2 + 1, $$10 * 2 + 0), $$5.getPixel($$9 * 2 + 0, $$10 * 2 + 1), $$5.getPixel($$9 * 2 + 1, $$10 * 2 + 1), $$3));
                }
            }
            $$2[$$4] = $$6;
        }
        return $$2;
    }

    private static boolean hasTransparentPixel(NativeImage $$0) {
        for (int $$1 = 0; $$1 < $$0.getWidth(); ++$$1) {
            for (int $$2 = 0; $$2 < $$0.getHeight(); ++$$2) {
                if (ARGB.alpha($$0.getPixel($$1, $$2)) != 0) continue;
                return true;
            }
        }
        return false;
    }

    private static int alphaBlend(int $$0, int $$1, int $$2, int $$3, boolean $$4) {
        if ($$4) {
            float $$5 = 0.0f;
            float $$6 = 0.0f;
            float $$7 = 0.0f;
            float $$8 = 0.0f;
            if ($$0 >> 24 != 0) {
                $$5 += MipmapGenerator.getPow22($$0 >> 24);
                $$6 += MipmapGenerator.getPow22($$0 >> 16);
                $$7 += MipmapGenerator.getPow22($$0 >> 8);
                $$8 += MipmapGenerator.getPow22($$0 >> 0);
            }
            if ($$1 >> 24 != 0) {
                $$5 += MipmapGenerator.getPow22($$1 >> 24);
                $$6 += MipmapGenerator.getPow22($$1 >> 16);
                $$7 += MipmapGenerator.getPow22($$1 >> 8);
                $$8 += MipmapGenerator.getPow22($$1 >> 0);
            }
            if ($$2 >> 24 != 0) {
                $$5 += MipmapGenerator.getPow22($$2 >> 24);
                $$6 += MipmapGenerator.getPow22($$2 >> 16);
                $$7 += MipmapGenerator.getPow22($$2 >> 8);
                $$8 += MipmapGenerator.getPow22($$2 >> 0);
            }
            if ($$3 >> 24 != 0) {
                $$5 += MipmapGenerator.getPow22($$3 >> 24);
                $$6 += MipmapGenerator.getPow22($$3 >> 16);
                $$7 += MipmapGenerator.getPow22($$3 >> 8);
                $$8 += MipmapGenerator.getPow22($$3 >> 0);
            }
            int $$9 = (int)(Math.pow($$5 /= 4.0f, 0.45454545454545453) * 255.0);
            int $$10 = (int)(Math.pow($$6 /= 4.0f, 0.45454545454545453) * 255.0);
            int $$11 = (int)(Math.pow($$7 /= 4.0f, 0.45454545454545453) * 255.0);
            int $$12 = (int)(Math.pow($$8 /= 4.0f, 0.45454545454545453) * 255.0);
            if ($$9 < 96) {
                $$9 = 0;
            }
            return ARGB.color($$9, $$10, $$11, $$12);
        }
        int $$13 = MipmapGenerator.gammaBlend($$0, $$1, $$2, $$3, 24);
        int $$14 = MipmapGenerator.gammaBlend($$0, $$1, $$2, $$3, 16);
        int $$15 = MipmapGenerator.gammaBlend($$0, $$1, $$2, $$3, 8);
        int $$16 = MipmapGenerator.gammaBlend($$0, $$1, $$2, $$3, 0);
        return ARGB.color($$13, $$14, $$15, $$16);
    }

    private static int gammaBlend(int $$0, int $$1, int $$2, int $$3, int $$4) {
        float $$5 = MipmapGenerator.getPow22($$0 >> $$4);
        float $$6 = MipmapGenerator.getPow22($$1 >> $$4);
        float $$7 = MipmapGenerator.getPow22($$2 >> $$4);
        float $$8 = MipmapGenerator.getPow22($$3 >> $$4);
        float $$9 = (float)((double)((float)Math.pow((double)($$5 + $$6 + $$7 + $$8) * 0.25, 0.45454545454545453)));
        return (int)((double)$$9 * 255.0);
    }

    private static float getPow22(int $$0) {
        return POW22[$$0 & 0xFF];
    }
}

