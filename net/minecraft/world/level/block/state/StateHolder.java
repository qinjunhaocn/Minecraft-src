/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 */
package net.minecraft.world.level.block.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class StateHolder<O, S> {
    public static final String NAME_TAG = "Name";
    public static final String PROPERTIES_TAG = "Properties";
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Map.Entry<Property<?>, Comparable<?>>, String>(){

        @Override
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> $$0) {
            if ($$0 == null) {
                return "<NULL>";
            }
            Property<?> $$1 = $$0.getKey();
            return $$1.getName() + "=" + this.getName($$1, $$0.getValue());
        }

        private <T extends Comparable<T>> String getName(Property<T> $$0, Comparable<?> $$1) {
            return $$0.getName($$1);
        }

        @Override
        public /* synthetic */ Object apply(@Nullable Object object) {
            return this.apply((Map.Entry)object);
        }
    };
    protected final O owner;
    private final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;
    private Map<Property<?>, S[]> neighbours;
    protected final MapCodec<S> propertiesCodec;

    protected StateHolder(O $$0, Reference2ObjectArrayMap<Property<?>, Comparable<?>> $$1, MapCodec<S> $$2) {
        this.owner = $$0;
        this.values = $$1;
        this.propertiesCodec = $$2;
    }

    public <T extends Comparable<T>> S cycle(Property<T> $$0) {
        return this.setValue($$0, (Comparable)StateHolder.findNextInCollection($$0.getPossibleValues(), this.getValue($$0)));
    }

    protected static <T> T findNextInCollection(List<T> $$0, T $$1) {
        int $$2 = $$0.indexOf($$1) + 1;
        return (T)($$2 == $$0.size() ? $$0.getFirst() : $$0.get($$2));
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append(this.owner);
        if (!this.getValues().isEmpty()) {
            $$0.append('[');
            $$0.append(this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
            $$0.append(']');
        }
        return $$0.toString();
    }

    public final boolean equals(Object $$0) {
        return super.equals($$0);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Collection<Property<?>> getProperties() {
        return Collections.unmodifiableCollection(this.values.keySet());
    }

    public boolean hasProperty(Property<?> $$0) {
        return this.values.containsKey($$0);
    }

    public <T extends Comparable<T>> T getValue(Property<T> $$0) {
        Comparable $$1 = (Comparable)this.values.get($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("Cannot get property " + String.valueOf($$0) + " as it does not exist in " + String.valueOf(this.owner));
        }
        return (T)((Comparable)$$0.getValueClass().cast($$1));
    }

    public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> $$0) {
        return Optional.ofNullable(this.getNullableValue($$0));
    }

    public <T extends Comparable<T>> T getValueOrElse(Property<T> $$0, T $$1) {
        return (T)((Comparable)Objects.requireNonNullElse(this.getNullableValue($$0), $$1));
    }

    @Nullable
    private <T extends Comparable<T>> T getNullableValue(Property<T> $$0) {
        Comparable $$1 = (Comparable)this.values.get($$0);
        if ($$1 == null) {
            return null;
        }
        return (T)((Comparable)$$0.getValueClass().cast($$1));
    }

    public <T extends Comparable<T>, V extends T> S setValue(Property<T> $$0, V $$1) {
        Comparable $$2 = (Comparable)this.values.get($$0);
        if ($$2 == null) {
            throw new IllegalArgumentException("Cannot set property " + String.valueOf($$0) + " as it does not exist in " + String.valueOf(this.owner));
        }
        return this.setValueInternal($$0, $$1, $$2);
    }

    public <T extends Comparable<T>, V extends T> S trySetValue(Property<T> $$0, V $$1) {
        Comparable $$2 = (Comparable)this.values.get($$0);
        if ($$2 == null) {
            return (S)this;
        }
        return this.setValueInternal($$0, $$1, $$2);
    }

    private <T extends Comparable<T>, V extends T> S setValueInternal(Property<T> $$0, V $$1, Comparable<?> $$2) {
        if ($$2.equals($$1)) {
            return (S)this;
        }
        int $$3 = $$0.getInternalIndex($$1);
        if ($$3 < 0) {
            throw new IllegalArgumentException("Cannot set property " + String.valueOf($$0) + " to " + String.valueOf($$1) + " on " + String.valueOf(this.owner) + ", it is not an allowed value");
        }
        return this.neighbours.get($$0)[$$3];
    }

    public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> $$0) {
        if (this.neighbours != null) {
            throw new IllegalStateException();
        }
        Reference2ObjectArrayMap $$1 = new Reference2ObjectArrayMap(this.values.size());
        for (Map.Entry $$22 : this.values.entrySet()) {
            Property $$3 = (Property)$$22.getKey();
            $$1.put($$3, $$3.getPossibleValues().stream().map($$2 -> $$0.get(this.makeNeighbourValues($$3, (Comparable<?>)$$2))).toArray());
        }
        this.neighbours = $$1;
    }

    private Map<Property<?>, Comparable<?>> makeNeighbourValues(Property<?> $$0, Comparable<?> $$1) {
        Reference2ObjectArrayMap $$2 = new Reference2ObjectArrayMap(this.values);
        $$2.put($$0, $$1);
        return $$2;
    }

    public Map<Property<?>, Comparable<?>> getValues() {
        return this.values;
    }

    protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> $$02, Function<O, S> $$1) {
        return $$02.dispatch(NAME_TAG, $$0 -> $$0.owner, $$12 -> {
            StateHolder $$2 = (StateHolder)$$1.apply($$12);
            if ($$2.getValues().isEmpty()) {
                return MapCodec.unit((Object)$$2);
            }
            return $$2.propertiesCodec.codec().lenientOptionalFieldOf(PROPERTIES_TAG).xmap($$1 -> $$1.orElse($$2), Optional::of);
        });
    }
}

