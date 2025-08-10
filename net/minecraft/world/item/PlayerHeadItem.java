/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerHeadItem
extends StandingAndWallBlockItem {
    public PlayerHeadItem(Block $$0, Block $$1, Item.Properties $$2) {
        super($$0, $$1, Direction.DOWN, $$2);
    }

    @Override
    public Component getName(ItemStack $$0) {
        ResolvableProfile $$1 = $$0.get(DataComponents.PROFILE);
        if ($$1 != null && $$1.name().isPresent()) {
            return Component.a(this.descriptionId + ".named", $$1.name().get());
        }
        return super.getName($$0);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack $$0) {
        ResolvableProfile $$12 = $$0.get(DataComponents.PROFILE);
        if ($$12 != null && !$$12.isResolved()) {
            $$12.resolve().thenAcceptAsync($$1 -> $$0.set(DataComponents.PROFILE, $$1), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
        }
    }
}

