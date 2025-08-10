/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.color.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapItemColor;

public record MapColor(int defaultColor) implements ItemTintSource
{
    public static final MapCodec<MapColor> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(MapColor::defaultColor)).apply((Applicative)$$0, MapColor::new));

    public MapColor() {
        this(MapItemColor.DEFAULT.rgb());
    }

    @Override
    public int calculate(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2) {
        MapItemColor $$3 = $$0.get(DataComponents.MAP_COLOR);
        if ($$3 != null) {
            return ARGB.opaque($$3.rgb());
        }
        return ARGB.opaque(this.defaultColor);
    }

    public MapCodec<MapColor> type() {
        return MAP_CODEC;
    }
}

