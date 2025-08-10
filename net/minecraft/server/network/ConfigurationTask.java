/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;

public interface ConfigurationTask {
    public void start(Consumer<Packet<?>> var1);

    public Type type();

    public record Type(String id) {
        public String toString() {
            return this.id;
        }
    }
}

