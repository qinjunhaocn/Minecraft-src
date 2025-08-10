/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemClusterRenderState
extends EntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public int count;
    public int seed;

    public void extractItemGroupRenderState(Entity $$0, ItemStack $$1, ItemModelResolver $$2) {
        $$2.updateForNonLiving(this.item, $$1, ItemDisplayContext.GROUND, $$0);
        this.count = ItemClusterRenderState.getRenderedAmount($$1.getCount());
        this.seed = ItemClusterRenderState.getSeedForItemStack($$1);
    }

    public static int getSeedForItemStack(ItemStack $$0) {
        return $$0.isEmpty() ? 187 : Item.getId($$0.getItem()) + $$0.getDamageValue();
    }

    public static int getRenderedAmount(int $$0) {
        if ($$0 <= 1) {
            return 1;
        }
        if ($$0 <= 16) {
            return 2;
        }
        if ($$0 <= 32) {
            return 3;
        }
        if ($$0 <= 48) {
            return 4;
        }
        return 5;
    }
}

