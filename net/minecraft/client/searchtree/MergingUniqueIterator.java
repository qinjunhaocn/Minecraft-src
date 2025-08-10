/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class MergingUniqueIterator<T>
extends AbstractIterator<T> {
    private final PeekingIterator<T> firstIterator;
    private final PeekingIterator<T> secondIterator;
    private final Comparator<T> comparator;

    public MergingUniqueIterator(Iterator<T> $$0, Iterator<T> $$1, Comparator<T> $$2) {
        this.firstIterator = Iterators.peekingIterator($$0);
        this.secondIterator = Iterators.peekingIterator($$1);
        this.comparator = $$2;
    }

    @Override
    protected T computeNext() {
        boolean $$1;
        boolean $$0 = !this.firstIterator.hasNext();
        boolean bl = $$1 = !this.secondIterator.hasNext();
        if ($$0 && $$1) {
            return this.endOfData();
        }
        if ($$0) {
            return this.secondIterator.next();
        }
        if ($$1) {
            return this.firstIterator.next();
        }
        int $$2 = this.comparator.compare(this.firstIterator.peek(), this.secondIterator.peek());
        if ($$2 == 0) {
            this.secondIterator.next();
        }
        return $$2 <= 0 ? this.firstIterator.next() : this.secondIterator.next();
    }
}

