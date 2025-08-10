/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public record PlayerScoreEntry(String owner, int value, @Nullable Component display, @Nullable NumberFormat numberFormatOverride) {
    public boolean isHidden() {
        return this.owner.startsWith("#");
    }

    public Component ownerName() {
        if (this.display != null) {
            return this.display;
        }
        return Component.literal(this.owner());
    }

    public MutableComponent formatValue(NumberFormat $$0) {
        return ((NumberFormat)Objects.requireNonNullElse((Object)this.numberFormatOverride, (Object)$$0)).format(this.value);
    }

    @Nullable
    public Component display() {
        return this.display;
    }

    @Nullable
    public NumberFormat numberFormatOverride() {
        return this.numberFormatOverride;
    }
}

