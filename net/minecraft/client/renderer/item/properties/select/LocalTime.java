/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.DateFormat
 *  com.ibm.icu.text.SimpleDateFormat
 *  com.ibm.icu.util.Calendar
 *  com.ibm.icu.util.TimeZone
 *  com.ibm.icu.util.ULocale
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.item.properties.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LocalTime
implements SelectItemModelProperty<String> {
    public static final String ROOT_LOCALE = "";
    private static final long UPDATE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1L);
    public static final Codec<String> VALUE_CODEC = Codec.STRING;
    private static final Codec<TimeZone> TIME_ZONE_CODEC = VALUE_CODEC.comapFlatMap($$0 -> {
        TimeZone $$1 = TimeZone.getTimeZone((String)$$0);
        if ($$1.equals((Object)TimeZone.UNKNOWN_ZONE)) {
            return DataResult.error(() -> "Unknown timezone: " + $$0);
        }
        return DataResult.success((Object)$$1);
    }, TimeZone::getID);
    private static final MapCodec<Data> DATA_MAP_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.STRING.fieldOf("pattern").forGetter($$0 -> $$0.format), (App)Codec.STRING.optionalFieldOf("locale", (Object)ROOT_LOCALE).forGetter($$0 -> $$0.localeId), (App)TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter($$0 -> $$0.timeZone)).apply((Applicative)$$02, Data::new));
    public static final SelectItemModelProperty.Type<LocalTime, String> TYPE = SelectItemModelProperty.Type.create(DATA_MAP_CODEC.flatXmap(LocalTime::create, $$0 -> DataResult.success((Object)((Object)$$0.data))), VALUE_CODEC);
    private final Data data;
    private final DateFormat parsedFormat;
    private long nextUpdateTimeMs;
    private String lastResult = "";

    private LocalTime(Data $$0, DateFormat $$1) {
        this.data = $$0;
        this.parsedFormat = $$1;
    }

    public static LocalTime create(String $$02, String $$1, Optional<TimeZone> $$2) {
        return (LocalTime)LocalTime.create(new Data($$02, $$1, $$2)).getOrThrow($$0 -> new IllegalStateException("Failed to validate format: " + $$0));
    }

    private static DataResult<LocalTime> create(Data $$0) {
        ULocale $$12 = new ULocale($$0.localeId);
        Calendar $$2 = $$0.timeZone.map($$1 -> Calendar.getInstance((TimeZone)$$1, (ULocale)$$12)).orElseGet(() -> Calendar.getInstance((ULocale)$$12));
        SimpleDateFormat $$3 = new SimpleDateFormat($$0.format, $$12);
        $$3.setCalendar($$2);
        try {
            $$3.format(new Date());
        } catch (Exception $$4) {
            return DataResult.error(() -> "Invalid time format '" + String.valueOf($$3) + "': " + $$4.getMessage());
        }
        return DataResult.success((Object)new LocalTime($$0, (DateFormat)$$3));
    }

    @Override
    @Nullable
    public String get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        long $$5 = Util.getMillis();
        if ($$5 > this.nextUpdateTimeMs) {
            this.lastResult = this.update();
            this.nextUpdateTimeMs = $$5 + UPDATE_INTERVAL_MS;
        }
        return this.lastResult;
    }

    private String update() {
        return this.parsedFormat.format(new Date());
    }

    @Override
    public SelectItemModelProperty.Type<LocalTime, String> type() {
        return TYPE;
    }

    @Override
    public Codec<String> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public /* synthetic */ Object get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int n, ItemDisplayContext itemDisplayContext) {
        return this.get(itemStack, clientLevel, livingEntity, n, itemDisplayContext);
    }

    static final class Data
    extends Record {
        final String format;
        final String localeId;
        final Optional<TimeZone> timeZone;

        Data(String $$0, String $$1, Optional<TimeZone> $$2) {
            this.format = $$0;
            this.localeId = $$1;
            this.timeZone = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this, $$0);
        }

        public String format() {
            return this.format;
        }

        public String localeId() {
            return this.localeId;
        }

        public Optional<TimeZone> timeZone() {
            return this.timeZone;
        }
    }
}

