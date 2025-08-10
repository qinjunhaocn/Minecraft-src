/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.slf4j.Logger;

@FunctionalInterface
public interface ServerAddressResolver {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ServerAddressResolver SYSTEM = $$0 -> {
        try {
            InetAddress $$1 = InetAddress.getByName($$0.getHost());
            return Optional.of(ResolvedServerAddress.from(new InetSocketAddress($$1, $$0.getPort())));
        } catch (UnknownHostException $$2) {
            LOGGER.debug("Couldn't resolve server {} address", (Object)$$0.getHost(), (Object)$$2);
            return Optional.empty();
        }
    };

    public Optional<ResolvedServerAddress> resolve(ServerAddress var1);
}

