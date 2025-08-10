/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.util.RandomSource;

public interface Weighted<T> {
    public int getWeight();

    public T getSound(RandomSource var1);

    public void preloadIfRequired(SoundEngine var1);
}

