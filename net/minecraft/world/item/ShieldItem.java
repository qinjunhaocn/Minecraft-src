/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShieldItem
extends Item {
    public ShieldItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public Component getName(ItemStack $$0) {
        DyeColor $$1 = $$0.get(DataComponents.BASE_COLOR);
        if ($$1 != null) {
            return Component.translatable(this.descriptionId + "." + $$1.getName());
        }
        return super.getName($$0);
    }
}

