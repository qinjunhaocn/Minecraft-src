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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record Cooldown() implements RangeSelectItemModelProperty
{
    public static final MapCodec<Cooldown> MAP_CODEC = MapCodec.unit((Object)new Cooldown());

    @Override
    public float get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3) {
        float f;
        if ($$2 instanceof Player) {
            Player $$4 = (Player)$$2;
            f = $$4.getCooldowns().getCooldownPercent($$0, 0.0f);
        } else {
            f = 0.0f;
        }
        return f;
    }

    public MapCodec<Cooldown> type() {
        return MAP_CODEC;
    }
}

