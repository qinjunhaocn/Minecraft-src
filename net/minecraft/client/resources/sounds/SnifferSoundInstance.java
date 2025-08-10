/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

public class SnifferSoundInstance
extends AbstractTickableSoundInstance {
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 1.0f;
    private final Sniffer sniffer;

    public SnifferSoundInstance(Sniffer $$0) {
        super(SoundEvents.SNIFFER_DIGGING, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.sniffer = $$0;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.looping = false;
        this.delay = 0;
    }

    @Override
    public boolean canPlaySound() {
        return !this.sniffer.isSilent();
    }

    @Override
    public void tick() {
        if (this.sniffer.isRemoved() || this.sniffer.getTarget() != null || !this.sniffer.canPlayDiggingSound()) {
            this.stop();
            return;
        }
        this.x = (float)this.sniffer.getX();
        this.y = (float)this.sniffer.getY();
        this.z = (float)this.sniffer.getZ();
        this.volume = 1.0f;
        this.pitch = 1.0f;
    }
}

