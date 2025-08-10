/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;

public class ValueInputContextHelper {
    final HolderLookup.Provider lookup;
    private final DynamicOps<Tag> ops;
    final ValueInput.ValueInputList emptyChildList = new ValueInput.ValueInputList(this){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Stream<ValueInput> stream() {
            return Stream.empty();
        }

        @Override
        public Iterator<ValueInput> iterator() {
            return Collections.emptyIterator();
        }
    };
    private final ValueInput.TypedInputList<Object> emptyTypedList = new ValueInput.TypedInputList<Object>(this){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Stream<Object> stream() {
            return Stream.empty();
        }

        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyIterator();
        }
    };
    private final ValueInput empty = new ValueInput(){

        @Override
        public <T> Optional<T> read(String $$0, Codec<T> $$1) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> read(MapCodec<T> $$0) {
            return Optional.empty();
        }

        @Override
        public Optional<ValueInput> child(String $$0) {
            return Optional.empty();
        }

        @Override
        public ValueInput childOrEmpty(String $$0) {
            return this;
        }

        @Override
        public Optional<ValueInput.ValueInputList> childrenList(String $$0) {
            return Optional.empty();
        }

        @Override
        public ValueInput.ValueInputList childrenListOrEmpty(String $$0) {
            return ValueInputContextHelper.this.emptyChildList;
        }

        @Override
        public <T> Optional<ValueInput.TypedInputList<T>> list(String $$0, Codec<T> $$1) {
            return Optional.empty();
        }

        @Override
        public <T> ValueInput.TypedInputList<T> listOrEmpty(String $$0, Codec<T> $$1) {
            return ValueInputContextHelper.this.emptyTypedList();
        }

        @Override
        public boolean getBooleanOr(String $$0, boolean $$1) {
            return $$1;
        }

        @Override
        public byte getByteOr(String $$0, byte $$1) {
            return $$1;
        }

        @Override
        public int getShortOr(String $$0, short $$1) {
            return $$1;
        }

        @Override
        public Optional<Integer> getInt(String $$0) {
            return Optional.empty();
        }

        @Override
        public int getIntOr(String $$0, int $$1) {
            return $$1;
        }

        @Override
        public long getLongOr(String $$0, long $$1) {
            return $$1;
        }

        @Override
        public Optional<Long> getLong(String $$0) {
            return Optional.empty();
        }

        @Override
        public float getFloatOr(String $$0, float $$1) {
            return $$1;
        }

        @Override
        public double getDoubleOr(String $$0, double $$1) {
            return $$1;
        }

        @Override
        public Optional<String> getString(String $$0) {
            return Optional.empty();
        }

        @Override
        public String getStringOr(String $$0, String $$1) {
            return $$1;
        }

        @Override
        public HolderLookup.Provider lookup() {
            return ValueInputContextHelper.this.lookup;
        }

        @Override
        public Optional<int[]> getIntArray(String $$0) {
            return Optional.empty();
        }
    };

    public ValueInputContextHelper(HolderLookup.Provider $$0, DynamicOps<Tag> $$1) {
        this.lookup = $$0;
        this.ops = $$0.createSerializationContext($$1);
    }

    public DynamicOps<Tag> ops() {
        return this.ops;
    }

    public HolderLookup.Provider lookup() {
        return this.lookup;
    }

    public ValueInput empty() {
        return this.empty;
    }

    public ValueInput.ValueInputList emptyList() {
        return this.emptyChildList;
    }

    public <T> ValueInput.TypedInputList<T> emptyTypedList() {
        return this.emptyTypedList;
    }
}

