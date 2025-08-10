/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class ElytraOnPlayerSoundInstance
extends AbstractTickableSoundInstance {
    public static final int DELAY = 20;
    private final LocalPlayer player;
    private int time;

    public ElytraOnPlayerSoundInstance(LocalPlayer $$0) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = $$0;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1f;
    }

    @Override
    public void tick() {
        ++this.time;
        if (this.player.isRemoved() || this.time > 20 && !this.player.isFallFlying()) {
            this.stop();
            return;
        }
        this.x = (float)this.player.getX();
        this.y = (float)this.player.getY();
        this.z = (float)this.player.getZ();
        float $$0 = (float)this.player.getDeltaMovement().lengthSqr();
        this.volume = (double)$$0 >= 1.0E-7 ? Mth.clamp($$0 / 4.0f, 0.0f, 1.0f) : 0.0f;
        if (this.time < 20) {
            this.volume = 0.0f;
        } else if (this.time < 40) {
            this.volume *= (float)(this.time - 20) / 20.0f;
        }
        float $$1 = 0.8f;
        this.pitch = this.volume > 0.8f ? 1.0f + (this.volume - 0.8f) : 1.0f;
    }
}

