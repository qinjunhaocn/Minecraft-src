/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument
implements ArgumentType<EntitySelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.toomany"));
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType((Message)Component.translatable("argument.player.toomany"));
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType((Message)Component.translatable("argument.player.entities"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.selector.not_allowed"));
    final boolean single;
    final boolean playersOnly;

    protected EntityArgument(boolean $$0, boolean $$1) {
        this.single = $$0;
        this.playersOnly = $$1;
    }

    public static EntityArgument entity() {
        return new EntityArgument(true, false);
    }

    public static Entity getEntity(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((EntitySelector)$$0.getArgument($$1, EntitySelector.class)).findSingleEntity((CommandSourceStack)$$0.getSource());
    }

    public static EntityArgument entities() {
        return new EntityArgument(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        Collection<? extends Entity> $$2 = EntityArgument.getOptionalEntities($$0, $$1);
        if ($$2.isEmpty()) {
            throw NO_ENTITIES_FOUND.create();
        }
        return $$2;
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((EntitySelector)$$0.getArgument($$1, EntitySelector.class)).findEntities((CommandSourceStack)$$0.getSource());
    }

    public static Collection<ServerPlayer> getOptionalPlayers(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((EntitySelector)$$0.getArgument($$1, EntitySelector.class)).findPlayers((CommandSourceStack)$$0.getSource());
    }

    public static EntityArgument player() {
        return new EntityArgument(true, true);
    }

    public static ServerPlayer getPlayer(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((EntitySelector)$$0.getArgument($$1, EntitySelector.class)).findSinglePlayer((CommandSourceStack)$$0.getSource());
    }

    public static EntityArgument players() {
        return new EntityArgument(false, true);
    }

    public static Collection<ServerPlayer> getPlayers(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        List<ServerPlayer> $$2 = ((EntitySelector)$$0.getArgument($$1, EntitySelector.class)).findPlayers((CommandSourceStack)$$0.getSource());
        if ($$2.isEmpty()) {
            throw NO_PLAYERS_FOUND.create();
        }
        return $$2;
    }

    public EntitySelector parse(StringReader $$0) throws CommandSyntaxException {
        return this.parse($$0, true);
    }

    public <S> EntitySelector parse(StringReader $$0, S $$1) throws CommandSyntaxException {
        return this.parse($$0, EntitySelectorParser.allowSelectors($$1));
    }

    private EntitySelector parse(StringReader $$0, boolean $$1) throws CommandSyntaxException {
        boolean $$2 = false;
        EntitySelectorParser $$3 = new EntitySelectorParser($$0, $$1);
        EntitySelector $$4 = $$3.parse();
        if ($$4.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                $$0.setCursor(0);
                throw ERROR_NOT_SINGLE_PLAYER.createWithContext((ImmutableStringReader)$$0);
            }
            $$0.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext((ImmutableStringReader)$$0);
        }
        if ($$4.includesEntities() && this.playersOnly && !$$4.isSelfSelector()) {
            $$0.setCursor(0);
            throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext((ImmutableStringReader)$$0);
        }
        return $$4;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$12) {
        Object object = $$0.getSource();
        if (object instanceof SharedSuggestionProvider) {
            SharedSuggestionProvider $$2 = (SharedSuggestionProvider)object;
            StringReader $$3 = new StringReader($$12.getInput());
            $$3.setCursor($$12.getStart());
            EntitySelectorParser $$4 = new EntitySelectorParser($$3, EntitySelectorParser.allowSelectors($$2));
            try {
                $$4.parse();
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return $$4.fillSuggestions($$12, $$1 -> {
                Collection<String> $$2 = $$2.getOnlinePlayerNames();
                Collection<String> $$3 = this.playersOnly ? $$2 : Iterables.concat($$2, $$2.getSelectedEntities());
                SharedSuggestionProvider.suggest($$3, $$1);
            });
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader, Object object) throws CommandSyntaxException {
        return this.parse(stringReader, object);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class Info
    implements ArgumentTypeInfo<EntityArgument, Template> {
        private static final byte FLAG_SINGLE = 1;
        private static final byte FLAG_PLAYERS_ONLY = 2;

        @Override
        public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
            int $$2 = 0;
            if ($$0.single) {
                $$2 |= 1;
            }
            if ($$0.playersOnly) {
                $$2 |= 2;
            }
            $$1.writeByte($$2);
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
            byte $$1 = $$0.readByte();
            return new Template(($$1 & 1) != 0, ($$1 & 2) != 0);
        }

        @Override
        public void serializeToJson(Template $$0, JsonObject $$1) {
            $$1.addProperty("amount", $$0.single ? "single" : "multiple");
            $$1.addProperty("type", $$0.playersOnly ? "players" : "entities");
        }

        @Override
        public Template unpack(EntityArgument $$0) {
            return new Template($$0.single, $$0.playersOnly);
        }

        @Override
        public /* synthetic */ ArgumentTypeInfo.Template deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return this.deserializeFromNetwork(friendlyByteBuf);
        }

        public final class Template
        implements ArgumentTypeInfo.Template<EntityArgument> {
            final boolean single;
            final boolean playersOnly;

            Template(boolean $$1, boolean $$2) {
                this.single = $$1;
                this.playersOnly = $$2;
            }

            @Override
            public EntityArgument instantiate(CommandBuildContext $$0) {
                return new EntityArgument(this.single, this.playersOnly);
            }

            @Override
            public ArgumentTypeInfo<EntityArgument, ?> type() {
                return Info.this;
            }

            @Override
            public /* synthetic */ ArgumentType instantiate(CommandBuildContext commandBuildContext) {
                return this.instantiate(commandBuildContext);
            }
        }
    }
}

