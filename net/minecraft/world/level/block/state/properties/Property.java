/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;

public abstract class Property<T extends Comparable<T>> {
    private final Class<T> clazz;
    private final String name;
    @Nullable
    private Integer hashCode;
    private final Codec<T> codec = Codec.STRING.comapFlatMap($$0 -> this.getValue((String)$$0).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unable to read property: " + String.valueOf(this) + " with value: " + $$0)), this::getName);
    private final Codec<Value<T>> valueCodec = this.codec.xmap(this::value, Value::value);

    protected Property(String $$02, Class<T> $$1) {
        this.clazz = $$1;
        this.name = $$02;
    }

    public Value<T> value(T $$0) {
        return new Value<T>(this, $$0);
    }

    public Value<T> value(StateHolder<?, ?> $$0) {
        return new Value(this, $$0.getValue(this));
    }

    public Stream<Value<T>> getAllValues() {
        return this.getPossibleValues().stream().map(this::value);
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public Codec<Value<T>> valueCodec() {
        return this.valueCodec;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getValueClass() {
        return this.clazz;
    }

    public abstract List<T> getPossibleValues();

    public abstract String getName(T var1);

    public abstract Optional<T> getValue(String var1);

    public abstract int getInternalIndex(T var1);

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof Property) {
            Property $$1 = (Property)$$0;
            return this.clazz.equals($$1.clazz) && this.name.equals($$1.name);
        }
        return false;
    }

    public final int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.generateHashCode();
        }
        return this.hashCode;
    }

    public int generateHashCode() {
        return 31 * this.clazz.hashCode() + this.name.hashCode();
    }

    public <U, S extends StateHolder<?, S>> DataResult<S> parseValue(DynamicOps<U> $$0, S $$12, U $$2) {
        DataResult $$3 = this.codec.parse($$0, $$2);
        return $$3.map($$1 -> (StateHolder)$$12.setValue(this, $$1)).setPartial($$12);
    }

    public record Value<T extends Comparable<T>>(Property<T> property, T value) {
        public Value {
            if (!$$0.getPossibleValues().contains($$1)) {
                throw new IllegalArgumentException("Value " + String.valueOf($$1) + " does not belong to property " + String.valueOf($$0));
            }
        }

        public String toString() {
            return this.property.getName() + "=" + this.property.getName(this.value);
        }
    }
}

