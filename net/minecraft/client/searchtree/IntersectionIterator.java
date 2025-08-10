/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class IntersectionIterator<T>
extends AbstractIterator<T> {
    private final PeekingIterator<T> firstIterator;
    private final PeekingIterator<T> secondIterator;
    private final Comparator<T> comparator;

    public IntersectionIterator(Iterator<T> $$0, Iterator<T> $$1, Comparator<T> $$2) {
        this.firstIterator = Iterators.peekingIterator($$0);
        this.secondIterator = Iterators.peekingIterator($$1);
        this.comparator = $$2;
    }

    @Override
    protected T computeNext() {
        while (this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
            int $$0 = this.comparator.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if ($$0 == 0) {
                this.secondIterator.next();
                return this.firstIterator.next();
            }
            if ($$0 < 0) {
                this.firstIterator.next();
                continue;
            }
            this.secondIterator.next();
        }
        return this.endOfData();
    }
}

