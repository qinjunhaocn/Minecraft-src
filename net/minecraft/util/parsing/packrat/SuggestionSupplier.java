/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

import java.util.stream.Stream;
import net.minecraft.util.parsing.packrat.ParseState;

public interface SuggestionSupplier<S> {
    public Stream<String> possibleValues(ParseState<S> var1);

    public static <S> SuggestionSupplier<S> empty() {
        return $$0 -> Stream.empty();
    }
}

