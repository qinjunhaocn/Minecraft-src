/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.platform;

import java.util.OptionalInt;

public record DisplayData(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean isFullscreen) {
    public DisplayData withSize(int $$0, int $$1) {
        return new DisplayData($$0, $$1, this.fullscreenWidth, this.fullscreenHeight, this.isFullscreen);
    }

    public DisplayData withFullscreen(boolean $$0) {
        return new DisplayData(this.width, this.height, this.fullscreenWidth, this.fullscreenHeight, $$0);
    }
}

