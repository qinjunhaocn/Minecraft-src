/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.world.flag.FeatureFlagSet;

public record CommonListenerCookie(GameProfile localGameProfile, WorldSessionTelemetryManager telemetryManager, RegistryAccess.Frozen receivedRegistries, FeatureFlagSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerData serverData, @Nullable Screen postDisconnectScreen, Map<ResourceLocation, byte[]> serverCookies, @Nullable ChatComponent.State chatState, Map<String, String> customReportDetails, ServerLinks serverLinks) {
    @Nullable
    public String serverBrand() {
        return this.serverBrand;
    }

    @Nullable
    public ServerData serverData() {
        return this.serverData;
    }

    @Nullable
    public Screen postDisconnectScreen() {
        return this.postDisconnectScreen;
    }

    @Nullable
    public ChatComponent.State chatState() {
        return this.chatState;
    }
}

