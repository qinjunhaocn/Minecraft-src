/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

public class LoadingDotsText {
    private static final String[] FRAMES = new String[]{"O o o", "o O o", "o o O", "o O o"};
    private static final long INTERVAL_MS = 300L;

    public static String get(long $$0) {
        int $$1 = (int)($$0 / 300L % (long)FRAMES.length);
        return FRAMES[$$1];
    }
}

