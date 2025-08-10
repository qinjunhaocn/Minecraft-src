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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsCarried() implements ConditionalItemModelProperty
{
    public static final MapCodec<IsCarried> MAP_CODEC = MapCodec.unit((Object)new IsCarried());

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        if (!($$2 instanceof LocalPlayer)) return false;
        LocalPlayer $$5 = (LocalPlayer)$$2;
        if ($$5.containerMenu.getCarried() != $$0) return false;
        return true;
    }

    public MapCodec<IsCarried> type() {
        return MAP_CODEC;
    }
}

