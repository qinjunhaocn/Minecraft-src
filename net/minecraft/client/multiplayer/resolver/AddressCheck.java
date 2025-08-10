/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.blocklist.BlockListSupplier
 */
package net.minecraft.client.multiplayer.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public interface AddressCheck {
    public boolean isAllowed(ResolvedServerAddress var1);

    public boolean isAllowed(ServerAddress var1);

    public static AddressCheck createFromService() {
        final ImmutableList $$0 = Streams.stream(ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
        return new AddressCheck(){

            @Override
            public boolean isAllowed(ResolvedServerAddress $$02) {
                String $$1 = $$02.getHostName();
                String $$22 = $$02.getHostIp();
                return $$0.stream().noneMatch($$2 -> $$2.test($$1) || $$2.test($$22));
            }

            @Override
            public boolean isAllowed(ServerAddress $$02) {
                String $$12 = $$02.getHost();
                return $$0.stream().noneMatch($$1 -> $$1.test($$12));
            }
        };
    }
}

