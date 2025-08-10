/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public interface ReadOnlyScoreInfo {
    public int value();

    public boolean isLocked();

    @Nullable
    public NumberFormat numberFormat();

    default public MutableComponent formatValue(NumberFormat $$0) {
        return ((NumberFormat)Objects.requireNonNullElse((Object)this.numberFormat(), (Object)$$0)).format(this.value());
    }

    public static MutableComponent safeFormatValue(@Nullable ReadOnlyScoreInfo $$0, NumberFormat $$1) {
        return $$0 != null ? $$0.formatValue($$1) : $$1.format(0);
    }
}

