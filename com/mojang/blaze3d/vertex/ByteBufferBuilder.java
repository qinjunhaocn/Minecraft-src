/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.MemoryUtil$MemoryAllocator
 */
package com.mojang.blaze3d.vertex;

import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class ByteBufferBuilder
implements AutoCloseable {
    private static final MemoryPool MEMORY_POOL = TracyClient.createMemoryPool((String)"ByteBufferBuilder");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator((boolean)false);
    private static final long DEFAULT_MAX_CAPACITY = 0xFFFFFFFFL;
    private static final int MAX_GROWTH_SIZE = 0x200000;
    private static final int BUFFER_FREED_GENERATION = -1;
    long pointer;
    private long capacity;
    private final long maxCapacity;
    private long writeOffset;
    private long nextResultOffset;
    private int resultCount;
    private int generation;

    public ByteBufferBuilder(int $$0, long $$1) {
        this.capacity = $$0;
        this.maxCapacity = $$1;
        this.pointer = ALLOCATOR.malloc((long)$$0);
        MEMORY_POOL.malloc(this.pointer, $$0);
        if (this.pointer == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + $$0 + " bytes");
        }
    }

    public ByteBufferBuilder(int $$0) {
        this($$0, 0xFFFFFFFFL);
    }

    public static ByteBufferBuilder exactlySized(int $$0) {
        return new ByteBufferBuilder($$0, $$0);
    }

    public long reserve(int $$0) {
        long $$1 = this.writeOffset;
        long $$2 = Math.addExact($$1, (long)$$0);
        this.ensureCapacity($$2);
        this.writeOffset = $$2;
        return Math.addExact(this.pointer, $$1);
    }

    private void ensureCapacity(long $$0) {
        if ($$0 > this.capacity) {
            if ($$0 > this.maxCapacity) {
                throw new IllegalArgumentException("Maximum capacity of ByteBufferBuilder (" + this.maxCapacity + ") exceeded, required " + $$0);
            }
            long $$1 = Math.min(this.capacity, 0x200000L);
            long $$2 = Mth.clamp(this.capacity + $$1, $$0, this.maxCapacity);
            this.resize($$2);
        }
    }

    private void resize(long $$0) {
        MEMORY_POOL.free(this.pointer);
        this.pointer = ALLOCATOR.realloc(this.pointer, $$0);
        MEMORY_POOL.malloc(this.pointer, (int)Math.min($$0, Integer.MAX_VALUE));
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)this.capacity, (Object)$$0);
        if (this.pointer == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + this.capacity + " bytes to " + $$0 + " bytes");
        }
        this.capacity = $$0;
    }

    @Nullable
    public Result build() {
        this.checkOpen();
        long $$0 = this.nextResultOffset;
        long $$1 = this.writeOffset - $$0;
        if ($$1 == 0L) {
            return null;
        }
        if ($$1 > Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot build buffer larger than 2147483647 bytes (was " + $$1 + ")");
        }
        this.nextResultOffset = this.writeOffset;
        ++this.resultCount;
        return new Result($$0, (int)$$1, this.generation);
    }

    public void clear() {
        if (this.resultCount > 0) {
            LOGGER.warn("Clearing BufferBuilder with unused batches");
        }
        this.discard();
    }

    public void discard() {
        this.checkOpen();
        if (this.resultCount > 0) {
            this.discardResults();
            this.resultCount = 0;
        }
    }

    boolean isValid(int $$0) {
        return $$0 == this.generation;
    }

    void freeResult() {
        if (--this.resultCount <= 0) {
            this.discardResults();
        }
    }

    private void discardResults() {
        long $$0 = this.writeOffset - this.nextResultOffset;
        if ($$0 > 0L) {
            MemoryUtil.memCopy((long)(this.pointer + this.nextResultOffset), (long)this.pointer, (long)$$0);
        }
        this.writeOffset = $$0;
        this.nextResultOffset = 0L;
        ++this.generation;
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            MEMORY_POOL.free(this.pointer);
            ALLOCATOR.free(this.pointer);
            this.pointer = 0L;
            this.generation = -1;
        }
    }

    private void checkOpen() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Buffer has been freed");
        }
    }

    public class Result
    implements AutoCloseable {
        private final long offset;
        private final int capacity;
        private final int generation;
        private boolean closed;

        Result(long $$1, int $$2, int $$3) {
            this.offset = $$1;
            this.capacity = $$2;
            this.generation = $$3;
        }

        public ByteBuffer byteBuffer() {
            if (!ByteBufferBuilder.this.isValid(this.generation)) {
                throw new IllegalStateException("Buffer is no longer valid");
            }
            return MemoryUtil.memByteBuffer((long)(ByteBufferBuilder.this.pointer + this.offset), (int)this.capacity);
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (ByteBufferBuilder.this.isValid(this.generation)) {
                ByteBufferBuilder.this.freeResult();
            }
        }
    }
}

