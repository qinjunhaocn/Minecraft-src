/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.properties.Property;

public record PropertyValueList(List<Property.Value<?>> values) {
    public static final PropertyValueList EMPTY = new PropertyValueList(List.of());
    private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing($$0 -> $$0.property().getName());

    public PropertyValueList extend(Property.Value<?> $$0) {
        return new PropertyValueList(Util.copyAndAdd(this.values, $$0));
    }

    public PropertyValueList extend(PropertyValueList $$0) {
        return new PropertyValueList((List<Property.Value<?>>)((Object)((ImmutableList.Builder)((ImmutableList.Builder)ImmutableList.builder().addAll(this.values)).addAll($$0.values)).build()));
    }

    public static PropertyValueList a(Property.Value<?> ... $$0) {
        return new PropertyValueList(List.of($$0));
    }

    public String getKey() {
        return this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining(","));
    }

    public String toString() {
        return this.getKey();
    }
}

