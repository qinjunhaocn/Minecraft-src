/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.FilteredText;

public interface TextFilter {
    public static final TextFilter DUMMY = new TextFilter(){

        @Override
        public CompletableFuture<FilteredText> processStreamMessage(String $$0) {
            return CompletableFuture.completedFuture(FilteredText.passThrough($$0));
        }

        @Override
        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> $$0) {
            return CompletableFuture.completedFuture((List)$$0.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
        }
    };

    default public void join() {
    }

    default public void leave() {
    }

    public CompletableFuture<FilteredText> processStreamMessage(String var1);

    public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1);
}

