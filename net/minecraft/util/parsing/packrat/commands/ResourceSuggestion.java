/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 */
package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public interface ResourceSuggestion
extends SuggestionSupplier<StringReader> {
    public Stream<ResourceLocation> possibleResources();

    @Override
    default public Stream<String> possibleValues(ParseState<StringReader> $$0) {
        return this.possibleResources().map(ResourceLocation::toString);
    }
}

