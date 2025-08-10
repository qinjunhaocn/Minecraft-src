/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import java.util.List;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ClientRegistryLayer
extends Enum<ClientRegistryLayer> {
    public static final /* enum */ ClientRegistryLayer STATIC = new ClientRegistryLayer();
    public static final /* enum */ ClientRegistryLayer REMOTE = new ClientRegistryLayer();
    private static final List<ClientRegistryLayer> VALUES;
    private static final RegistryAccess.Frozen STATIC_ACCESS;
    private static final /* synthetic */ ClientRegistryLayer[] $VALUES;

    public static ClientRegistryLayer[] values() {
        return (ClientRegistryLayer[])$VALUES.clone();
    }

    public static ClientRegistryLayer valueOf(String $$0) {
        return Enum.valueOf(ClientRegistryLayer.class, $$0);
    }

    public static LayeredRegistryAccess<ClientRegistryLayer> createRegistryAccess() {
        return new LayeredRegistryAccess<ClientRegistryLayer>(VALUES).a(STATIC, STATIC_ACCESS);
    }

    private static /* synthetic */ ClientRegistryLayer[] b() {
        return new ClientRegistryLayer[]{STATIC, REMOTE};
    }

    static {
        $VALUES = ClientRegistryLayer.b();
        VALUES = List.of((Object[])ClientRegistryLayer.values());
        STATIC_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }
}

