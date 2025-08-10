/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record UseRemainder(ItemStack convertInto) {
    public static final Codec<UseRemainder> CODEC = ItemStack.CODEC.xmap(UseRemainder::new, UseRemainder::convertInto);
    public static final StreamCodec<RegistryFriendlyByteBuf, UseRemainder> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, UseRemainder::convertInto, UseRemainder::new);

    public ItemStack convertIntoRemainder(ItemStack $$0, int $$1, boolean $$2, OnExtraCreatedRemainder $$3) {
        if ($$2) {
            return $$0;
        }
        if ($$0.getCount() >= $$1) {
            return $$0;
        }
        ItemStack $$4 = this.convertInto.copy();
        if ($$0.isEmpty()) {
            return $$4;
        }
        $$3.apply($$4);
        return $$0;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || ((Object)((Object)this)).getClass() != $$0.getClass()) {
            return false;
        }
        UseRemainder $$1 = (UseRemainder)((Object)$$0);
        return ItemStack.matches(this.convertInto, $$1.convertInto);
    }

    public int hashCode() {
        return ItemStack.hashItemAndComponents(this.convertInto);
    }

    @FunctionalInterface
    public static interface OnExtraCreatedRemainder {
        public void apply(ItemStack var1);
    }
}

