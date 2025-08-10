/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.enchantment;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record EnchantedItemInUse(ItemStack itemStack, @Nullable EquipmentSlot inSlot, @Nullable LivingEntity owner, Consumer<Item> onBreak) {
    public EnchantedItemInUse(ItemStack $$0, EquipmentSlot $$1, LivingEntity $$22) {
        this($$0, $$1, $$22, $$2 -> $$22.onEquippedItemBroken((Item)$$2, $$1));
    }

    @Nullable
    public EquipmentSlot inSlot() {
        return this.inSlot;
    }

    @Nullable
    public LivingEntity owner() {
        return this.owner;
    }
}

