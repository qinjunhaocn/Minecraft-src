/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
}

