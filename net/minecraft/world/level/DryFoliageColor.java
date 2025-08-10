/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.world.level.ColorMapColorUtil;

public class DryFoliageColor {
    public static final int FOLIAGE_DRY_DEFAULT = -10732494;
    private static int[] pixels = new int[65536];

    public static void a(int[] $$0) {
        pixels = $$0;
    }

    public static int get(double $$0, double $$1) {
        return ColorMapColorUtil.a($$0, $$1, pixels, -10732494);
    }
}

