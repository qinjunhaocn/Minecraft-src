/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.renderer.texture.Stitcher;

public class StitcherException
extends RuntimeException {
    private final Collection<Stitcher.Entry> allSprites;

    public StitcherException(Stitcher.Entry $$0, Collection<Stitcher.Entry> $$1) {
        super(String.format(Locale.ROOT, "Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", $$0.name(), $$0.width(), $$0.height()));
        this.allSprites = $$1;
    }

    public Collection<Stitcher.Entry> getAllSprites() {
        return this.allSprites;
    }
}

