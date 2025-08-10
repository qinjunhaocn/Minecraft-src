/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BundleSelectedItemSpecialRenderer
implements ItemModel {
    static final ItemModel INSTANCE = new BundleSelectedItemSpecialRenderer();

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        ItemStack $$7 = BundleItem.getSelectedItemStack($$1);
        if (!$$7.isEmpty()) {
            $$2.appendItemLayers($$0, $$7, $$3, $$4, $$5, $$6);
        }
    }

    public record Unbaked() implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            return INSTANCE;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
        }
    }
}

