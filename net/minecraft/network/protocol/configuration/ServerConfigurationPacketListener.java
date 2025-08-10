/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;

public interface ServerConfigurationPacketListener
extends ServerCommonPacketListener {
    @Override
    default public ConnectionProtocol protocol() {
        return ConnectionProtocol.CONFIGURATION;
    }

    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1);

    public void handleSelectKnownPacks(ServerboundSelectKnownPacks var1);
}

