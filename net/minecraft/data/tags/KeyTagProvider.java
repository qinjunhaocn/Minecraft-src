/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class KeyTagProvider<T>
extends TagsProvider<T> {
    protected KeyTagProvider(PackOutput $$0, ResourceKey<? extends Registry<T>> $$1, CompletableFuture<HolderLookup.Provider> $$2) {
        super($$0, $$1, $$2);
    }

    protected TagAppender<ResourceKey<T>, T> tag(TagKey<T> $$0) {
        TagBuilder $$1 = this.getOrCreateRawBuilder($$0);
        return TagAppender.forBuilder($$1);
    }
}

