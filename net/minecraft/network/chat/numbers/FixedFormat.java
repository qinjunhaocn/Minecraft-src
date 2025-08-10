/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.network.codec.StreamCodec;

public class FixedFormat
implements NumberFormat {
    public static final NumberFormatType<FixedFormat> TYPE = new NumberFormatType<FixedFormat>(){
        private static final MapCodec<FixedFormat> CODEC = ComponentSerialization.CODEC.fieldOf("value").xmap(FixedFormat::new, $$0 -> $$0.value);
        private static final StreamCodec<RegistryFriendlyByteBuf, FixedFormat> STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, $$0 -> $$0.value, FixedFormat::new);

        @Override
        public MapCodec<FixedFormat> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FixedFormat> streamCodec() {
            return STREAM_CODEC;
        }
    };
    final Component value;

    public FixedFormat(Component $$0) {
        this.value = $$0;
    }

    @Override
    public MutableComponent format(int $$0) {
        return this.value.copy();
    }

    public NumberFormatType<FixedFormat> type() {
        return TYPE;
    }
}

