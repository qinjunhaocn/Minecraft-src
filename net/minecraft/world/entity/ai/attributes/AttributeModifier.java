/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.entity.ai.attributes;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public record AttributeModifier(ResourceLocation id, double amount, Operation operation) {
    public static final MapCodec<AttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifier::id), (App)Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::amount), (App)Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)).apply((Applicative)$$0, AttributeModifier::new));
    public static final Codec<AttributeModifier> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<ByteBuf, AttributeModifier> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, AttributeModifier::id, ByteBufCodecs.DOUBLE, AttributeModifier::amount, Operation.STREAM_CODEC, AttributeModifier::operation, AttributeModifier::new);

    public boolean is(ResourceLocation $$0) {
        return $$0.equals(this.id);
    }

    public static final class Operation
    extends Enum<Operation>
    implements StringRepresentable {
        public static final /* enum */ Operation ADD_VALUE = new Operation("add_value", 0);
        public static final /* enum */ Operation ADD_MULTIPLIED_BASE = new Operation("add_multiplied_base", 1);
        public static final /* enum */ Operation ADD_MULTIPLIED_TOTAL = new Operation("add_multiplied_total", 2);
        public static final IntFunction<Operation> BY_ID;
        public static final StreamCodec<ByteBuf, Operation> STREAM_CODEC;
        public static final Codec<Operation> CODEC;
        private final String name;
        private final int id;
        private static final /* synthetic */ Operation[] $VALUES;

        public static Operation[] values() {
            return (Operation[])$VALUES.clone();
        }

        public static Operation valueOf(String $$0) {
            return Enum.valueOf(Operation.class, $$0);
        }

        private Operation(String $$0, int $$1) {
            this.name = $$0;
            this.id = $$1;
        }

        public int id() {
            return this.id;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Operation[] b() {
            return new Operation[]{ADD_VALUE, ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL};
        }

        static {
            $VALUES = Operation.b();
            BY_ID = ByIdMap.a(Operation::id, Operation.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Operation::id);
            CODEC = StringRepresentable.fromEnum(Operation::values);
        }
    }
}

