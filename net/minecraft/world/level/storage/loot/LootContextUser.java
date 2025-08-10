/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot;

import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface LootContextUser {
    default public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of();
    }

    default public void validate(ValidationContext $$0) {
        $$0.validateContextUsage(this);
    }
}

