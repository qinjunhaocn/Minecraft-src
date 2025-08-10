/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record CustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
    public static final CustomModelData EMPTY = new CustomModelData(List.of(), List.of(), List.of(), List.of());
    public static final Codec<CustomModelData> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.FLOAT.listOf().optionalFieldOf("floats", (Object)List.of()).forGetter(CustomModelData::floats), (App)Codec.BOOL.listOf().optionalFieldOf("flags", (Object)List.of()).forGetter(CustomModelData::flags), (App)Codec.STRING.listOf().optionalFieldOf("strings", (Object)List.of()).forGetter(CustomModelData::strings), (App)ExtraCodecs.RGB_COLOR_CODEC.listOf().optionalFieldOf("colors", (Object)List.of()).forGetter(CustomModelData::colors)).apply((Applicative)$$0, CustomModelData::new));
    public static final StreamCodec<ByteBuf, CustomModelData> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), CustomModelData::floats, ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), CustomModelData::flags, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), CustomModelData::strings, ByteBufCodecs.INT.apply(ByteBufCodecs.list()), CustomModelData::colors, CustomModelData::new);

    @Nullable
    private static <T> T getSafe(List<T> $$0, int $$1) {
        if ($$1 < 0 || $$1 >= $$0.size()) {
            return null;
        }
        return $$0.get($$1);
    }

    @Nullable
    public Float getFloat(int $$0) {
        return CustomModelData.getSafe(this.floats, $$0);
    }

    @Nullable
    public Boolean getBoolean(int $$0) {
        return CustomModelData.getSafe(this.flags, $$0);
    }

    @Nullable
    public String getString(int $$0) {
        return CustomModelData.getSafe(this.strings, $$0);
    }

    @Nullable
    public Integer getColor(int $$0) {
        return CustomModelData.getSafe(this.colors, $$0);
    }
}

