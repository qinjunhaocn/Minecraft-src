/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft.util.context;

import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import org.jetbrains.annotations.Contract;

public class ContextMap {
    private final Map<ContextKey<?>, Object> params;

    ContextMap(Map<ContextKey<?>, Object> $$0) {
        this.params = $$0;
    }

    public boolean has(ContextKey<?> $$0) {
        return this.params.containsKey($$0);
    }

    public <T> T getOrThrow(ContextKey<T> $$0) {
        Object $$1 = this.params.get($$0);
        if ($$1 == null) {
            throw new NoSuchElementException($$0.name().toString());
        }
        return (T)$$1;
    }

    @Nullable
    public <T> T getOptional(ContextKey<T> $$0) {
        return (T)this.params.get($$0);
    }

    @Nullable
    @Contract(value="_,!null->!null; _,_->_")
    public <T> T getOrDefault(ContextKey<T> $$0, @Nullable T $$1) {
        return (T)this.params.getOrDefault($$0, $$1);
    }

    public static class Builder {
        private final Map<ContextKey<?>, Object> params = new IdentityHashMap();

        public <T> Builder withParameter(ContextKey<T> $$0, T $$1) {
            this.params.put($$0, $$1);
            return this;
        }

        public <T> Builder withOptionalParameter(ContextKey<T> $$0, @Nullable T $$1) {
            if ($$1 == null) {
                this.params.remove($$0);
            } else {
                this.params.put($$0, $$1);
            }
            return this;
        }

        public <T> T getParameter(ContextKey<T> $$0) {
            Object $$1 = this.params.get($$0);
            if ($$1 == null) {
                throw new NoSuchElementException($$0.name().toString());
            }
            return (T)$$1;
        }

        @Nullable
        public <T> T getOptionalParameter(ContextKey<T> $$0) {
            return (T)this.params.get($$0);
        }

        public ContextMap create(ContextKeySet $$0) {
            Sets.SetView<ContextKey<?>> $$1 = Sets.difference(this.params.keySet(), $$0.allowed());
            if (!$$1.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + String.valueOf($$1));
            }
            Sets.SetView<ContextKey<?>> $$2 = Sets.difference($$0.required(), this.params.keySet());
            if (!$$2.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + String.valueOf($$2));
            }
            return new ContextMap(this.params);
        }
    }
}

