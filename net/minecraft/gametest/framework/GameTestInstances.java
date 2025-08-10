/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.gametest.framework.BuiltinTestFunctions;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestEnvironments;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface GameTestInstances {
    public static final ResourceKey<GameTestInstance> ALWAYS_PASS = GameTestInstances.create("always_pass");

    public static void bootstrap(BootstrapContext<GameTestInstance> $$0) {
        HolderGetter<Consumer<GameTestHelper>> $$1 = $$0.lookup(Registries.TEST_FUNCTION);
        HolderGetter<TestEnvironmentDefinition> $$2 = $$0.lookup(Registries.TEST_ENVIRONMENT);
        $$0.register(ALWAYS_PASS, new FunctionGameTestInstance(BuiltinTestFunctions.ALWAYS_PASS, new TestData<Holder<TestEnvironmentDefinition>>($$2.getOrThrow(GameTestEnvironments.DEFAULT_KEY), ResourceLocation.withDefaultNamespace("empty"), 1, 1, false)));
    }

    private static ResourceKey<GameTestInstance> create(String $$0) {
        return ResourceKey.create(Registries.TEST_INSTANCE, ResourceLocation.withDefaultNamespace($$0));
    }
}

