/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record CombinedCondition(Operation operation, List<Condition> terms) implements Condition
{
    @Override
    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> $$0) {
        return this.operation.apply(Lists.transform(this.terms, $$1 -> $$1.instantiate($$0)));
    }

    public static abstract sealed class Operation
    extends Enum<Operation>
    implements StringRepresentable {
        public static final /* enum */ Operation AND = new Operation("AND"){

            @Override
            public <V> Predicate<V> apply(List<Predicate<V>> $$0) {
                return Util.allOf($$0);
            }
        };
        public static final /* enum */ Operation OR = new Operation("OR"){

            @Override
            public <V> Predicate<V> apply(List<Predicate<V>> $$0) {
                return Util.anyOf($$0);
            }
        };
        public static final Codec<Operation> CODEC;
        private final String name;
        private static final /* synthetic */ Operation[] $VALUES;

        public static Operation[] values() {
            return (Operation[])$VALUES.clone();
        }

        public static Operation valueOf(String $$0) {
            return Enum.valueOf(Operation.class, $$0);
        }

        Operation(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public abstract <V> Predicate<V> apply(List<Predicate<V>> var1);

        private static /* synthetic */ Operation[] a() {
            return new Operation[]{AND, OR};
        }

        static {
            $VALUES = Operation.a();
            CODEC = StringRepresentable.fromEnum(Operation::values);
        }
    }
}

