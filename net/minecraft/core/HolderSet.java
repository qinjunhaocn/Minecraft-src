/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.VisibleForTesting;

public interface HolderSet<T>
extends Iterable<Holder<T>> {
    public Stream<Holder<T>> stream();

    public int size();

    public boolean isBound();

    public Either<TagKey<T>, List<Holder<T>>> unwrap();

    public Optional<Holder<T>> getRandomElement(RandomSource var1);

    public Holder<T> get(int var1);

    public boolean contains(Holder<T> var1);

    public boolean canSerializeIn(HolderOwner<T> var1);

    public Optional<TagKey<T>> unwrapKey();

    @Deprecated
    @VisibleForTesting
    public static <T> Named<T> emptyNamed(HolderOwner<T> $$0, TagKey<T> $$1) {
        return new Named<T>((HolderOwner)$$0, (TagKey)$$1){

            @Override
            protected List<Holder<T>> contents() {
                throw new UnsupportedOperationException("Tag " + String.valueOf(this.key()) + " can't be dereferenced during construction");
            }
        };
    }

    public static <T> HolderSet<T> empty() {
        return Direct.EMPTY;
    }

    @SafeVarargs
    public static <T> Direct<T> a(Holder<T> ... $$0) {
        return new Direct(List.of((Object[])$$0));
    }

    public static <T> Direct<T> direct(List<? extends Holder<T>> $$0) {
        return new Direct(List.copyOf($$0));
    }

    @SafeVarargs
    public static <E, T> Direct<T> a(Function<E, Holder<T>> $$0, E ... $$1) {
        return HolderSet.direct(Stream.of($$1).map($$0).toList());
    }

    public static <E, T> Direct<T> direct(Function<E, Holder<T>> $$0, Collection<E> $$1) {
        return HolderSet.direct($$1.stream().map($$0).toList());
    }

    public static final class Direct<T>
    extends ListBacked<T> {
        static final Direct<?> EMPTY = new Direct(List.of());
        private final List<Holder<T>> contents;
        @Nullable
        private Set<Holder<T>> contentsSet;

        Direct(List<Holder<T>> $$0) {
            this.contents = $$0;
        }

        @Override
        protected List<Holder<T>> contents() {
            return this.contents;
        }

        @Override
        public boolean isBound() {
            return true;
        }

        @Override
        public Either<TagKey<T>, List<Holder<T>>> unwrap() {
            return Either.right(this.contents);
        }

        @Override
        public Optional<TagKey<T>> unwrapKey() {
            return Optional.empty();
        }

        @Override
        public boolean contains(Holder<T> $$0) {
            if (this.contentsSet == null) {
                this.contentsSet = Set.copyOf(this.contents);
            }
            return this.contentsSet.contains($$0);
        }

        public String toString() {
            return "DirectSet[" + String.valueOf(this.contents) + "]";
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if (!($$0 instanceof Direct)) return false;
            Direct $$1 = (Direct)$$0;
            if (!this.contents.equals($$1.contents)) return false;
            return true;
        }

        public int hashCode() {
            return this.contents.hashCode();
        }
    }

    public static class Named<T>
    extends ListBacked<T> {
        private final HolderOwner<T> owner;
        private final TagKey<T> key;
        @Nullable
        private List<Holder<T>> contents;

        Named(HolderOwner<T> $$0, TagKey<T> $$1) {
            this.owner = $$0;
            this.key = $$1;
        }

        void bind(List<Holder<T>> $$0) {
            this.contents = List.copyOf($$0);
        }

        public TagKey<T> key() {
            return this.key;
        }

        @Override
        protected List<Holder<T>> contents() {
            if (this.contents == null) {
                throw new IllegalStateException("Trying to access unbound tag '" + String.valueOf(this.key) + "' from registry " + String.valueOf(this.owner));
            }
            return this.contents;
        }

        @Override
        public boolean isBound() {
            return this.contents != null;
        }

        @Override
        public Either<TagKey<T>, List<Holder<T>>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public Optional<TagKey<T>> unwrapKey() {
            return Optional.of(this.key);
        }

        @Override
        public boolean contains(Holder<T> $$0) {
            return $$0.is(this.key);
        }

        public String toString() {
            return "NamedSet(" + String.valueOf(this.key) + ")[" + String.valueOf(this.contents) + "]";
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return this.owner.canSerializeIn($$0);
        }
    }

    public static abstract class ListBacked<T>
    implements HolderSet<T> {
        protected abstract List<Holder<T>> contents();

        @Override
        public int size() {
            return this.contents().size();
        }

        @Override
        public Spliterator<Holder<T>> spliterator() {
            return this.contents().spliterator();
        }

        @Override
        public Iterator<Holder<T>> iterator() {
            return this.contents().iterator();
        }

        @Override
        public Stream<Holder<T>> stream() {
            return this.contents().stream();
        }

        @Override
        public Optional<Holder<T>> getRandomElement(RandomSource $$0) {
            return Util.getRandomSafe(this.contents(), $$0);
        }

        @Override
        public Holder<T> get(int $$0) {
            return this.contents().get($$0);
        }

        @Override
        public boolean canSerializeIn(HolderOwner<T> $$0) {
            return true;
        }
    }
}

