/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class IntrinsicHolderTagsProvider<T>
extends TagsProvider<T> {
    private final Function<T, ResourceKey<T>> keyExtractor;

    public IntrinsicHolderTagsProvider(PackOutput $$0, ResourceKey<? extends Registry<T>> $$1, CompletableFuture<HolderLookup.Provider> $$2, Function<T, ResourceKey<T>> $$3) {
        super($$0, $$1, $$2);
        this.keyExtractor = $$3;
    }

    public IntrinsicHolderTagsProvider(PackOutput $$0, ResourceKey<? extends Registry<T>> $$1, CompletableFuture<HolderLookup.Provider> $$2, CompletableFuture<TagsProvider.TagLookup<T>> $$3, Function<T, ResourceKey<T>> $$4) {
        super($$0, $$1, $$2, $$3);
        this.keyExtractor = $$4;
    }

    protected TagAppender<T, T> tag(TagKey<T> $$0) {
        TagBuilder $$1 = this.getOrCreateRawBuilder($$0);
        return TagAppender.forBuilder($$1).map(this.keyExtractor);
    }
}

