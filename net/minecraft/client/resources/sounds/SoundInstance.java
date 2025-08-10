/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public interface SoundInstance {
    public ResourceLocation getLocation();

    @Nullable
    public WeighedSoundEvents resolve(SoundManager var1);

    @Nullable
    public Sound getSound();

    public SoundSource getSource();

    public boolean isLooping();

    public boolean isRelative();

    public int getDelay();

    public float getVolume();

    public float getPitch();

    public double getX();

    public double getY();

    public double getZ();

    public Attenuation getAttenuation();

    default public boolean canStartSilent() {
        return false;
    }

    default public boolean canPlaySound() {
        return true;
    }

    public static RandomSource createUnseededRandom() {
        return RandomSource.create();
    }

    public static final class Attenuation
    extends Enum<Attenuation> {
        public static final /* enum */ Attenuation NONE = new Attenuation();
        public static final /* enum */ Attenuation LINEAR = new Attenuation();
        private static final /* synthetic */ Attenuation[] $VALUES;

        public static Attenuation[] values() {
            return (Attenuation[])$VALUES.clone();
        }

        public static Attenuation valueOf(String $$0) {
            return Enum.valueOf(Attenuation.class, $$0);
        }

        private static /* synthetic */ Attenuation[] a() {
            return new Attenuation[]{NONE, LINEAR};
        }

        static {
            $VALUES = Attenuation.a();
        }
    }
}

