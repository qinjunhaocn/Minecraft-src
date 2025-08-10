/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.item;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemModelResolver {
    private final Function<ResourceLocation, ItemModel> modelGetter = $$0::getItemModel;
    private final Function<ResourceLocation, ClientItem.Properties> clientProperties = $$0::getItemProperties;

    public ItemModelResolver(ModelManager $$0) {
    }

    public void updateForLiving(ItemStackRenderState $$0, ItemStack $$1, ItemDisplayContext $$2, LivingEntity $$3) {
        this.updateForTopItem($$0, $$1, $$2, $$3.level(), $$3, $$3.getId() + $$2.ordinal());
    }

    public void updateForNonLiving(ItemStackRenderState $$0, ItemStack $$1, ItemDisplayContext $$2, Entity $$3) {
        this.updateForTopItem($$0, $$1, $$2, $$3.level(), null, $$3.getId());
    }

    public void updateForTopItem(ItemStackRenderState $$0, ItemStack $$1, ItemDisplayContext $$2, @Nullable Level $$3, @Nullable LivingEntity $$4, int $$5) {
        $$0.clear();
        if (!$$1.isEmpty()) {
            $$0.displayContext = $$2;
            this.appendItemLayers($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    public void appendItemLayers(ItemStackRenderState $$0, ItemStack $$1, ItemDisplayContext $$2, @Nullable Level $$3, @Nullable LivingEntity $$4, int $$5) {
        ClientLevel $$7;
        ResourceLocation $$6 = $$1.get(DataComponents.ITEM_MODEL);
        if ($$6 == null) {
            return;
        }
        $$0.setOversizedInGui(this.clientProperties.apply($$6).oversizedInGui());
        this.modelGetter.apply($$6).update($$0, $$1, this, $$2, $$3 instanceof ClientLevel ? ($$7 = (ClientLevel)$$3) : null, $$4, $$5);
    }

    public boolean shouldPlaySwapAnimation(ItemStack $$0) {
        ResourceLocation $$1 = $$0.get(DataComponents.ITEM_MODEL);
        if ($$1 == null) {
            return true;
        }
        return this.clientProperties.apply($$1).handAnimationOnSwap();
    }
}

