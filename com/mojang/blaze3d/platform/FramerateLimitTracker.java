/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package com.mojang.blaze3d.platform;

import net.minecraft.Util;
import net.minecraft.client.InactivityFpsLimit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public class FramerateLimitTracker {
    private static final int OUT_OF_LEVEL_MENU_LIMIT = 60;
    private static final int ICONIFIED_WINDOW_LIMIT = 10;
    private static final int AFK_LIMIT = 30;
    private static final int LONG_AFK_LIMIT = 10;
    private static final long AFK_THRESHOLD_MS = 60000L;
    private static final long LONG_AFK_THRESHOLD_MS = 600000L;
    private final Options options;
    private final Minecraft minecraft;
    private int framerateLimit;
    private long latestInputTime;

    public FramerateLimitTracker(Options $$0, Minecraft $$1) {
        this.options = $$0;
        this.minecraft = $$1;
        this.framerateLimit = $$0.framerateLimit().get();
    }

    public int getFramerateLimit() {
        return switch (this.getThrottleReason().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.framerateLimit;
            case 1 -> 10;
            case 2 -> 10;
            case 3 -> Math.min(this.framerateLimit, 30);
            case 4 -> 60;
        };
    }

    public FramerateThrottleReason getThrottleReason() {
        InactivityFpsLimit $$0 = this.options.inactivityFpsLimit().get();
        if (this.minecraft.getWindow().isIconified()) {
            return FramerateThrottleReason.WINDOW_ICONIFIED;
        }
        if ($$0 == InactivityFpsLimit.AFK) {
            long $$1 = Util.getMillis() - this.latestInputTime;
            if ($$1 > 600000L) {
                return FramerateThrottleReason.LONG_AFK;
            }
            if ($$1 > 60000L) {
                return FramerateThrottleReason.SHORT_AFK;
            }
        }
        if (this.minecraft.level == null && (this.minecraft.screen != null || this.minecraft.getOverlay() != null)) {
            return FramerateThrottleReason.OUT_OF_LEVEL_MENU;
        }
        return FramerateThrottleReason.NONE;
    }

    public boolean isHeavilyThrottled() {
        FramerateThrottleReason $$0 = this.getThrottleReason();
        return $$0 == FramerateThrottleReason.WINDOW_ICONIFIED || $$0 == FramerateThrottleReason.LONG_AFK;
    }

    public void setFramerateLimit(int $$0) {
        this.framerateLimit = $$0;
    }

    public void onInputReceived() {
        this.latestInputTime = Util.getMillis();
    }

    public static final class FramerateThrottleReason
    extends Enum<FramerateThrottleReason> {
        public static final /* enum */ FramerateThrottleReason NONE = new FramerateThrottleReason();
        public static final /* enum */ FramerateThrottleReason WINDOW_ICONIFIED = new FramerateThrottleReason();
        public static final /* enum */ FramerateThrottleReason LONG_AFK = new FramerateThrottleReason();
        public static final /* enum */ FramerateThrottleReason SHORT_AFK = new FramerateThrottleReason();
        public static final /* enum */ FramerateThrottleReason OUT_OF_LEVEL_MENU = new FramerateThrottleReason();
        private static final /* synthetic */ FramerateThrottleReason[] $VALUES;

        public static FramerateThrottleReason[] values() {
            return (FramerateThrottleReason[])$VALUES.clone();
        }

        public static FramerateThrottleReason valueOf(String $$0) {
            return Enum.valueOf(FramerateThrottleReason.class, $$0);
        }

        private static /* synthetic */ FramerateThrottleReason[] a() {
            return new FramerateThrottleReason[]{NONE, WINDOW_ICONIFIED, LONG_AFK, SHORT_AFK, OUT_OF_LEVEL_MENU};
        }

        static {
            $VALUES = FramerateThrottleReason.a();
        }
    }
}

