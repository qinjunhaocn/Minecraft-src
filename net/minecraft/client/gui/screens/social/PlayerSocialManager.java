/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.UserApiService
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PlayerSocialManager {
    private final Minecraft minecraft;
    private final Set<UUID> hiddenPlayers = Sets.newHashSet();
    private final UserApiService service;
    private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();
    private boolean onlineMode;
    private CompletableFuture<?> pendingBlockListRefresh = CompletableFuture.completedFuture(null);

    public PlayerSocialManager(Minecraft $$0, UserApiService $$1) {
        this.minecraft = $$0;
        this.service = $$1;
    }

    public void hidePlayer(UUID $$0) {
        this.hiddenPlayers.add($$0);
    }

    public void showPlayer(UUID $$0) {
        this.hiddenPlayers.remove($$0);
    }

    public boolean shouldHideMessageFrom(UUID $$0) {
        return this.isHidden($$0) || this.isBlocked($$0);
    }

    public boolean isHidden(UUID $$0) {
        return this.hiddenPlayers.contains($$0);
    }

    public void startOnlineMode() {
        this.onlineMode = true;
        this.pendingBlockListRefresh = this.pendingBlockListRefresh.thenRunAsync(() -> ((UserApiService)this.service).refreshBlockList(), Util.ioPool());
    }

    public void stopOnlineMode() {
        this.onlineMode = false;
    }

    public boolean isBlocked(UUID $$0) {
        if (!this.onlineMode) {
            return false;
        }
        this.pendingBlockListRefresh.join();
        return this.service.isBlockedPlayer($$0);
    }

    public Set<UUID> getHiddenPlayers() {
        return this.hiddenPlayers;
    }

    public UUID getDiscoveredUUID(String $$0) {
        return this.discoveredNamesToUUID.getOrDefault($$0, Util.NIL_UUID);
    }

    public void addPlayer(PlayerInfo $$0) {
        GameProfile $$1 = $$0.getProfile();
        this.discoveredNamesToUUID.put($$1.getName(), $$1.getId());
        Screen screen = this.minecraft.screen;
        if (screen instanceof SocialInteractionsScreen) {
            SocialInteractionsScreen $$2 = (SocialInteractionsScreen)screen;
            $$2.onAddPlayer($$0);
        }
    }

    public void removePlayer(UUID $$0) {
        Screen screen = this.minecraft.screen;
        if (screen instanceof SocialInteractionsScreen) {
            SocialInteractionsScreen $$1 = (SocialInteractionsScreen)screen;
            $$1.onRemovePlayer($$0);
        }
    }
}

