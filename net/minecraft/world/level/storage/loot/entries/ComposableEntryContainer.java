/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;

@FunctionalInterface
interface ComposableEntryContainer {
    public static final ComposableEntryContainer ALWAYS_FALSE = ($$0, $$1) -> false;
    public static final ComposableEntryContainer ALWAYS_TRUE = ($$0, $$1) -> true;

    public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2);

    default public ComposableEntryContainer and(ComposableEntryContainer $$0) {
        Objects.requireNonNull($$0);
        return ($$1, $$2) -> this.expand($$1, $$2) && $$0.expand($$1, $$2);
    }

    default public ComposableEntryContainer or(ComposableEntryContainer $$0) {
        Objects.requireNonNull($$0);
        return ($$1, $$2) -> this.expand($$1, $$2) || $$0.expand($$1, $$2);
    }
}

