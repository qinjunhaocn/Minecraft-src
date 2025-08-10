/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.network.config;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;

public class ServerResourcePackConfigurationTask
implements ConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("server_resource_pack");
    private final MinecraftServer.ServerResourcePackInfo info;

    public ServerResourcePackConfigurationTask(MinecraftServer.ServerResourcePackInfo $$0) {
        this.info = $$0;
    }

    @Override
    public void start(Consumer<Packet<?>> $$0) {
        $$0.accept(new ClientboundResourcePackPushPacket(this.info.id(), this.info.url(), this.info.hash(), this.info.isRequired(), Optional.ofNullable(this.info.prompt())));
    }

    @Override
    public ConfigurationTask.Type type() {
        return TYPE;
    }
}

