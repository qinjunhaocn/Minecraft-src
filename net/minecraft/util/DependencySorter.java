/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DependencySorter<K, V extends Entry<K>> {
    private final Map<K, V> contents = new HashMap();

    public DependencySorter<K, V> addEntry(K $$0, V $$1) {
        this.contents.put($$0, $$1);
        return this;
    }

    private void visitDependenciesAndElement(Multimap<K, K> $$0, Set<K> $$1, K $$2, BiConsumer<K, V> $$32) {
        if (!$$1.add($$2)) {
            return;
        }
        $$0.get($$2).forEach($$3 -> this.visitDependenciesAndElement($$0, $$1, $$3, $$32));
        Entry $$4 = (Entry)this.contents.get($$2);
        if ($$4 != null) {
            $$32.accept($$2, $$4);
        }
    }

    private static <K> boolean isCyclic(Multimap<K, K> $$0, K $$1, K $$22) {
        Collection<K> $$3 = $$0.get($$22);
        if ($$3.contains($$1)) {
            return true;
        }
        return $$3.stream().anyMatch($$2 -> DependencySorter.isCyclic($$0, $$1, $$2));
    }

    private static <K> void addDependencyIfNotCyclic(Multimap<K, K> $$0, K $$1, K $$2) {
        if (!DependencySorter.isCyclic($$0, $$1, $$2)) {
            $$0.put($$1, $$2);
        }
    }

    public void orderByDependencies(BiConsumer<K, V> $$0) {
        HashMultimap $$12 = HashMultimap.create();
        this.contents.forEach(($$1, $$22) -> $$22.visitRequiredDependencies($$2 -> DependencySorter.addDependencyIfNotCyclic($$12, $$1, $$2)));
        this.contents.forEach(($$1, $$22) -> $$22.visitOptionalDependencies($$2 -> DependencySorter.addDependencyIfNotCyclic($$12, $$1, $$2)));
        HashSet $$2 = new HashSet();
        this.contents.keySet().forEach($$3 -> this.visitDependenciesAndElement($$12, $$2, $$3, $$0));
    }

    public static interface Entry<K> {
        public void visitRequiredDependencies(Consumer<K> var1);

        public void visitOptionalDependencies(Consumer<K> var1);
    }
}

