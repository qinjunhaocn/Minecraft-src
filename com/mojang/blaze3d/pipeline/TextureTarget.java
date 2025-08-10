/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;

public class TextureTarget
extends RenderTarget {
    public TextureTarget(@Nullable String $$0, int $$1, int $$2, boolean $$3) {
        super($$0, $$3);
        RenderSystem.assertOnRenderThread();
        this.resize($$1, $$2);
    }
}

