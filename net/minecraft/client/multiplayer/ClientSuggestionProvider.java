/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.PermissionSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientSuggestionProvider
implements PermissionSource,
SharedSuggestionProvider {
    private final ClientPacketListener connection;
    private final Minecraft minecraft;
    private int pendingSuggestionsId = -1;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestionsFuture;
    private final Set<String> customCompletionSuggestions = new HashSet<String>();
    private final boolean allowsRestrictedCommands;

    public ClientSuggestionProvider(ClientPacketListener $$0, Minecraft $$1, boolean $$2) {
        this.connection = $$0;
        this.minecraft = $$1;
        this.allowsRestrictedCommands = $$2;
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        ArrayList<String> $$0 = Lists.newArrayList();
        for (PlayerInfo $$1 : this.connection.getOnlinePlayers()) {
            $$0.add($$1.getProfile().getName());
        }
        return $$0;
    }

    @Override
    public Collection<String> getCustomTabSugggestions() {
        if (this.customCompletionSuggestions.isEmpty()) {
            return this.getOnlinePlayerNames();
        }
        HashSet<String> $$0 = new HashSet<String>(this.getOnlinePlayerNames());
        $$0.addAll(this.customCompletionSuggestions);
        return $$0;
    }

    @Override
    public Collection<String> getSelectedEntities() {
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            return Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.connection.scoreboard().getTeamNames();
    }

    @Override
    public Stream<ResourceLocation> getAvailableSounds() {
        return this.minecraft.getSoundManager().getAvailableSounds().stream();
    }

    @Override
    public boolean hasPermission(int $$0) {
        return this.allowsRestrictedCommands || $$0 == 0;
    }

    @Override
    public boolean allowsSelectors() {
        return this.allowsRestrictedCommands;
    }

    @Override
    public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> $$0, SharedSuggestionProvider.ElementSuggestionType $$1, SuggestionsBuilder $$22, CommandContext<?> $$3) {
        return this.registryAccess().lookup($$0).map($$2 -> {
            this.suggestRegistryElements((HolderLookup<?>)$$2, $$1, $$22);
            return $$22.buildFuture();
        }).orElseGet(() -> this.customSuggestion($$3));
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> $$0) {
        if (this.pendingSuggestionsFuture != null) {
            this.pendingSuggestionsFuture.cancel(false);
        }
        this.pendingSuggestionsFuture = new CompletableFuture();
        int $$1 = ++this.pendingSuggestionsId;
        this.connection.send(new ServerboundCommandSuggestionPacket($$1, $$0.getInput()));
        return this.pendingSuggestionsFuture;
    }

    private static String prettyPrint(double $$0) {
        return String.format(Locale.ROOT, "%.2f", $$0);
    }

    private static String prettyPrint(int $$0) {
        return Integer.toString($$0);
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 == null || $$0.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getRelevantCoordinates();
        }
        BlockPos $$1 = ((BlockHitResult)$$0).getBlockPos();
        return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint($$1.getX()), ClientSuggestionProvider.prettyPrint($$1.getY()), ClientSuggestionProvider.prettyPrint($$1.getZ())));
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 == null || $$0.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getAbsoluteCoordinates();
        }
        Vec3 $$1 = $$0.getLocation();
        return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint($$1.x), ClientSuggestionProvider.prettyPrint($$1.y), ClientSuggestionProvider.prettyPrint($$1.z)));
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.connection.levels();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.connection.enabledFeatures();
    }

    public void completeCustomSuggestions(int $$0, Suggestions $$1) {
        if ($$0 == this.pendingSuggestionsId) {
            this.pendingSuggestionsFuture.complete($$1);
            this.pendingSuggestionsFuture = null;
            this.pendingSuggestionsId = -1;
        }
    }

    public void modifyCustomCompletions(ClientboundCustomChatCompletionsPacket.Action $$0, List<String> $$1) {
        switch ($$0) {
            case ADD: {
                this.customCompletionSuggestions.addAll($$1);
                break;
            }
            case REMOVE: {
                $$1.forEach(this.customCompletionSuggestions::remove);
                break;
            }
            case SET: {
                this.customCompletionSuggestions.clear();
                this.customCompletionSuggestions.addAll($$1);
            }
        }
    }

    public boolean allowsRestrictedCommands() {
        return this.allowsRestrictedCommands;
    }
}

