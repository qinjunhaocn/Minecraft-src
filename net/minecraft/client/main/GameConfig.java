/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.PropertyMap
 */
package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.util.StringUtil;

public class GameConfig {
    public final UserData user;
    public final DisplayData display;
    public final FolderData location;
    public final GameData game;
    public final QuickPlayData quickPlay;

    public GameConfig(UserData $$0, DisplayData $$1, FolderData $$2, GameData $$3, QuickPlayData $$4) {
        this.user = $$0;
        this.display = $$1;
        this.location = $$2;
        this.game = $$3;
        this.quickPlay = $$4;
    }

    public static class UserData {
        public final User user;
        public final PropertyMap userProperties;
        public final PropertyMap profileProperties;
        public final Proxy proxy;

        public UserData(User $$0, PropertyMap $$1, PropertyMap $$2, Proxy $$3) {
            this.user = $$0;
            this.userProperties = $$1;
            this.profileProperties = $$2;
            this.proxy = $$3;
        }
    }

    public static class FolderData {
        public final File gameDirectory;
        public final File resourcePackDirectory;
        public final File assetDirectory;
        @Nullable
        public final String assetIndex;

        public FolderData(File $$0, File $$1, File $$2, @Nullable String $$3) {
            this.gameDirectory = $$0;
            this.resourcePackDirectory = $$1;
            this.assetDirectory = $$2;
            this.assetIndex = $$3;
        }

        public Path getExternalAssetSource() {
            return this.assetIndex == null ? this.assetDirectory.toPath() : IndexedAssetSource.createIndexFs(this.assetDirectory.toPath(), this.assetIndex);
        }
    }

    public static class GameData {
        public final boolean demo;
        public final String launchVersion;
        public final String versionType;
        public final boolean disableMultiplayer;
        public final boolean disableChat;
        public final boolean captureTracyImages;
        public final boolean renderDebugLabels;

        public GameData(boolean $$0, String $$1, String $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6) {
            this.demo = $$0;
            this.launchVersion = $$1;
            this.versionType = $$2;
            this.disableMultiplayer = $$3;
            this.disableChat = $$4;
            this.captureTracyImages = $$5;
            this.renderDebugLabels = $$6;
        }
    }

    public record QuickPlayData(@Nullable String logPath, QuickPlayVariant variant) {
        public boolean isEnabled() {
            return this.variant.isEnabled();
        }

        @Nullable
        public String logPath() {
            return this.logPath;
        }
    }

    public record QuickPlayDisabled() implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    public record QuickPlayRealmsData(String realmId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringUtil.isBlank(this.realmId);
        }
    }

    public record QuickPlayMultiplayerData(String serverAddress) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringUtil.isBlank(this.serverAddress);
        }
    }

    public record QuickPlaySinglePlayerData(@Nullable String worldId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Nullable
        public String worldId() {
            return this.worldId;
        }
    }

    public static sealed interface QuickPlayVariant
    permits QuickPlaySinglePlayerData, QuickPlayMultiplayerData, QuickPlayRealmsData, QuickPlayDisabled {
        public static final QuickPlayVariant DISABLED = new QuickPlayDisabled();

        public boolean isEnabled();
    }
}

