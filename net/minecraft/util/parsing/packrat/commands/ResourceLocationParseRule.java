/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class ResourceLocationParseRule
implements Rule<StringReader, ResourceLocation> {
    public static final Rule<StringReader, ResourceLocation> INSTANCE = new ResourceLocationParseRule();

    private ResourceLocationParseRule() {
    }

    @Override
    @Nullable
    public ResourceLocation parse(ParseState<StringReader> $$0) {
        $$0.input().skipWhitespace();
        try {
            return ResourceLocation.readNonEmpty($$0.input());
        } catch (CommandSyntaxException $$1) {
            return null;
        }
    }

    @Override
    @Nullable
    public /* synthetic */ Object parse(ParseState parseState) {
        return this.parse(parseState);
    }
}

