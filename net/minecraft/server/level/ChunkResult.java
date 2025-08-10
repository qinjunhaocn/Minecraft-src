/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ChunkResult<T> {
    public static <T> ChunkResult<T> of(T $$0) {
        return new Success<T>($$0);
    }

    public static <T> ChunkResult<T> error(String $$0) {
        return ChunkResult.error(() -> $$0);
    }

    public static <T> ChunkResult<T> error(Supplier<String> $$0) {
        return new Fail($$0);
    }

    public boolean isSuccess();

    @Nullable
    public T orElse(@Nullable T var1);

    @Nullable
    public static <R> R orElse(ChunkResult<? extends R> $$0, @Nullable R $$1) {
        R $$2 = $$0.orElse(null);
        return $$2 != null ? $$2 : (R)$$1;
    }

    @Nullable
    public String getError();

    public ChunkResult<T> ifSuccess(Consumer<T> var1);

    public <R> ChunkResult<R> map(Function<T, R> var1);

    public <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E;

    public record Success<T>(T value) implements ChunkResult<T>
    {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T orElse(@Nullable T $$0) {
            return this.value;
        }

        @Override
        @Nullable
        public String getError() {
            return null;
        }

        @Override
        public ChunkResult<T> ifSuccess(Consumer<T> $$0) {
            $$0.accept(this.value);
            return this;
        }

        @Override
        public <R> ChunkResult<R> map(Function<T, R> $$0) {
            return new Success<R>($$0.apply(this.value));
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> $$0) throws E {
            return this.value;
        }
    }

    public record Fail<T>(Supplier<String> error) implements ChunkResult<T>
    {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        @Nullable
        public T orElse(@Nullable T $$0) {
            return $$0;
        }

        @Override
        public String getError() {
            return this.error.get();
        }

        @Override
        public ChunkResult<T> ifSuccess(Consumer<T> $$0) {
            return this;
        }

        @Override
        public <R> ChunkResult<R> map(Function<T, R> $$0) {
            return new Fail<T>(this.error);
        }

        @Override
        public <E extends Throwable> T orElseThrow(Supplier<E> $$0) throws E {
            throw (Throwable)$$0.get();
        }
    }
}

