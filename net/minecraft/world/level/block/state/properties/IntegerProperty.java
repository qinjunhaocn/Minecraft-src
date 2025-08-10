/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntImmutableList
 */
package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.ints.IntImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.world.level.block.state.properties.Property;

public final class IntegerProperty
extends Property<Integer> {
    private final IntImmutableList values;
    private final int min;
    private final int max;

    private IntegerProperty(String $$0, int $$1, int $$2) {
        super($$0, Integer.class);
        if ($$1 < 0) {
            throw new IllegalArgumentException("Min value of " + $$0 + " must be 0 or greater");
        }
        if ($$2 <= $$1) {
            throw new IllegalArgumentException("Max value of " + $$0 + " must be greater than min (" + $$1 + ")");
        }
        this.min = $$1;
        this.max = $$2;
        this.values = IntImmutableList.toList((IntStream)IntStream.range($$1, $$2 + 1));
    }

    @Override
    public List<Integer> getPossibleValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof IntegerProperty) {
            IntegerProperty $$1 = (IntegerProperty)$$0;
            if (super.equals($$0)) {
                return this.values.equals($$1.values);
            }
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }

    public static IntegerProperty create(String $$0, int $$1, int $$2) {
        return new IntegerProperty($$0, $$1, $$2);
    }

    @Override
    public Optional<Integer> getValue(String $$0) {
        try {
            int $$1 = Integer.parseInt($$0);
            return $$1 >= this.min && $$1 <= this.max ? Optional.of($$1) : Optional.empty();
        } catch (NumberFormatException $$2) {
            return Optional.empty();
        }
    }

    @Override
    public String getName(Integer $$0) {
        return $$0.toString();
    }

    @Override
    public int getInternalIndex(Integer $$0) {
        if ($$0 <= this.max) {
            return $$0 - this.min;
        }
        return -1;
    }

    @Override
    public /* synthetic */ int getInternalIndex(Comparable comparable) {
        return this.getInternalIndex((Integer)comparable);
    }

    @Override
    public /* synthetic */ String getName(Comparable comparable) {
        return this.getName((Integer)comparable);
    }
}

