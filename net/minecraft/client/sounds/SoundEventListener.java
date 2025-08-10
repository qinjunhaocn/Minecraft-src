/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;

public interface SoundEventListener {
    public void onPlaySound(SoundInstance var1, WeighedSoundEvents var2, float var3);
}

