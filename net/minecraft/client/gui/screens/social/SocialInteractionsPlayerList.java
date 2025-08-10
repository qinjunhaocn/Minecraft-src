/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;

public class SocialInteractionsPlayerList
extends ContainerObjectSelectionList<PlayerEntry> {
    private final SocialInteractionsScreen socialInteractionsScreen;
    private final List<PlayerEntry> players = Lists.newArrayList();
    @Nullable
    private String filter;

    public SocialInteractionsPlayerList(SocialInteractionsScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$1, $$2, $$3, $$4, $$5);
        this.socialInteractionsScreen = $$0;
    }

    @Override
    protected void renderListBackground(GuiGraphics $$0) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics $$0) {
    }

    @Override
    protected void enableScissor(GuiGraphics $$0) {
        $$0.enableScissor(this.getX(), this.getY() + 4, this.getRight(), this.getBottom());
    }

    public void updatePlayerList(Collection<UUID> $$0, double $$1, boolean $$2) {
        HashMap<UUID, PlayerEntry> $$3 = new HashMap<UUID, PlayerEntry>();
        this.addOnlinePlayers($$0, $$3);
        this.updatePlayersFromChatLog($$3, $$2);
        this.updateFiltersAndScroll($$3.values(), $$1);
    }

    private void addOnlinePlayers(Collection<UUID> $$0, Map<UUID, PlayerEntry> $$1) {
        ClientPacketListener $$2 = this.minecraft.player.connection;
        for (UUID $$3 : $$0) {
            PlayerInfo $$4 = $$2.getPlayerInfo($$3);
            if ($$4 == null) continue;
            boolean $$5 = $$4.hasVerifiableChat();
            $$1.put($$3, new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$3, $$4.getProfile().getName(), $$4::getSkin, $$5));
        }
    }

    private void updatePlayersFromChatLog(Map<UUID, PlayerEntry> $$0, boolean $$12) {
        Collection<GameProfile> $$2 = SocialInteractionsPlayerList.collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
        for (GameProfile $$3 : $$2) {
            PlayerEntry $$5;
            if ($$12) {
                PlayerEntry $$4 = $$0.computeIfAbsent($$3.getId(), $$1 -> {
                    PlayerEntry $$2 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$3.getId(), $$3.getName(), this.minecraft.getSkinManager().lookupInsecure($$3), true);
                    $$2.setRemoved(true);
                    return $$2;
                });
            } else {
                $$5 = $$0.get($$3.getId());
                if ($$5 == null) continue;
            }
            $$5.setHasRecentMessages(true);
        }
    }

    private static Collection<GameProfile> collectProfilesFromChatLog(ChatLog $$0) {
        ObjectLinkedOpenHashSet $$1 = new ObjectLinkedOpenHashSet();
        for (int $$2 = $$0.end(); $$2 >= $$0.start(); --$$2) {
            LoggedChatMessage.Player $$4;
            LoggedChatEvent $$3 = $$0.lookup($$2);
            if (!($$3 instanceof LoggedChatMessage.Player) || !($$4 = (LoggedChatMessage.Player)$$3).message().hasSignature()) continue;
            $$1.add($$4.profile());
        }
        return $$1;
    }

    private void sortPlayerEntries() {
        this.players.sort(Comparator.comparing($$0 -> {
            if (this.minecraft.isLocalPlayer($$0.getPlayerId())) {
                return 0;
            }
            if (this.minecraft.getReportingContext().hasDraftReportFor($$0.getPlayerId())) {
                return 1;
            }
            if ($$0.getPlayerId().version() == 2) {
                return 4;
            }
            if ($$0.hasRecentMessages()) {
                return 2;
            }
            return 3;
        }).thenComparing($$0 -> {
            int $$1;
            if (!$$0.getPlayerName().isBlank() && (($$1 = $$0.getPlayerName().codePointAt(0)) == 95 || $$1 >= 97 && $$1 <= 122 || $$1 >= 65 && $$1 <= 90 || $$1 >= 48 && $$1 <= 57)) {
                return 0;
            }
            return 1;
        }).thenComparing(PlayerEntry::getPlayerName, String::compareToIgnoreCase));
    }

    private void updateFiltersAndScroll(Collection<PlayerEntry> $$0, double $$1) {
        this.players.clear();
        this.players.addAll($$0);
        this.sortPlayerEntries();
        this.updateFilteredPlayers();
        this.replaceEntries(this.players);
        this.setScrollAmount($$1);
    }

    private void updateFilteredPlayers() {
        if (this.filter != null) {
            this.players.removeIf($$0 -> !$$0.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter));
            this.replaceEntries(this.players);
        }
    }

    public void setFilter(String $$0) {
        this.filter = $$0;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void addPlayer(PlayerInfo $$0, SocialInteractionsScreen.Page $$1) {
        UUID $$2 = $$0.getProfile().getId();
        for (PlayerEntry $$3 : this.players) {
            if (!$$3.getPlayerId().equals($$2)) continue;
            $$3.setRemoved(false);
            return;
        }
        if (($$1 == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom($$2)) && (Strings.isNullOrEmpty(this.filter) || $$0.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
            boolean $$4 = $$0.hasVerifiableChat();
            PlayerEntry $$5 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$0.getProfile().getId(), $$0.getProfile().getName(), $$0::getSkin, $$4);
            this.addEntry($$5);
            this.players.add($$5);
        }
    }

    public void removePlayer(UUID $$0) {
        for (PlayerEntry $$1 : this.players) {
            if (!$$1.getPlayerId().equals($$0)) continue;
            $$1.setRemoved(true);
            return;
        }
    }

    public void refreshHasDraftReport() {
        this.players.forEach($$0 -> $$0.refreshHasDraftReport(this.minecraft.getReportingContext()));
    }
}

