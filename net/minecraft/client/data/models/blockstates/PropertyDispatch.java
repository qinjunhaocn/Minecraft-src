/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.datafixers.util.Function5
 */
package net.minecraft.client.data.models.blockstates;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch<V> {
    private final Map<PropertyValueList, V> values = new HashMap<PropertyValueList, V>();

    protected void putValue(PropertyValueList $$0, V $$1) {
        V $$2 = this.values.put($$0, $$1);
        if ($$2 != null) {
            throw new IllegalStateException("Value " + String.valueOf((Object)$$0) + " is already defined");
        }
    }

    Map<PropertyValueList, V> getEntries() {
        this.verifyComplete();
        return Map.copyOf(this.values);
    }

    private void verifyComplete() {
        List<Property<?>> $$02 = this.getDefinedProperties();
        Stream<PropertyValueList> $$12 = Stream.of(PropertyValueList.EMPTY);
        for (Property<?> $$2 : $$02) {
            $$12 = $$12.flatMap($$1 -> $$2.getAllValues().map($$1::extend));
        }
        List $$3 = $$12.filter($$0 -> !this.values.containsKey($$0)).toList();
        if (!$$3.isEmpty()) {
            throw new IllegalStateException("Missing definition for properties: " + String.valueOf($$3));
        }
    }

    abstract List<Property<?>> getDefinedProperties();

    public static <T1 extends Comparable<T1>> C1<MultiVariant, T1> initial(Property<T1> $$0) {
        return new C1($$0);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<MultiVariant, T1, T2> initial(Property<T1> $$0, Property<T2> $$1) {
        return new C2($$0, $$1);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<MultiVariant, T1, T2, T3> initial(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2) {
        return new C3($$0, $$1, $$2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<MultiVariant, T1, T2, T3, T4> initial(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3) {
        return new C4($$0, $$1, $$2, $$3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<MultiVariant, T1, T2, T3, T4, T5> initial(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3, Property<T5> $$4) {
        return new C5($$0, $$1, $$2, $$3, $$4);
    }

    public static <T1 extends Comparable<T1>> C1<VariantMutator, T1> modify(Property<T1> $$0) {
        return new C1($$0);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<VariantMutator, T1, T2> modify(Property<T1> $$0, Property<T2> $$1) {
        return new C2($$0, $$1);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<VariantMutator, T1, T2, T3> modify(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2) {
        return new C3($$0, $$1, $$2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<VariantMutator, T1, T2, T3, T4> modify(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3) {
        return new C4($$0, $$1, $$2, $$3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<VariantMutator, T1, T2, T3, T4, T5> modify(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3, Property<T5> $$4) {
        return new C5($$0, $$1, $$2, $$3, $$4);
    }

    public static class C1<V, T1 extends Comparable<T1>>
    extends PropertyDispatch<V> {
        private final Property<T1> property1;

        C1(Property<T1> $$0) {
            this.property1 = $$0;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return List.of(this.property1);
        }

        public C1<V, T1> select(T1 $$0, V $$1) {
            PropertyValueList $$2 = PropertyValueList.a(this.property1.value($$0));
            this.putValue($$2, $$1);
            return this;
        }

        public PropertyDispatch<V> generate(Function<T1, V> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.select($$1, $$0.apply($$1)));
            return this;
        }
    }

    public static class C2<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    extends PropertyDispatch<V> {
        private final Property<T1> property1;
        private final Property<T2> property2;

        C2(Property<T1> $$0, Property<T2> $$1) {
            this.property1 = $$0;
            this.property2 = $$1;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return List.of(this.property1, this.property2);
        }

        public C2<V, T1, T2> select(T1 $$0, T2 $$1, V $$2) {
            PropertyValueList $$3 = PropertyValueList.a(this.property1.value($$0), this.property2.value($$1));
            this.putValue($$3, $$2);
            return this;
        }

        public PropertyDispatch<V> generate(BiFunction<T1, T2, V> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.select($$1, $$2, $$0.apply($$1, $$2))));
            return this;
        }
    }

    public static class C3<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    extends PropertyDispatch<V> {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;

        C3(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return List.of(this.property1, this.property2, this.property3);
        }

        public C3<V, T1, T2, T3> select(T1 $$0, T2 $$1, T3 $$2, V $$3) {
            PropertyValueList $$4 = PropertyValueList.a(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2));
            this.putValue($$4, $$3);
            return this;
        }

        public PropertyDispatch<V> generate(Function3<T1, T2, T3, V> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.select($$1, $$2, $$3, $$0.apply($$1, $$2, $$3)))));
            return this;
        }
    }

    public static class C4<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    extends PropertyDispatch<V> {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;

        C4(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
            this.property4 = $$3;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return List.of(this.property1, this.property2, this.property3, this.property4);
        }

        public C4<V, T1, T2, T3, T4> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, V $$4) {
            PropertyValueList $$5 = PropertyValueList.a(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2), this.property4.value($$3));
            this.putValue($$5, $$4);
            return this;
        }

        public PropertyDispatch<V> generate(Function4<T1, T2, T3, T4, V> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.select($$1, $$2, $$3, $$4, $$0.apply($$1, $$2, $$3, $$4))))));
            return this;
        }
    }

    public static class C5<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
    extends PropertyDispatch<V> {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;
        private final Property<T5> property5;

        C5(Property<T1> $$0, Property<T2> $$1, Property<T3> $$2, Property<T4> $$3, Property<T5> $$4) {
            this.property1 = $$0;
            this.property2 = $$1;
            this.property3 = $$2;
            this.property4 = $$3;
            this.property5 = $$4;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return List.of(this.property1, this.property2, this.property3, this.property4, this.property5);
        }

        public C5<V, T1, T2, T3, T4, T5> select(T1 $$0, T2 $$1, T3 $$2, T4 $$3, T5 $$4, V $$5) {
            PropertyValueList $$6 = PropertyValueList.a(this.property1.value($$0), this.property2.value($$1), this.property3.value($$2), this.property4.value($$3), this.property5.value($$4));
            this.putValue($$6, $$5);
            return this;
        }

        public PropertyDispatch<V> generate(Function5<T1, T2, T3, T4, T5, V> $$0) {
            this.property1.getPossibleValues().forEach($$1 -> this.property2.getPossibleValues().forEach($$2 -> this.property3.getPossibleValues().forEach($$3 -> this.property4.getPossibleValues().forEach($$4 -> this.property5.getPossibleValues().forEach($$5 -> this.select($$1, $$2, $$3, $$4, $$5, $$0.apply($$1, $$2, $$3, $$4, $$5)))))));
            return this;
        }
    }
}

