/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.client.color.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;

public record Firework(int defaultColor) implements ItemTintSource
{
    public static final MapCodec<Firework> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(Firework::defaultColor)).apply((Applicative)$$0, Firework::new));

    public Firework() {
        this(-7697782);
    }

    @Override
    public int calculate(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2) {
        FireworkExplosion $$3 = $$0.get(DataComponents.FIREWORK_EXPLOSION);
        IntList $$4 = $$3 != null ? $$3.colors() : IntList.of();
        int $$5 = $$4.size();
        if ($$5 == 0) {
            return this.defaultColor;
        }
        if ($$5 == 1) {
            return ARGB.opaque($$4.getInt(0));
        }
        int $$6 = 0;
        int $$7 = 0;
        int $$8 = 0;
        for (int $$9 = 0; $$9 < $$5; ++$$9) {
            int $$10 = $$4.getInt($$9);
            $$6 += ARGB.red($$10);
            $$7 += ARGB.green($$10);
            $$8 += ARGB.blue($$10);
        }
        return ARGB.color($$6 / $$5, $$7 / $$5, $$8 / $$5);
    }

    public MapCodec<Firework> type() {
        return MAP_CODEC;
    }
}

