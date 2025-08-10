/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class PlayerInfo {
    private final GameProfile profile;
    private final java.util.function.Supplier<PlayerSkin> skinLookup;
    private GameType gameMode = GameType.DEFAULT_MODE;
    private int latency;
    @Nullable
    private Component tabListDisplayName;
    private boolean showHat = true;
    @Nullable
    private RemoteChatSession chatSession;
    private SignedMessageValidator messageValidator;
    private int tabListOrder;

    public PlayerInfo(GameProfile $$0, boolean $$1) {
        this.profile = $$0;
        this.messageValidator = PlayerInfo.fallbackMessageValidator($$1);
        Supplier<java.util.function.Supplier> $$2 = Suppliers.memoize(() -> PlayerInfo.createSkinLookup($$0));
        this.skinLookup = () -> (PlayerSkin)((Object)((Object)((java.util.function.Supplier)$$2.get()).get()));
    }

    private static java.util.function.Supplier<PlayerSkin> createSkinLookup(GameProfile $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        SkinManager $$2 = $$1.getSkinManager();
        CompletableFuture<Optional<PlayerSkin>> $$3 = $$2.getOrLoad($$0);
        boolean $$4 = !$$1.isLocalPlayer($$0.getId());
        PlayerSkin $$5 = DefaultPlayerSkin.get($$0);
        return () -> {
            PlayerSkin $$3 = $$3.getNow(Optional.empty()).orElse($$5);
            if ($$4 && !$$3.secure()) {
                return $$5;
            }
            return $$3;
        };
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    @Nullable
    public RemoteChatSession getChatSession() {
        return this.chatSession;
    }

    public SignedMessageValidator getMessageValidator() {
        return this.messageValidator;
    }

    public boolean hasVerifiableChat() {
        return this.chatSession != null;
    }

    protected void setChatSession(RemoteChatSession $$0) {
        this.chatSession = $$0;
        this.messageValidator = $$0.createMessageValidator(ProfilePublicKey.EXPIRY_GRACE_PERIOD);
    }

    protected void clearChatSession(boolean $$0) {
        this.chatSession = null;
        this.messageValidator = PlayerInfo.fallbackMessageValidator($$0);
    }

    private static SignedMessageValidator fallbackMessageValidator(boolean $$0) {
        return $$0 ? SignedMessageValidator.REJECT_ALL : SignedMessageValidator.ACCEPT_UNSIGNED;
    }

    public GameType getGameMode() {
        return this.gameMode;
    }

    protected void setGameMode(GameType $$0) {
        this.gameMode = $$0;
    }

    public int getLatency() {
        return this.latency;
    }

    protected void setLatency(int $$0) {
        this.latency = $$0;
    }

    public PlayerSkin getSkin() {
        return this.skinLookup.get();
    }

    @Nullable
    public PlayerTeam getTeam() {
        return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
    }

    public void setTabListDisplayName(@Nullable Component $$0) {
        this.tabListDisplayName = $$0;
    }

    @Nullable
    public Component getTabListDisplayName() {
        return this.tabListDisplayName;
    }

    public void setShowHat(boolean $$0) {
        this.showHat = $$0;
    }

    public boolean showHat() {
        return this.showHat;
    }

    public void setTabListOrder(int $$0) {
        this.tabListOrder = $$0;
    }

    public int getTabListOrder() {
        return this.tabListOrder;
    }

    private static /* synthetic */ PlayerSkin lambda$createSkinLookup$2(PlayerSkin $$0) {
        return $$0;
    }
}

