/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record BundleHasSelectedItem() implements ConditionalItemModelProperty
{
    public static final MapCodec<BundleHasSelectedItem> MAP_CODEC = MapCodec.unit((Object)new BundleHasSelectedItem());

    @Override
    public boolean get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        return BundleItem.hasSelectedItem($$0);
    }

    public MapCodec<BundleHasSelectedItem> type() {
        return MAP_CODEC;
    }
}

