/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 */
package net.minecraft.client;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public interface DeltaTracker {
    public static final DeltaTracker ZERO = new DefaultValue(0.0f);
    public static final DeltaTracker ONE = new DefaultValue(1.0f);

    public float getGameTimeDeltaTicks();

    public float getGameTimeDeltaPartialTick(boolean var1);

    public float getRealtimeDeltaTicks();

    public static class DefaultValue
    implements DeltaTracker {
        private final float value;

        DefaultValue(float $$0) {
            this.value = $$0;
        }

        @Override
        public float getGameTimeDeltaTicks() {
            return this.value;
        }

        @Override
        public float getGameTimeDeltaPartialTick(boolean $$0) {
            return this.value;
        }

        @Override
        public float getRealtimeDeltaTicks() {
            return this.value;
        }
    }

    public static class Timer
    implements DeltaTracker {
        private float deltaTicks;
        private float deltaTickResidual;
        private float realtimeDeltaTicks;
        private float pausedDeltaTickResidual;
        private long lastMs;
        private long lastUiMs;
        private final float msPerTick;
        private final FloatUnaryOperator targetMsptProvider;
        private boolean paused;
        private boolean frozen;

        public Timer(float $$0, long $$1, FloatUnaryOperator $$2) {
            this.msPerTick = 1000.0f / $$0;
            this.lastUiMs = this.lastMs = $$1;
            this.targetMsptProvider = $$2;
        }

        public int advanceTime(long $$0, boolean $$1) {
            this.advanceRealTime($$0);
            if ($$1) {
                return this.advanceGameTime($$0);
            }
            return 0;
        }

        private int advanceGameTime(long $$0) {
            this.deltaTicks = (float)($$0 - this.lastMs) / this.targetMsptProvider.apply(this.msPerTick);
            this.lastMs = $$0;
            this.deltaTickResidual += this.deltaTicks;
            int $$1 = (int)this.deltaTickResidual;
            this.deltaTickResidual -= (float)$$1;
            return $$1;
        }

        private void advanceRealTime(long $$0) {
            this.realtimeDeltaTicks = (float)($$0 - this.lastUiMs) / this.msPerTick;
            this.lastUiMs = $$0;
        }

        public void updatePauseState(boolean $$0) {
            if ($$0) {
                this.pause();
            } else {
                this.unPause();
            }
        }

        private void pause() {
            if (!this.paused) {
                this.pausedDeltaTickResidual = this.deltaTickResidual;
            }
            this.paused = true;
        }

        private void unPause() {
            if (this.paused) {
                this.deltaTickResidual = this.pausedDeltaTickResidual;
            }
            this.paused = false;
        }

        public void updateFrozenState(boolean $$0) {
            this.frozen = $$0;
        }

        @Override
        public float getGameTimeDeltaTicks() {
            return this.deltaTicks;
        }

        @Override
        public float getGameTimeDeltaPartialTick(boolean $$0) {
            if (!$$0 && this.frozen) {
                return 1.0f;
            }
            return this.paused ? this.pausedDeltaTickResidual : this.deltaTickResidual;
        }

        @Override
        public float getRealtimeDeltaTicks() {
            if (this.realtimeDeltaTicks > 7.0f) {
                return 0.5f;
            }
            return this.realtimeDeltaTicks;
        }
    }
}

