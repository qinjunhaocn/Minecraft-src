/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import java.util.function.Function;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;

public interface GlyphInfo {
    public float getAdvance();

    default public float getAdvance(boolean $$0) {
        return this.getAdvance() + ($$0 ? this.getBoldOffset() : 0.0f);
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }

    public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1);

    public static interface SpaceGlyphInfo
    extends GlyphInfo {
        @Override
        default public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return EmptyGlyph.INSTANCE;
        }
    }
}

