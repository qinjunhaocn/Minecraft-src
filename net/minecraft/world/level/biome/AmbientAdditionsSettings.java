/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class AmbientAdditionsSettings {
    public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter($$0 -> $$0.soundEvent), (App)Codec.DOUBLE.fieldOf("tick_chance").forGetter($$0 -> $$0.tickChance)).apply((Applicative)$$02, AmbientAdditionsSettings::new));
    private final Holder<SoundEvent> soundEvent;
    private final double tickChance;

    public AmbientAdditionsSettings(Holder<SoundEvent> $$0, double $$1) {
        this.soundEvent = $$0;
        this.tickChance = $$1;
    }

    public Holder<SoundEvent> getSoundEvent() {
        return this.soundEvent;
    }

    public double getTickChance() {
        return this.tickChance;
    }
}

