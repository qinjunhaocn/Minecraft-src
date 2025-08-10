/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;

public class TeamArgument
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
    private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType($$0 -> Component.b("team.notFound", $$0));

    public static TeamArgument team() {
        return new TeamArgument();
    }

    public static PlayerTeam getTeam(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        String $$2 = (String)$$0.getArgument($$1, String.class);
        ServerScoreboard $$3 = ((CommandSourceStack)$$0.getSource()).getServer().getScoreboard();
        PlayerTeam $$4 = $$3.getPlayerTeam($$2);
        if ($$4 == null) {
            throw ERROR_TEAM_NOT_FOUND.create((Object)$$2);
        }
        return $$4;
    }

    public String parse(StringReader $$0) throws CommandSyntaxException {
        return $$0.readUnquotedString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        if ($$0.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggest(((SharedSuggestionProvider)$$0.getSource()).getAllTeams(), $$1);
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

