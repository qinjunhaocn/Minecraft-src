/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentInstance(Holder<Enchantment> enchantment, int level) {
    public int weight() {
        return this.enchantment().value().getWeight();
    }
}

