/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.util.SequencedCollection
 */
package net.minecraft.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import java.util.SequencedCollection;
import javax.annotation.Nullable;

public interface ListAndDeque<T>
extends Serializable,
Cloneable,
Deque<T>,
List<T>,
RandomAccess {
    public ListAndDeque<T> reversed();

    @Override
    public T getFirst();

    @Override
    public T getLast();

    @Override
    public void addFirst(T var1);

    @Override
    public void addLast(T var1);

    @Override
    public T removeFirst();

    @Override
    public T removeLast();

    @Override
    default public boolean offer(T $$0) {
        return this.offerLast($$0);
    }

    @Override
    default public T remove() {
        return this.removeFirst();
    }

    @Override
    @Nullable
    default public T poll() {
        return (T)this.pollFirst();
    }

    @Override
    default public T element() {
        return this.getFirst();
    }

    @Override
    @Nullable
    default public T peek() {
        return (T)this.peekFirst();
    }

    @Override
    default public void push(T $$0) {
        this.addFirst($$0);
    }

    @Override
    default public T pop() {
        return this.removeFirst();
    }

    default public /* synthetic */ List reversed() {
        return this.reversed();
    }

    default public /* synthetic */ SequencedCollection reversed() {
        return this.reversed();
    }

    default public /* synthetic */ Deque reversed() {
        return this.reversed();
    }
}

