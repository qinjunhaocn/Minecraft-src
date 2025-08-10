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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record Count(boolean normalize) implements RangeSelectItemModelProperty
{
    public static final MapCodec<Count> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("normalize", (Object)true).forGetter(Count::normalize)).apply((Applicative)$$0, Count::new));

    @Override
    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        float $$4 = $$0.getCount();
        float $$5 = $$0.getMaxStackSize();
        if (this.normalize) {
            return Mth.clamp($$4 / $$5, 0.0f, 1.0f);
        }
        return Mth.clamp($$4, 0.0f, $$5);
    }

    public MapCodec<Count> type() {
        return MAP_CODEC;
    }
}

