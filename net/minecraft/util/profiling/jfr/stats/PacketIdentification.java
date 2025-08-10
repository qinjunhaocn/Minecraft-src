/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record PacketIdentification(String direction, String protocolId, String packetId) {
    public static PacketIdentification from(RecordedEvent $$0) {
        return new PacketIdentification($$0.getString("packetDirection"), $$0.getString("protocolId"), $$0.getString("packetId"));
    }
}

