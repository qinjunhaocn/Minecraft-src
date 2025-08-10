/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.Property;

public final class EnumProperty<T extends Enum<T>>
extends Property<T> {
    private final List<T> values;
    private final Map<String, T> names;
    private final int[] ordinalToIndex;

    private EnumProperty(String $$0, Class<T> $$1, List<T> $$2) {
        super($$0, $$1);
        if ($$2.isEmpty()) {
            throw new IllegalArgumentException("Trying to make empty EnumProperty '" + $$0 + "'");
        }
        this.values = List.copyOf($$2);
        Enum[] $$3 = (Enum[])$$1.getEnumConstants();
        this.ordinalToIndex = new int[$$3.length];
        for (Enum $$4 : $$3) {
            this.ordinalToIndex[$$4.ordinal()] = $$2.indexOf($$4);
        }
        ImmutableMap.Builder $$5 = ImmutableMap.builder();
        for (Enum $$6 : $$2) {
            String $$7 = ((StringRepresentable)((Object)$$6)).getSerializedName();
            $$5.put($$7, $$6);
        }
        this.names = $$5.buildOrThrow();
    }

    @Override
    public List<T> getPossibleValues() {
        return this.values;
    }

    @Override
    public Optional<T> getValue(String $$0) {
        return Optional.ofNullable((Enum)this.names.get($$0));
    }

    @Override
    public String getName(T $$0) {
        return ((StringRepresentable)$$0).getSerializedName();
    }

    @Override
    public int getInternalIndex(T $$0) {
        return this.ordinalToIndex[((Enum)$$0).ordinal()];
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof EnumProperty) {
            EnumProperty $$1 = (EnumProperty)$$0;
            if (super.equals($$0)) {
                return this.values.equals($$1.values);
            }
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        int $$0 = super.generateHashCode();
        $$0 = 31 * $$0 + this.values.hashCode();
        return $$0;
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$02, Class<T> $$1) {
        return EnumProperty.create($$02, $$1, (T $$0) -> true);
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$0, Class<T> $$1, Predicate<T> $$2) {
        return EnumProperty.create($$0, $$1, Arrays.stream((Enum[])$$1.getEnumConstants()).filter($$2).collect(Collectors.toList()));
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumProperty<T> a(String $$0, Class<T> $$1, T ... $$2) {
        return EnumProperty.create($$0, $$1, List.of((Object[])$$2));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String $$0, Class<T> $$1, List<T> $$2) {
        return new EnumProperty<T>($$0, $$1, $$2);
    }

    @Override
    public /* synthetic */ int getInternalIndex(Comparable comparable) {
        return this.getInternalIndex((Enum)((Object)comparable));
    }

    @Override
    public /* synthetic */ String getName(Comparable comparable) {
        return this.getName((Enum)((Object)comparable));
    }
}

