/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface GameTestEnvironments {
    public static final String DEFAULT = "default";
    public static final ResourceKey<TestEnvironmentDefinition> DEFAULT_KEY = GameTestEnvironments.create("default");

    private static ResourceKey<TestEnvironmentDefinition> create(String $$0) {
        return ResourceKey.create(Registries.TEST_ENVIRONMENT, ResourceLocation.withDefaultNamespace($$0));
    }

    public static void bootstrap(BootstrapContext<TestEnvironmentDefinition> $$0) {
        $$0.register(DEFAULT_KEY, new TestEnvironmentDefinition.AllOf(List.of()));
    }
}

