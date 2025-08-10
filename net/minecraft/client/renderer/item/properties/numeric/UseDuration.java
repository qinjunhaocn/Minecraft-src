/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record UseDuration(boolean remaining) implements RangeSelectItemModelProperty
{
    public static final MapCodec<UseDuration> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("remaining", (Object)false).forGetter(UseDuration::remaining)).apply((Applicative)$$0, UseDuration::new));

    @Override
    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        if ($$2 == null || $$2.getUseItem() != $$0) {
            return 0.0f;
        }
        return this.remaining ? (float)$$2.getUseItemRemainingTicks() : (float)UseDuration.useDuration($$0, $$2);
    }

    public MapCodec<UseDuration> type() {
        return MAP_CODEC;
    }

    public static int useDuration(ItemStack $$0, LivingEntity $$1) {
        return $$0.getUseDuration($$1) - $$1.getUseItemRemainingTicks();
    }
}

