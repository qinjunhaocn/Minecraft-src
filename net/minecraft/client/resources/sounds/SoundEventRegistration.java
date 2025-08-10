/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;

public class SoundEventRegistration {
    private final List<Sound> sounds;
    private final boolean replace;
    @Nullable
    private final String subtitle;

    public SoundEventRegistration(List<Sound> $$0, boolean $$1, @Nullable String $$2) {
        this.sounds = $$0;
        this.replace = $$1;
        this.subtitle = $$2;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean isReplace() {
        return this.replace;
    }

    @Nullable
    public String getSubtitle() {
        return this.subtitle;
    }
}

