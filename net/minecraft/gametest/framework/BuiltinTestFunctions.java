/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunctionLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BuiltinTestFunctions
extends TestFunctionLoader {
    public static final ResourceKey<Consumer<GameTestHelper>> ALWAYS_PASS = BuiltinTestFunctions.create("always_pass");
    public static final Consumer<GameTestHelper> ALWAYS_PASS_INSTANCE = GameTestHelper::succeed;

    private static ResourceKey<Consumer<GameTestHelper>> create(String $$0) {
        return ResourceKey.create(Registries.TEST_FUNCTION, ResourceLocation.withDefaultNamespace($$0));
    }

    public static Consumer<GameTestHelper> bootstrap(Registry<Consumer<GameTestHelper>> $$0) {
        BuiltinTestFunctions.registerLoader(new BuiltinTestFunctions());
        BuiltinTestFunctions.runLoaders($$0);
        return ALWAYS_PASS_INSTANCE;
    }

    @Override
    public void load(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> $$0) {
        $$0.accept(ALWAYS_PASS, ALWAYS_PASS_INSTANCE);
    }
}

