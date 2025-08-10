/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument
implements ArgumentType<Coordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType((Message)Component.translatable("argument.pos3d.incomplete"));
    public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType((Message)Component.translatable("argument.pos.mixed"));
    private final boolean centerCorrect;

    public Vec3Argument(boolean $$0) {
        this.centerCorrect = $$0;
    }

    public static Vec3Argument vec3() {
        return new Vec3Argument(true);
    }

    public static Vec3Argument vec3(boolean $$0) {
        return new Vec3Argument($$0);
    }

    public static Vec3 getVec3(CommandContext<CommandSourceStack> $$0, String $$1) {
        return ((Coordinates)$$0.getArgument($$1, Coordinates.class)).getPosition((CommandSourceStack)$$0.getSource());
    }

    public static Coordinates getCoordinates(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Coordinates)$$0.getArgument($$1, Coordinates.class);
    }

    public Coordinates parse(StringReader $$0) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '^') {
            return LocalCoordinates.parse($$0);
        }
        return WorldCoordinates.parseDouble($$0, this.centerCorrect);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        if ($$0.getSource() instanceof SharedSuggestionProvider) {
            Collection<SharedSuggestionProvider.TextCoordinates> $$4;
            String $$2 = $$1.getRemaining();
            if (!$$2.isEmpty() && $$2.charAt(0) == '^') {
                Set<SharedSuggestionProvider.TextCoordinates> $$3 = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
            } else {
                $$4 = ((SharedSuggestionProvider)$$0.getSource()).getAbsoluteCoordinates();
            }
            return SharedSuggestionProvider.suggestCoordinates($$2, $$4, $$1, Commands.createValidator(this::parse));
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

