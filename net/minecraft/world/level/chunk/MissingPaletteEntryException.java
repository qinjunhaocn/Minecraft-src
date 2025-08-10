/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk;

public class MissingPaletteEntryException
extends RuntimeException {
    public MissingPaletteEntryException(int $$0) {
        super("Missing Palette entry for index " + $$0 + ".");
    }
}

