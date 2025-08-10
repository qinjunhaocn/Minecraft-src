/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;

public record MusicInfo(@Nullable Music music, float volume) {
    public MusicInfo(Music $$0) {
        this($$0, 1.0f);
    }

    public boolean canReplace(SoundInstance $$0) {
        if (this.music == null) {
            return false;
        }
        return this.music.replaceCurrentMusic() && !this.music.event().value().location().equals($$0.getLocation());
    }

    @Nullable
    public Music music() {
        return this.music;
    }
}

