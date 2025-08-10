/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.color;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

public class ColorLerper {
    public static final DyeColor[] MUSIC_NOTE_COLORS = new DyeColor[]{DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.CYAN, DyeColor.GREEN, DyeColor.LIME, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PINK, DyeColor.RED, DyeColor.MAGENTA};

    public static int getLerpedColor(Type $$0, float $$1) {
        int $$2 = Mth.floor($$1);
        int $$3 = $$2 / $$0.colorDuration;
        int $$4 = $$0.colors.length;
        int $$5 = $$3 % $$4;
        int $$6 = ($$3 + 1) % $$4;
        float $$7 = ((float)($$2 % $$0.colorDuration) + Mth.frac($$1)) / (float)$$0.colorDuration;
        int $$8 = $$0.getColor($$0.colors[$$5]);
        int $$9 = $$0.getColor($$0.colors[$$6]);
        return ARGB.lerp($$7, $$8, $$9);
    }

    static int getModifiedColor(DyeColor $$0, float $$1) {
        if ($$0 == DyeColor.WHITE) {
            return -1644826;
        }
        int $$2 = $$0.getTextureDiffuseColor();
        return ARGB.color(255, Mth.floor((float)ARGB.red($$2) * $$1), Mth.floor((float)ARGB.green($$2) * $$1), Mth.floor((float)ARGB.blue($$2) * $$1));
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type SHEEP = new Type(25, DyeColor.values(), 0.75f);
        public static final /* enum */ Type MUSIC_NOTE = new Type(30, MUSIC_NOTE_COLORS, 1.25f);
        final int colorDuration;
        private final Map<DyeColor, Integer> colorByDye;
        final DyeColor[] colors;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(int $$02, DyeColor[] $$12, float $$2) {
            this.colorDuration = $$02;
            this.colorByDye = Maps.newHashMap(Arrays.stream($$12).collect(Collectors.toMap($$0 -> $$0, $$1 -> ColorLerper.getModifiedColor($$1, $$2))));
            this.colors = $$12;
        }

        public final int getColor(DyeColor $$0) {
            return this.colorByDye.get($$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{SHEEP, MUSIC_NOTE};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

