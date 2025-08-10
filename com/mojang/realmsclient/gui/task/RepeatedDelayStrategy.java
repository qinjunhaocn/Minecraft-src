/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.task;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public interface RepeatedDelayStrategy {
    public static final RepeatedDelayStrategy CONSTANT = new RepeatedDelayStrategy(){

        @Override
        public long delayCyclesAfterSuccess() {
            return 1L;
        }

        @Override
        public long delayCyclesAfterFailure() {
            return 1L;
        }
    };

    public long delayCyclesAfterSuccess();

    public long delayCyclesAfterFailure();

    public static RepeatedDelayStrategy exponentialBackoff(final int $$0) {
        return new RepeatedDelayStrategy(){
            private static final Logger LOGGER = LogUtils.getLogger();
            private int failureCount;

            @Override
            public long delayCyclesAfterSuccess() {
                this.failureCount = 0;
                return 1L;
            }

            @Override
            public long delayCyclesAfterFailure() {
                ++this.failureCount;
                long $$02 = Math.min(1L << this.failureCount, (long)$$0);
                LOGGER.debug("Skipping for {} extra cycles", (Object)$$02);
                return $$02;
            }
        };
    }
}

