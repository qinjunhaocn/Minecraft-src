/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphInfo;
import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.FontOption;

public interface GlyphProvider
extends AutoCloseable {
    public static final float BASELINE = 7.0f;

    @Override
    default public void close() {
    }

    @Nullable
    default public GlyphInfo getGlyph(int $$0) {
        return null;
    }

    public IntSet getSupportedGlyphs();

    public record Conditional(GlyphProvider provider, FontOption.Filter filter) implements AutoCloseable
    {
        @Override
        public void close() {
            this.provider.close();
        }
    }
}

