/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 */
package net.minecraft.client.sounds;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.minecraft.client.sounds.ChunkedSampleByteBuf;
import net.minecraft.client.sounds.FiniteAudioStream;

public interface FloatSampleSource
extends FiniteAudioStream {
    public static final int EXPECTED_MAX_FRAME_SIZE = 8192;

    public boolean readChunk(FloatConsumer var1) throws IOException;

    @Override
    default public ByteBuffer read(int $$0) throws IOException {
        ChunkedSampleByteBuf $$1 = new ChunkedSampleByteBuf($$0 + 8192);
        while (this.readChunk($$1) && $$1.size() < $$0) {
        }
        return $$1.get();
    }

    @Override
    default public ByteBuffer readAll() throws IOException {
        ChunkedSampleByteBuf $$0 = new ChunkedSampleByteBuf(16384);
        while (this.readChunk($$0)) {
        }
        return $$0.get();
    }
}

