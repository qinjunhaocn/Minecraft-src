/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.sounds;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public class MusicManager {
    private static final int STARTING_DELAY = 100;
    private final RandomSource random = RandomSource.create();
    private final Minecraft minecraft;
    @Nullable
    private SoundInstance currentMusic;
    private MusicFrequency gameMusicFrequency;
    private float currentGain = 1.0f;
    private int nextSongDelay = 100;
    private boolean toastShown = false;

    public MusicManager(Minecraft $$0) {
        this.minecraft = $$0;
        this.gameMusicFrequency = $$0.options.musicFrequency().get();
    }

    public void tick() {
        boolean $$2;
        MusicInfo $$0 = this.minecraft.getSituationalMusic();
        float $$1 = $$0.volume();
        if (this.currentMusic != null && this.currentGain != $$1 && !($$2 = this.fadePlaying($$1))) {
            return;
        }
        Music $$3 = $$0.music();
        if ($$3 == null) {
            this.nextSongDelay = Math.max(this.nextSongDelay, 100);
            return;
        }
        if (this.currentMusic != null) {
            if ($$0.canReplace(this.currentMusic)) {
                this.minecraft.getSoundManager().stop(this.currentMusic);
                this.nextSongDelay = Mth.nextInt(this.random, 0, $$3.minDelay() / 2);
            }
            if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
                this.currentMusic = null;
                this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay($$3, this.random));
            }
        }
        this.nextSongDelay = Math.min(this.nextSongDelay, this.gameMusicFrequency.getNextSongDelay($$3, this.random));
        if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
            this.startPlaying($$0);
        }
    }

    public void startPlaying(MusicInfo $$0) {
        SoundEvent $$1 = $$0.music().event().value();
        this.currentMusic = SimpleSoundInstance.forMusic($$1, $$0.volume());
        switch (this.minecraft.getSoundManager().play(this.currentMusic)) {
            case STARTED: {
                this.minecraft.getToastManager().showNowPlayingToast();
                this.toastShown = true;
                break;
            }
            case STARTED_SILENTLY: {
                this.toastShown = false;
            }
        }
        this.nextSongDelay = Integer.MAX_VALUE;
        this.currentGain = $$0.volume();
    }

    public void showNowPlayingToastIfNeeded() {
        if (!this.toastShown) {
            this.minecraft.getToastManager().showNowPlayingToast();
            this.toastShown = true;
        }
    }

    public void stopPlaying(Music $$0) {
        if (this.isPlayingMusic($$0)) {
            this.stopPlaying();
        }
    }

    public void stopPlaying() {
        if (this.currentMusic != null) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.currentMusic = null;
            this.minecraft.getToastManager().hideNowPlayingToast();
        }
        this.nextSongDelay += 100;
    }

    private boolean fadePlaying(float $$0) {
        if (this.currentMusic == null) {
            return false;
        }
        if (this.currentGain == $$0) {
            return true;
        }
        if (this.currentGain < $$0) {
            this.currentGain += Mth.clamp(this.currentGain, 5.0E-4f, 0.005f);
            if (this.currentGain > $$0) {
                this.currentGain = $$0;
            }
        } else {
            this.currentGain = 0.03f * $$0 + 0.97f * this.currentGain;
            if (Math.abs(this.currentGain - $$0) < 1.0E-4f || this.currentGain < $$0) {
                this.currentGain = $$0;
            }
        }
        this.currentGain = Mth.clamp(this.currentGain, 0.0f, 1.0f);
        if (this.currentGain <= 1.0E-4f) {
            this.stopPlaying();
            return false;
        }
        this.minecraft.getSoundManager().setVolume(this.currentMusic, this.currentGain);
        return true;
    }

    public boolean isPlayingMusic(Music $$0) {
        if (this.currentMusic == null) {
            return false;
        }
        return $$0.event().value().location().equals(this.currentMusic.getLocation());
    }

    @Nullable
    public String getCurrentMusicTranslationKey() {
        Sound $$0;
        if (this.currentMusic != null && ($$0 = this.currentMusic.getSound()) != null) {
            return $$0.getLocation().toShortLanguageKey();
        }
        return null;
    }

    public void setMinutesBetweenSongs(MusicFrequency $$0) {
        this.gameMusicFrequency = $$0;
        this.nextSongDelay = this.gameMusicFrequency.getNextSongDelay(this.minecraft.getSituationalMusic().music(), this.random);
    }

    public static final class MusicFrequency
    extends Enum<MusicFrequency>
    implements OptionEnum,
    StringRepresentable {
        public static final /* enum */ MusicFrequency DEFAULT = new MusicFrequency(20);
        public static final /* enum */ MusicFrequency FREQUENT = new MusicFrequency(10);
        public static final /* enum */ MusicFrequency CONSTANT = new MusicFrequency(0);
        public static final Codec<MusicFrequency> CODEC;
        private static final String KEY_PREPEND = "options.music_frequency.";
        private final int id;
        private final int maxFrequency;
        private final String key;
        private static final /* synthetic */ MusicFrequency[] $VALUES;

        public static MusicFrequency[] values() {
            return (MusicFrequency[])$VALUES.clone();
        }

        public static MusicFrequency valueOf(String $$0) {
            return Enum.valueOf(MusicFrequency.class, $$0);
        }

        private MusicFrequency(int $$0) {
            this.id = $$0;
            this.maxFrequency = $$0 * 1200;
            this.key = KEY_PREPEND + this.name().toLowerCase();
        }

        int getNextSongDelay(@Nullable Music $$0, RandomSource $$1) {
            if ($$0 == null) {
                return this.maxFrequency;
            }
            if (this == CONSTANT) {
                return 100;
            }
            int $$2 = Math.min($$0.minDelay(), this.maxFrequency);
            int $$3 = Math.min($$0.maxDelay(), this.maxFrequency);
            return Mth.nextInt($$1, $$2, $$3);
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getSerializedName() {
            return this.name();
        }

        private static /* synthetic */ MusicFrequency[] e() {
            return new MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
        }

        static {
            $VALUES = MusicFrequency.e();
            CODEC = StringRepresentable.fromEnum(MusicFrequency::values);
        }
    }
}

