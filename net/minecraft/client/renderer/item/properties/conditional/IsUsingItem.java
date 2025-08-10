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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsUsingItem() implements ConditionalItemModelProperty
{
    public static final MapCodec<IsUsingItem> MAP_CODEC = MapCodec.unit((Object)new IsUsingItem());

    @Override
    public boolean get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        if ($$2 == null) {
            return false;
        }
        return $$2.isUsingItem() && $$2.getUseItem() == $$0;
    }

    public MapCodec<IsUsingItem> type() {
        return MAP_CODEC;
    }
}

