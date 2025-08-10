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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class Time
extends NeedleDirectionHelper
implements RangeSelectItemModelProperty {
    public static final MapCodec<Time> MAP_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleDirectionHelper::wobble), (App)TimeSource.CODEC.fieldOf("source").forGetter($$0 -> $$0.source)).apply((Applicative)$$02, Time::new));
    private final TimeSource source;
    private final RandomSource randomSource = RandomSource.create();
    private final NeedleDirectionHelper.Wobbler wobbler;

    public Time(boolean $$0, TimeSource $$1) {
        super($$0);
        this.source = $$1;
        this.wobbler = this.newWobbler(0.9f);
    }

    @Override
    protected float calculate(ItemStack $$0, ClientLevel $$1, int $$2, Entity $$3) {
        float $$4 = this.source.get($$1, $$0, $$3, this.randomSource);
        long $$5 = $$1.getGameTime();
        if (this.wobbler.shouldUpdate($$5)) {
            this.wobbler.update($$5, $$4);
        }
        return this.wobbler.rotation();
    }

    public MapCodec<Time> type() {
        return MAP_CODEC;
    }

    public static abstract sealed class TimeSource
    extends Enum<TimeSource>
    implements StringRepresentable {
        public static final /* enum */ TimeSource RANDOM = new TimeSource("random"){

            @Override
            public float get(ClientLevel $$0, ItemStack $$1, Entity $$2, RandomSource $$3) {
                return $$3.nextFloat();
            }
        };
        public static final /* enum */ TimeSource DAYTIME = new TimeSource("daytime"){

            @Override
            public float get(ClientLevel $$0, ItemStack $$1, Entity $$2, RandomSource $$3) {
                return $$0.getTimeOfDay(1.0f);
            }
        };
        public static final /* enum */ TimeSource MOON_PHASE = new TimeSource("moon_phase"){

            @Override
            public float get(ClientLevel $$0, ItemStack $$1, Entity $$2, RandomSource $$3) {
                return (float)$$0.getMoonPhase() / 8.0f;
            }
        };
        public static final Codec<TimeSource> CODEC;
        private final String name;
        private static final /* synthetic */ TimeSource[] $VALUES;

        public static TimeSource[] values() {
            return (TimeSource[])$VALUES.clone();
        }

        public static TimeSource valueOf(String $$0) {
            return Enum.valueOf(TimeSource.class, $$0);
        }

        TimeSource(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        abstract float get(ClientLevel var1, ItemStack var2, Entity var3, RandomSource var4);

        private static /* synthetic */ TimeSource[] a() {
            return new TimeSource[]{RANDOM, DAYTIME, MOON_PHASE};
        }

        static {
            $VALUES = TimeSource.a();
            CODEC = StringRepresentable.fromEnum(TimeSource::values);
        }
    }
}

