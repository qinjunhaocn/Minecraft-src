/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class CrossbowPull
implements RangeSelectItemModelProperty {
    public static final MapCodec<CrossbowPull> MAP_CODEC = MapCodec.unit((Object)new CrossbowPull());

    @Override
    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        if ($$2 == null) {
            return 0.0f;
        }
        if (CrossbowItem.isCharged($$0)) {
            return 0.0f;
        }
        int $$4 = CrossbowItem.getChargeDuration($$0, $$2);
        return (float)UseDuration.useDuration($$0, $$2) / (float)$$4;
    }

    public MapCodec<CrossbowPull> type() {
        return MAP_CODEC;
    }
}

