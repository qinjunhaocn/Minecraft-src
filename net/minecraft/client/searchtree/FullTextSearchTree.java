/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.IntersectionIterator;
import net.minecraft.client.searchtree.MergingUniqueIterator;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.resources.ResourceLocation;

public class FullTextSearchTree<T>
extends IdSearchTree<T> {
    private final SearchTree<T> plainTextSearchTree;

    public FullTextSearchTree(Function<T, Stream<String>> $$0, Function<T, Stream<ResourceLocation>> $$1, List<T> $$2) {
        super($$1, $$2);
        this.plainTextSearchTree = SearchTree.plainText($$2, $$0);
    }

    @Override
    protected List<T> searchPlainText(String $$0) {
        return this.plainTextSearchTree.search($$0);
    }

    @Override
    protected List<T> searchResourceLocation(String $$0, String $$1) {
        List $$2 = this.resourceLocationSearchTree.searchNamespace($$0);
        List $$3 = this.resourceLocationSearchTree.searchPath($$1);
        List<T> $$4 = this.plainTextSearchTree.search($$1);
        MergingUniqueIterator $$5 = new MergingUniqueIterator($$3.iterator(), $$4.iterator(), this.additionOrder);
        return ImmutableList.copyOf(new IntersectionIterator($$2.iterator(), $$5, this.additionOrder));
    }
}

