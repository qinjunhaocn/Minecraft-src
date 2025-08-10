/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer<T> {
    public Continuation accept(T var1);

    public static <T> AbortableIterationConsumer<T> forConsumer(Consumer<T> $$0) {
        return $$1 -> {
            $$0.accept($$1);
            return Continuation.CONTINUE;
        };
    }

    public static final class Continuation
    extends Enum<Continuation> {
        public static final /* enum */ Continuation CONTINUE = new Continuation();
        public static final /* enum */ Continuation ABORT = new Continuation();
        private static final /* synthetic */ Continuation[] $VALUES;

        public static Continuation[] values() {
            return (Continuation[])$VALUES.clone();
        }

        public static Continuation valueOf(String $$0) {
            return Enum.valueOf(Continuation.class, $$0);
        }

        public boolean shouldAbort() {
            return this == ABORT;
        }

        private static /* synthetic */ Continuation[] b() {
            return new Continuation[]{CONTINUE, ABORT};
        }

        static {
            $VALUES = Continuation.b();
        }
    }
}

