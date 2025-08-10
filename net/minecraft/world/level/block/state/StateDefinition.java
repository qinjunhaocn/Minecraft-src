/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 */
package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
    static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> propertiesByName;
    private final ImmutableList<S> states;

    protected StateDefinition(Function<O, S> $$0, O $$12, Factory<O, S> $$2, Map<String, Property<?>> $$3) {
        this.owner = $$12;
        this.propertiesByName = ImmutableSortedMap.copyOf($$3);
        Supplier<StateHolder> $$4 = () -> (StateHolder)$$0.apply($$12);
        MapCodec<StateHolder> $$52 = MapCodec.of((MapEncoder)Encoder.empty(), (MapDecoder)Decoder.unit($$4));
        for (Map.Entry $$6 : this.propertiesByName.entrySet()) {
            $$52 = StateDefinition.appendPropertyCodec($$52, $$4, (String)$$6.getKey(), (Property)$$6.getValue());
        }
        MapCodec<StateHolder> $$7 = $$52;
        LinkedHashMap $$8 = Maps.newLinkedHashMap();
        ArrayList<StateHolder> $$9 = Lists.newArrayList();
        Stream<List<List<Object>>> $$10 = Stream.of(Collections.emptyList());
        for (Property $$11 : this.propertiesByName.values()) {
            $$10 = $$10.flatMap($$1 -> $$11.getPossibleValues().stream().map($$2 -> {
                ArrayList<Pair> $$3 = Lists.newArrayList($$1);
                $$3.add(Pair.of((Object)$$11, (Object)$$2));
                return $$3;
            }));
        }
        $$10.forEach($$5 -> {
            Reference2ObjectArrayMap $$6 = new Reference2ObjectArrayMap($$5.size());
            for (Pair $$7 : $$5) {
                $$6.put((Object)((Property)$$7.getFirst()), (Object)((Comparable)$$7.getSecond()));
            }
            StateHolder $$8 = (StateHolder)$$2.create($$12, $$6, $$7);
            $$8.put($$6, $$8);
            $$9.add($$8);
        });
        for (StateHolder $$122 : $$9) {
            $$122.populateNeighbours($$8);
        }
        this.states = ImmutableList.copyOf($$9);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(MapCodec<S> $$02, Supplier<S> $$12, String $$2, Property<T> $$3) {
        return Codec.mapPair($$02, (MapCodec)$$3.valueCodec().fieldOf($$2).orElseGet($$0 -> {}, () -> $$3.value((StateHolder)$$12.get()))).xmap($$1 -> (StateHolder)((StateHolder)$$1.getFirst()).setValue($$3, ((Property.Value)((Object)((Object)$$1.getSecond()))).value()), $$1 -> Pair.of((Object)$$1, $$3.value((StateHolder<?, ?>)$$1)));
    }

    public ImmutableList<S> getPossibleStates() {
        return this.states;
    }

    public S any() {
        return (S)((StateHolder)this.states.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.propertiesByName.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String $$0) {
        return this.propertiesByName.get($$0);
    }

    public static interface Factory<O, S> {
        public S create(O var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

    public static class Builder<O, S extends StateHolder<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = Maps.newHashMap();

        public Builder(O $$0) {
            this.owner = $$0;
        }

        public Builder<O, S> a(Property<?> ... $$0) {
            for (Property<?> $$1 : $$0) {
                this.validateProperty($$1);
                this.properties.put($$1.getName(), $$1);
            }
            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> $$0) {
            String $$1 = $$0.getName();
            if (!NAME_PATTERN.matcher($$1).matches()) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has invalidly named property: " + $$1);
            }
            List<T> $$2 = $$0.getPossibleValues();
            if ($$2.size() <= 1) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " attempted use property " + $$1 + " with <= 1 possible values");
            }
            for (Comparable $$3 : $$2) {
                String $$4 = $$0.getName($$3);
                if (NAME_PATTERN.matcher($$4).matches()) continue;
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has property: " + $$1 + " with invalidly named value: " + $$4);
            }
            if (this.properties.containsKey($$1)) {
                throw new IllegalArgumentException(String.valueOf(this.owner) + " has duplicate property: " + $$1);
            }
        }

        public StateDefinition<O, S> create(Function<O, S> $$0, Factory<O, S> $$1) {
            return new StateDefinition<O, S>($$0, this.owner, $$1, this.properties);
        }
    }
}

