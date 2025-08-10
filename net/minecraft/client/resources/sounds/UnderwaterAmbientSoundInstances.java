/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class UnderwaterAmbientSoundInstances {

    public static class UnderwaterAmbientSoundInstance
    extends AbstractTickableSoundInstance {
        public static final int FADE_DURATION = 40;
        private final LocalPlayer player;
        private int fade;

        public UnderwaterAmbientSoundInstance(LocalPlayer $$0) {
            super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.player = $$0;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (this.player.isRemoved() || this.fade < 0) {
                this.stop();
                return;
            }
            this.fade = this.player.isUnderWater() ? ++this.fade : (this.fade -= 2);
            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0f, Math.min((float)this.fade / 40.0f, 1.0f));
        }
    }

    public static class SubSound
    extends AbstractTickableSoundInstance {
        private final LocalPlayer player;

        protected SubSound(LocalPlayer $$0, SoundEvent $$1) {
            super($$1, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.player = $$0;
            this.looping = false;
            this.delay = 0;
            this.volume = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (this.player.isRemoved() || !this.player.isUnderWater()) {
                this.stop();
            }
        }
    }
}

