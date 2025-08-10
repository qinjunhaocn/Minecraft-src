/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server;

import java.util.List;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public final class RegistryLayer
extends Enum<RegistryLayer> {
    public static final /* enum */ RegistryLayer STATIC = new RegistryLayer();
    public static final /* enum */ RegistryLayer WORLDGEN = new RegistryLayer();
    public static final /* enum */ RegistryLayer DIMENSIONS = new RegistryLayer();
    public static final /* enum */ RegistryLayer RELOADABLE = new RegistryLayer();
    private static final List<RegistryLayer> VALUES;
    private static final RegistryAccess.Frozen STATIC_ACCESS;
    private static final /* synthetic */ RegistryLayer[] $VALUES;

    public static RegistryLayer[] values() {
        return (RegistryLayer[])$VALUES.clone();
    }

    public static RegistryLayer valueOf(String $$0) {
        return Enum.valueOf(RegistryLayer.class, $$0);
    }

    public static LayeredRegistryAccess<RegistryLayer> createRegistryAccess() {
        return new LayeredRegistryAccess<RegistryLayer>(VALUES).a(STATIC, STATIC_ACCESS);
    }

    private static /* synthetic */ RegistryLayer[] b() {
        return new RegistryLayer[]{STATIC, WORLDGEN, DIMENSIONS, RELOADABLE};
    }

    static {
        $VALUES = RegistryLayer.b();
        VALUES = List.of((Object[])RegistryLayer.values());
        STATIC_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }
}

