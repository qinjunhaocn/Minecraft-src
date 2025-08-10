/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public class IntegratedPlayerList
extends PlayerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private CompoundTag playerData;

    public IntegratedPlayerList(IntegratedServer $$0, LayeredRegistryAccess<RegistryLayer> $$1, PlayerDataStorage $$2) {
        super($$0, $$1, $$2, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void save(ServerPlayer $$0) {
        if (this.getServer().isSingleplayerOwner($$0.getGameProfile())) {
            try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER);){
                TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0.registryAccess());
                $$0.saveWithoutId($$2);
                this.playerData = $$2.buildResult();
            }
        }
        super.save($$0);
    }

    @Override
    public Component canPlayerLogin(SocketAddress $$0, GameProfile $$1) {
        if (this.getServer().isSingleplayerOwner($$1) && this.getPlayerByName($$1.getName()) != null) {
            return Component.translatable("multiplayer.disconnect.name_taken");
        }
        return super.canPlayerLogin($$0, $$1);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    @Nullable
    public CompoundTag getSingleplayerData() {
        return this.playerData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

