/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class AbstractSoundInstance
implements SoundInstance {
    @Nullable
    protected Sound sound;
    protected final SoundSource source;
    protected final ResourceLocation location;
    protected float volume = 1.0f;
    protected float pitch = 1.0f;
    protected double x;
    protected double y;
    protected double z;
    protected boolean looping;
    protected int delay;
    protected SoundInstance.Attenuation attenuation = SoundInstance.Attenuation.LINEAR;
    protected boolean relative;
    protected RandomSource random;

    protected AbstractSoundInstance(SoundEvent $$0, SoundSource $$1, RandomSource $$2) {
        this($$0.location(), $$1, $$2);
    }

    protected AbstractSoundInstance(ResourceLocation $$0, SoundSource $$1, RandomSource $$2) {
        this.location = $$0;
        this.source = $$1;
        this.random = $$2;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.location;
    }

    @Override
    @Nullable
    public WeighedSoundEvents resolve(SoundManager $$0) {
        if (this.location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
            this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
            return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
        }
        WeighedSoundEvents $$1 = $$0.getSoundEvent(this.location);
        this.sound = $$1 == null ? SoundManager.EMPTY_SOUND : $$1.getSound(this.random);
        return $$1;
    }

    @Override
    @Nullable
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundSource getSource() {
        return this.source;
    }

    @Override
    public boolean isLooping() {
        return this.looping;
    }

    @Override
    public int getDelay() {
        return this.delay;
    }

    @Override
    public float getVolume() {
        return this.volume * this.sound.getVolume().sample(this.random);
    }

    @Override
    public float getPitch() {
        return this.pitch * this.sound.getPitch().sample(this.random);
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public SoundInstance.Attenuation getAttenuation() {
        return this.attenuation;
    }

    @Override
    public boolean isRelative() {
        return this.relative;
    }

    public String toString() {
        return "SoundInstance[" + String.valueOf(this.location) + "]";
    }
}

