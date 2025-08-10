/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.sounds;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;

public class LoopingAudioStream
implements AudioStream {
    private final AudioStreamProvider provider;
    private AudioStream stream;
    private final BufferedInputStream bufferedInputStream;

    public LoopingAudioStream(AudioStreamProvider $$0, InputStream $$1) throws IOException {
        this.provider = $$0;
        this.bufferedInputStream = new BufferedInputStream($$1);
        this.bufferedInputStream.mark(Integer.MAX_VALUE);
        this.stream = $$0.create(new NoCloseBuffer(this.bufferedInputStream));
    }

    @Override
    public AudioFormat getFormat() {
        return this.stream.getFormat();
    }

    @Override
    public ByteBuffer read(int $$0) throws IOException {
        ByteBuffer $$1 = this.stream.read($$0);
        if (!$$1.hasRemaining()) {
            this.stream.close();
            this.bufferedInputStream.reset();
            this.stream = this.provider.create(new NoCloseBuffer(this.bufferedInputStream));
            $$1 = this.stream.read($$0);
        }
        return $$1;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
        this.bufferedInputStream.close();
    }

    @FunctionalInterface
    public static interface AudioStreamProvider {
        public AudioStream create(InputStream var1) throws IOException;
    }

    static class NoCloseBuffer
    extends FilterInputStream {
        NoCloseBuffer(InputStream $$0) {
            super($$0);
        }

        @Override
        public void close() {
        }
    }
}

