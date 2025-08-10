/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.sounds;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public record Music(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
    public static final Codec<Music> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter($$0 -> $$0.event), (App)Codec.INT.fieldOf("min_delay").forGetter($$0 -> $$0.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter($$0 -> $$0.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter($$0 -> $$0.replaceCurrentMusic)).apply((Applicative)$$02, Music::new));
}

