/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

public interface NumberProvider
extends LootContextUser {
    public float getFloat(LootContext var1);

    default public int getInt(LootContext $$0) {
        return Math.round(this.getFloat($$0));
    }

    public LootNumberProviderType getType();
}

