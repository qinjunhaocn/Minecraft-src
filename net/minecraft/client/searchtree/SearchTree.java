/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.SuffixArray;

@FunctionalInterface
public interface SearchTree<T> {
    public static <T> SearchTree<T> empty() {
        return $$0 -> List.of();
    }

    public static <T> SearchTree<T> plainText(List<T> $$0, Function<T, Stream<String>> $$1) {
        if ($$0.isEmpty()) {
            return SearchTree.empty();
        }
        SuffixArray $$22 = new SuffixArray();
        for (Object $$3 : $$0) {
            $$1.apply($$3).forEach($$2 -> $$22.add($$3, $$2.toLowerCase(Locale.ROOT)));
        }
        $$22.generate();
        return $$22::search;
    }

    public List<T> search(String var1);
}

