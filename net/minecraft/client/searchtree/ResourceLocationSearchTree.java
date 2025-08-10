/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.SuffixArray;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationSearchTree<T> {
    public static <T> ResourceLocationSearchTree<T> empty() {
        return new ResourceLocationSearchTree<T>(){

            @Override
            public List<T> searchNamespace(String $$0) {
                return List.of();
            }

            @Override
            public List<T> searchPath(String $$0) {
                return List.of();
            }
        };
    }

    public static <T> ResourceLocationSearchTree<T> create(List<T> $$0, Function<T, Stream<ResourceLocation>> $$1) {
        if ($$0.isEmpty()) {
            return ResourceLocationSearchTree.empty();
        }
        final SuffixArray $$2 = new SuffixArray();
        final SuffixArray $$32 = new SuffixArray();
        for (Object $$4 : $$0) {
            $$1.apply($$4).forEach($$3 -> {
                $$2.add($$4, $$3.getNamespace().toLowerCase(Locale.ROOT));
                $$32.add($$4, $$3.getPath().toLowerCase(Locale.ROOT));
            });
        }
        $$2.generate();
        $$32.generate();
        return new ResourceLocationSearchTree<T>(){

            @Override
            public List<T> searchNamespace(String $$0) {
                return $$2.search($$0);
            }

            @Override
            public List<T> searchPath(String $$0) {
                return $$32.search($$0);
            }
        };
    }

    public List<T> searchNamespace(String var1);

    public List<T> searchPath(String var1);
}

