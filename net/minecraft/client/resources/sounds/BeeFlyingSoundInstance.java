/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;

public class BeeFlyingSoundInstance
extends BeeSoundInstance {
    public BeeFlyingSoundInstance(Bee $$0) {
        super($$0, SoundEvents.BEE_LOOP, SoundSource.NEUTRAL);
    }

    @Override
    protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
        return new BeeAggressiveSoundInstance(this.bee);
    }

    @Override
    protected boolean shouldSwitchSounds() {
        return this.bee.isAngry();
    }
}

