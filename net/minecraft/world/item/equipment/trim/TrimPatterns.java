/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.equipment.trim;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public class TrimPatterns {
    public static final ResourceKey<TrimPattern> SENTRY = TrimPatterns.registryKey("sentry");
    public static final ResourceKey<TrimPattern> DUNE = TrimPatterns.registryKey("dune");
    public static final ResourceKey<TrimPattern> COAST = TrimPatterns.registryKey("coast");
    public static final ResourceKey<TrimPattern> WILD = TrimPatterns.registryKey("wild");
    public static final ResourceKey<TrimPattern> WARD = TrimPatterns.registryKey("ward");
    public static final ResourceKey<TrimPattern> EYE = TrimPatterns.registryKey("eye");
    public static final ResourceKey<TrimPattern> VEX = TrimPatterns.registryKey("vex");
    public static final ResourceKey<TrimPattern> TIDE = TrimPatterns.registryKey("tide");
    public static final ResourceKey<TrimPattern> SNOUT = TrimPatterns.registryKey("snout");
    public static final ResourceKey<TrimPattern> RIB = TrimPatterns.registryKey("rib");
    public static final ResourceKey<TrimPattern> SPIRE = TrimPatterns.registryKey("spire");
    public static final ResourceKey<TrimPattern> WAYFINDER = TrimPatterns.registryKey("wayfinder");
    public static final ResourceKey<TrimPattern> SHAPER = TrimPatterns.registryKey("shaper");
    public static final ResourceKey<TrimPattern> SILENCE = TrimPatterns.registryKey("silence");
    public static final ResourceKey<TrimPattern> RAISER = TrimPatterns.registryKey("raiser");
    public static final ResourceKey<TrimPattern> HOST = TrimPatterns.registryKey("host");
    public static final ResourceKey<TrimPattern> FLOW = TrimPatterns.registryKey("flow");
    public static final ResourceKey<TrimPattern> BOLT = TrimPatterns.registryKey("bolt");

    public static void bootstrap(BootstrapContext<TrimPattern> $$0) {
        TrimPatterns.register($$0, SENTRY);
        TrimPatterns.register($$0, DUNE);
        TrimPatterns.register($$0, COAST);
        TrimPatterns.register($$0, WILD);
        TrimPatterns.register($$0, WARD);
        TrimPatterns.register($$0, EYE);
        TrimPatterns.register($$0, VEX);
        TrimPatterns.register($$0, TIDE);
        TrimPatterns.register($$0, SNOUT);
        TrimPatterns.register($$0, RIB);
        TrimPatterns.register($$0, SPIRE);
        TrimPatterns.register($$0, WAYFINDER);
        TrimPatterns.register($$0, SHAPER);
        TrimPatterns.register($$0, SILENCE);
        TrimPatterns.register($$0, RAISER);
        TrimPatterns.register($$0, HOST);
        TrimPatterns.register($$0, FLOW);
        TrimPatterns.register($$0, BOLT);
    }

    public static void register(BootstrapContext<TrimPattern> $$0, ResourceKey<TrimPattern> $$1) {
        TrimPattern $$2 = new TrimPattern(TrimPatterns.defaultAssetId($$1), Component.translatable(Util.makeDescriptionId("trim_pattern", $$1.location())), false);
        $$0.register($$1, $$2);
    }

    private static ResourceKey<TrimPattern> registryKey(String $$0) {
        return ResourceKey.create(Registries.TRIM_PATTERN, ResourceLocation.withDefaultNamespace($$0));
    }

    public static ResourceLocation defaultAssetId(ResourceKey<TrimPattern> $$0) {
        return $$0.location();
    }
}

