/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.scores;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.world.scores.ReadOnlyScoreInfo;

public class Score
implements ReadOnlyScoreInfo {
    public static final MapCodec<Score> MAP_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.INT.optionalFieldOf("Score", (Object)0).forGetter(Score::value), (App)Codec.BOOL.optionalFieldOf("Locked", (Object)false).forGetter(Score::isLocked), (App)ComponentSerialization.CODEC.optionalFieldOf("display").forGetter($$0 -> Optional.ofNullable($$0.display)), (App)NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter($$0 -> Optional.ofNullable($$0.numberFormat))).apply((Applicative)$$02, Score::new));
    private int value;
    private boolean locked = true;
    @Nullable
    private Component display;
    @Nullable
    private NumberFormat numberFormat;

    public Score() {
    }

    private Score(int $$0, boolean $$1, Optional<Component> $$2, Optional<NumberFormat> $$3) {
        this.value = $$0;
        this.locked = $$1;
        this.display = $$2.orElse(null);
        this.numberFormat = $$3.orElse(null);
    }

    @Override
    public int value() {
        return this.value;
    }

    public void value(int $$0) {
        this.value = $$0;
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean $$0) {
        this.locked = $$0;
    }

    @Nullable
    public Component display() {
        return this.display;
    }

    public void display(@Nullable Component $$0) {
        this.display = $$0;
    }

    @Override
    @Nullable
    public NumberFormat numberFormat() {
        return this.numberFormat;
    }

    public void numberFormat(@Nullable NumberFormat $$0) {
        this.numberFormat = $$0;
    }
}

