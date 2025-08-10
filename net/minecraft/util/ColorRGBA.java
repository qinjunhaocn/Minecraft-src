/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public record ColorRGBA(int rgba) {
    private static final String CUSTOM_COLOR_PREFIX = "#";
    public static final Codec<ColorRGBA> CODEC = Codec.STRING.comapFlatMap($$0 -> {
        if (!$$0.startsWith(CUSTOM_COLOR_PREFIX)) {
            return DataResult.error(() -> "Not a color code: " + $$0);
        }
        try {
            int $$1 = (int)Long.parseLong($$0.substring(1), 16);
            return DataResult.success((Object)((Object)new ColorRGBA($$1)));
        } catch (NumberFormatException $$2) {
            return DataResult.error(() -> "Exception parsing color code: " + $$2.getMessage());
        }
    }, ColorRGBA::formatValue);

    private String formatValue() {
        return String.format(Locale.ROOT, "#%08X", this.rgba);
    }

    public String toString() {
        return this.formatValue();
    }
}

