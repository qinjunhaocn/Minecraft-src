/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ARGB {
    public static int alpha(int $$0) {
        return $$0 >>> 24;
    }

    public static int red(int $$0) {
        return $$0 >> 16 & 0xFF;
    }

    public static int green(int $$0) {
        return $$0 >> 8 & 0xFF;
    }

    public static int blue(int $$0) {
        return $$0 & 0xFF;
    }

    public static int color(int $$0, int $$1, int $$2, int $$3) {
        return $$0 << 24 | $$1 << 16 | $$2 << 8 | $$3;
    }

    public static int color(int $$0, int $$1, int $$2) {
        return ARGB.color(255, $$0, $$1, $$2);
    }

    public static int color(Vec3 $$0) {
        return ARGB.color(ARGB.as8BitChannel((float)$$0.x()), ARGB.as8BitChannel((float)$$0.y()), ARGB.as8BitChannel((float)$$0.z()));
    }

    public static int multiply(int $$0, int $$1) {
        if ($$0 == -1) {
            return $$1;
        }
        if ($$1 == -1) {
            return $$0;
        }
        return ARGB.color(ARGB.alpha($$0) * ARGB.alpha($$1) / 255, ARGB.red($$0) * ARGB.red($$1) / 255, ARGB.green($$0) * ARGB.green($$1) / 255, ARGB.blue($$0) * ARGB.blue($$1) / 255);
    }

    public static int scaleRGB(int $$0, float $$1) {
        return ARGB.scaleRGB($$0, $$1, $$1, $$1);
    }

    public static int scaleRGB(int $$0, float $$1, float $$2, float $$3) {
        return ARGB.color(ARGB.alpha($$0), Math.clamp((long)((int)((float)ARGB.red($$0) * $$1)), (int)0, (int)255), Math.clamp((long)((int)((float)ARGB.green($$0) * $$2)), (int)0, (int)255), Math.clamp((long)((int)((float)ARGB.blue($$0) * $$3)), (int)0, (int)255));
    }

    public static int scaleRGB(int $$0, int $$1) {
        return ARGB.color(ARGB.alpha($$0), Math.clamp((long)((long)ARGB.red($$0) * (long)$$1 / 255L), (int)0, (int)255), Math.clamp((long)((long)ARGB.green($$0) * (long)$$1 / 255L), (int)0, (int)255), Math.clamp((long)((long)ARGB.blue($$0) * (long)$$1 / 255L), (int)0, (int)255));
    }

    public static int greyscale(int $$0) {
        int $$1 = (int)((float)ARGB.red($$0) * 0.3f + (float)ARGB.green($$0) * 0.59f + (float)ARGB.blue($$0) * 0.11f);
        return ARGB.color($$1, $$1, $$1);
    }

    public static int lerp(float $$0, int $$1, int $$2) {
        int $$3 = Mth.lerpInt($$0, ARGB.alpha($$1), ARGB.alpha($$2));
        int $$4 = Mth.lerpInt($$0, ARGB.red($$1), ARGB.red($$2));
        int $$5 = Mth.lerpInt($$0, ARGB.green($$1), ARGB.green($$2));
        int $$6 = Mth.lerpInt($$0, ARGB.blue($$1), ARGB.blue($$2));
        return ARGB.color($$3, $$4, $$5, $$6);
    }

    public static int opaque(int $$0) {
        return $$0 | 0xFF000000;
    }

    public static int transparent(int $$0) {
        return $$0 & 0xFFFFFF;
    }

    public static int color(int $$0, int $$1) {
        return $$0 << 24 | $$1 & 0xFFFFFF;
    }

    public static int color(float $$0, int $$1) {
        return ARGB.as8BitChannel($$0) << 24 | $$1 & 0xFFFFFF;
    }

    public static int white(float $$0) {
        return ARGB.as8BitChannel($$0) << 24 | 0xFFFFFF;
    }

    public static int colorFromFloat(float $$0, float $$1, float $$2, float $$3) {
        return ARGB.color(ARGB.as8BitChannel($$0), ARGB.as8BitChannel($$1), ARGB.as8BitChannel($$2), ARGB.as8BitChannel($$3));
    }

    public static Vector3f vector3fFromRGB24(int $$0) {
        float $$1 = (float)ARGB.red($$0) / 255.0f;
        float $$2 = (float)ARGB.green($$0) / 255.0f;
        float $$3 = (float)ARGB.blue($$0) / 255.0f;
        return new Vector3f($$1, $$2, $$3);
    }

    public static int average(int $$0, int $$1) {
        return ARGB.color((ARGB.alpha($$0) + ARGB.alpha($$1)) / 2, (ARGB.red($$0) + ARGB.red($$1)) / 2, (ARGB.green($$0) + ARGB.green($$1)) / 2, (ARGB.blue($$0) + ARGB.blue($$1)) / 2);
    }

    public static int as8BitChannel(float $$0) {
        return Mth.floor($$0 * 255.0f);
    }

    public static float alphaFloat(int $$0) {
        return ARGB.from8BitChannel(ARGB.alpha($$0));
    }

    public static float redFloat(int $$0) {
        return ARGB.from8BitChannel(ARGB.red($$0));
    }

    public static float greenFloat(int $$0) {
        return ARGB.from8BitChannel(ARGB.green($$0));
    }

    public static float blueFloat(int $$0) {
        return ARGB.from8BitChannel(ARGB.blue($$0));
    }

    private static float from8BitChannel(int $$0) {
        return (float)$$0 / 255.0f;
    }

    public static int toABGR(int $$0) {
        return $$0 & 0xFF00FF00 | ($$0 & 0xFF0000) >> 16 | ($$0 & 0xFF) << 16;
    }

    public static int fromABGR(int $$0) {
        return ARGB.toABGR($$0);
    }

    public static int setBrightness(int $$0, float $$1) {
        float $$17;
        float $$10;
        int $$2 = ARGB.red($$0);
        int $$3 = ARGB.green($$0);
        int $$4 = ARGB.blue($$0);
        int $$5 = ARGB.alpha($$0);
        int $$6 = Math.max(Math.max($$2, $$3), $$4);
        int $$7 = Math.min(Math.min($$2, $$3), $$4);
        float $$8 = $$6 - $$7;
        if ($$6 != 0) {
            float $$9 = $$8 / (float)$$6;
        } else {
            $$10 = 0.0f;
        }
        if ($$10 == 0.0f) {
            float $$11 = 0.0f;
        } else {
            float $$12 = (float)($$6 - $$2) / $$8;
            float $$13 = (float)($$6 - $$3) / $$8;
            float $$14 = (float)($$6 - $$4) / $$8;
            if ($$2 == $$6) {
                float $$15 = $$14 - $$13;
            } else if ($$3 == $$6) {
                float $$16 = 2.0f + $$12 - $$14;
            } else {
                $$17 = 4.0f + $$13 - $$12;
            }
            $$17 /= 6.0f;
            if ($$17 < 0.0f) {
                $$17 += 1.0f;
            }
        }
        if ($$10 == 0.0f) {
            $$3 = $$4 = Math.round($$1 * 255.0f);
            $$2 = $$4;
            return ARGB.color($$5, $$2, $$3, $$4);
        }
        void $$18 = ($$17 - (float)Math.floor($$17)) * 6.0f;
        void $$19 = $$18 - (float)Math.floor((double)$$18);
        float $$20 = $$1 * (1.0f - $$10);
        float $$21 = $$1 * (1.0f - $$10 * $$19);
        float $$22 = $$1 * (1.0f - $$10 * (1.0f - $$19));
        switch ((int)$$18) {
            case 0: {
                $$2 = Math.round($$1 * 255.0f);
                $$3 = Math.round($$22 * 255.0f);
                $$4 = Math.round($$20 * 255.0f);
                break;
            }
            case 1: {
                $$2 = Math.round($$21 * 255.0f);
                $$3 = Math.round($$1 * 255.0f);
                $$4 = Math.round($$20 * 255.0f);
                break;
            }
            case 2: {
                $$2 = Math.round($$20 * 255.0f);
                $$3 = Math.round($$1 * 255.0f);
                $$4 = Math.round($$22 * 255.0f);
                break;
            }
            case 3: {
                $$2 = Math.round($$20 * 255.0f);
                $$3 = Math.round($$21 * 255.0f);
                $$4 = Math.round($$1 * 255.0f);
                break;
            }
            case 4: {
                $$2 = Math.round($$22 * 255.0f);
                $$3 = Math.round($$20 * 255.0f);
                $$4 = Math.round($$1 * 255.0f);
                break;
            }
            case 5: {
                $$2 = Math.round($$1 * 255.0f);
                $$3 = Math.round($$20 * 255.0f);
                $$4 = Math.round($$21 * 255.0f);
            }
        }
        return ARGB.color($$5, $$2, $$3, $$4);
    }
}

