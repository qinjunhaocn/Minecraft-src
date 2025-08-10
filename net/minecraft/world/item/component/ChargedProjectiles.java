/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;

public final class ChargedProjectiles
implements TooltipProvider {
    public static final ChargedProjectiles EMPTY = new ChargedProjectiles(List.of());
    public static final Codec<ChargedProjectiles> CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, $$0 -> $$0.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ChargedProjectiles::new, $$0 -> $$0.items);
    private final List<ItemStack> items;

    private ChargedProjectiles(List<ItemStack> $$0) {
        this.items = $$0;
    }

    public static ChargedProjectiles of(ItemStack $$0) {
        return new ChargedProjectiles(List.of((Object)$$0.copy()));
    }

    public static ChargedProjectiles of(List<ItemStack> $$0) {
        return new ChargedProjectiles(List.copyOf(Lists.transform($$0, ItemStack::copy)));
    }

    public boolean contains(Item $$0) {
        for (ItemStack $$1 : this.items) {
            if (!$$1.is($$0)) continue;
            return true;
        }
        return false;
    }

    public List<ItemStack> getItems() {
        return Lists.transform(this.items, ItemStack::copy);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof ChargedProjectiles)) return false;
        ChargedProjectiles $$1 = (ChargedProjectiles)$$0;
        if (!ItemStack.listMatches(this.items, $$1.items)) return false;
        return true;
    }

    public int hashCode() {
        return ItemStack.hashStackList(this.items);
    }

    public String toString() {
        return "ChargedProjectiles[items=" + String.valueOf(this.items) + "]";
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        ItemStack $$4 = null;
        int $$5 = 0;
        for (ItemStack $$6 : this.items) {
            if ($$4 == null) {
                $$4 = $$6;
                $$5 = 1;
                continue;
            }
            if (ItemStack.matches($$4, $$6)) {
                ++$$5;
                continue;
            }
            ChargedProjectiles.addProjectileTooltip($$0, $$1, $$4, $$5);
            $$4 = $$6;
            $$5 = 1;
        }
        if ($$4 != null) {
            ChargedProjectiles.addProjectileTooltip($$0, $$1, $$4, $$5);
        }
    }

    private static void addProjectileTooltip(Item.TooltipContext $$0, Consumer<Component> $$12, ItemStack $$2, int $$3) {
        if ($$3 == 1) {
            $$12.accept(Component.a("item.minecraft.crossbow.projectile.single", $$2.getDisplayName()));
        } else {
            $$12.accept(Component.a("item.minecraft.crossbow.projectile.multiple", $$3, $$2.getDisplayName()));
        }
        TooltipDisplay $$4 = $$2.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        $$2.addDetailsToTooltip($$0, $$4, null, TooltipFlag.NORMAL, $$1 -> $$12.accept(Component.literal("  ").append((Component)$$1).withStyle(ChatFormatting.GRAY)));
    }
}

