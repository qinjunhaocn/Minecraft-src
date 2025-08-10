/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class TagParseRule<T>
implements Rule<StringReader, Dynamic<?>> {
    private final TagParser<T> parser;

    public TagParseRule(DynamicOps<T> $$0) {
        this.parser = TagParser.create($$0);
    }

    @Override
    @Nullable
    public Dynamic<T> parse(ParseState<StringReader> $$0) {
        $$0.input().skipWhitespace();
        int $$1 = $$0.mark();
        try {
            return new Dynamic(this.parser.getOps(), this.parser.parseAsArgument($$0.input()));
        } catch (Exception $$2) {
            $$0.errorCollector().store($$1, $$2);
            return null;
        }
    }

    @Override
    @Nullable
    public /* synthetic */ Object parse(ParseState parseState) {
        return this.parse((ParseState<StringReader>)parseState);
    }
}

