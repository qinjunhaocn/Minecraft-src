/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  org.lwjgl.BufferUtils
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.util.Mth;
import org.lwjgl.BufferUtils;

public class ChunkedSampleByteBuf
implements FloatConsumer {
    private final List<ByteBuffer> buffers = Lists.newArrayList();
    private final int bufferSize;
    private int byteCount;
    private ByteBuffer currentBuffer;

    public ChunkedSampleByteBuf(int $$0) {
        this.bufferSize = $$0 + 1 & 0xFFFFFFFE;
        this.currentBuffer = BufferUtils.createByteBuffer((int)$$0);
    }

    public void accept(float $$0) {
        if (this.currentBuffer.remaining() == 0) {
            this.currentBuffer.flip();
            this.buffers.add(this.currentBuffer);
            this.currentBuffer = BufferUtils.createByteBuffer((int)this.bufferSize);
        }
        int $$1 = Mth.clamp((int)($$0 * 32767.5f - 0.5f), Short.MIN_VALUE, Short.MAX_VALUE);
        this.currentBuffer.putShort((short)$$1);
        this.byteCount += 2;
    }

    public ByteBuffer get() {
        this.currentBuffer.flip();
        if (this.buffers.isEmpty()) {
            return this.currentBuffer;
        }
        ByteBuffer $$0 = BufferUtils.createByteBuffer((int)this.byteCount);
        this.buffers.forEach($$0::put);
        $$0.put(this.currentBuffer);
        $$0.flip();
        return $$0;
    }

    public int size() {
        return this.byteCount;
    }
}

