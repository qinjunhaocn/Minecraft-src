/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record DyedItemColor(int rgb) implements TooltipProvider
{
    public static final Codec<DyedItemColor> CODEC = ExtraCodecs.RGB_COLOR_CODEC.xmap(DyedItemColor::new, DyedItemColor::rgb);
    public static final StreamCodec<ByteBuf, DyedItemColor> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, DyedItemColor::rgb, DyedItemColor::new);
    public static final int LEATHER_COLOR = -6265536;

    public static int getOrDefault(ItemStack $$0, int $$1) {
        DyedItemColor $$2 = $$0.get(DataComponents.DYED_COLOR);
        return $$2 != null ? ARGB.opaque($$2.rgb()) : $$1;
    }

    public static ItemStack applyDyes(ItemStack $$0, List<DyeItem> $$1) {
        if (!$$0.is(ItemTags.DYEABLE)) {
            return ItemStack.EMPTY;
        }
        ItemStack $$2 = $$0.copyWithCount(1);
        int $$3 = 0;
        int $$4 = 0;
        int $$5 = 0;
        int $$6 = 0;
        int $$7 = 0;
        DyedItemColor $$8 = $$2.get(DataComponents.DYED_COLOR);
        if ($$8 != null) {
            int $$9 = ARGB.red($$8.rgb());
            int $$10 = ARGB.green($$8.rgb());
            int $$11 = ARGB.blue($$8.rgb());
            $$6 += Math.max($$9, Math.max($$10, $$11));
            $$3 += $$9;
            $$4 += $$10;
            $$5 += $$11;
            ++$$7;
        }
        for (DyeItem $$12 : $$1) {
            int $$13 = $$12.getDyeColor().getTextureDiffuseColor();
            int $$14 = ARGB.red($$13);
            int $$15 = ARGB.green($$13);
            int $$16 = ARGB.blue($$13);
            $$6 += Math.max($$14, Math.max($$15, $$16));
            $$3 += $$14;
            $$4 += $$15;
            $$5 += $$16;
            ++$$7;
        }
        int $$17 = $$3 / $$7;
        int $$18 = $$4 / $$7;
        int $$19 = $$5 / $$7;
        float $$20 = (float)$$6 / (float)$$7;
        float $$21 = Math.max($$17, Math.max($$18, $$19));
        $$17 = (int)((float)$$17 * $$20 / $$21);
        $$18 = (int)((float)$$18 * $$20 / $$21);
        $$19 = (int)((float)$$19 * $$20 / $$21);
        int $$22 = ARGB.color(0, $$17, $$18, $$19);
        $$2.set(DataComponents.DYED_COLOR, new DyedItemColor($$22));
        return $$2;
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        if ($$2.isAdvanced()) {
            $$1.accept(Component.a("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).withStyle(ChatFormatting.GRAY));
        } else {
            $$1.accept(Component.translatable("item.dyed").a(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}

