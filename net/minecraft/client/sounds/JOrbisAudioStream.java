/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.jcraft.jogg.Packet
 *  com.jcraft.jogg.Page
 *  com.jcraft.jogg.StreamState
 *  com.jcraft.jogg.SyncState
 *  com.jcraft.jorbis.Block
 *  com.jcraft.jorbis.Comment
 *  com.jcraft.jorbis.DspState
 *  com.jcraft.jorbis.Info
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 */
package net.minecraft.client.sounds;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.FloatSampleSource;

public class JOrbisAudioStream
implements FloatSampleSource {
    private static final int BUFSIZE = 8192;
    private static final int PAGEOUT_RECAPTURE = -1;
    private static final int PAGEOUT_NEED_MORE_DATA = 0;
    private static final int PAGEOUT_OK = 1;
    private static final int PACKETOUT_ERROR = -1;
    private static final int PACKETOUT_NEED_MORE_DATA = 0;
    private static final int PACKETOUT_OK = 1;
    private final SyncState syncState = new SyncState();
    private final Page page = new Page();
    private final StreamState streamState = new StreamState();
    private final Packet packet = new Packet();
    private final Info info = new Info();
    private final DspState dspState = new DspState();
    private final Block block = new Block(this.dspState);
    private final AudioFormat audioFormat;
    private final InputStream input;
    private long samplesWritten;
    private long totalSamplesInStream = Long.MAX_VALUE;

    public JOrbisAudioStream(InputStream $$0) throws IOException {
        this.input = $$0;
        Comment $$1 = new Comment();
        Page $$2 = this.readPage();
        if ($$2 == null) {
            throw new IOException("Invalid Ogg file - can't find first page");
        }
        Packet $$3 = this.readIdentificationPacket($$2);
        if (JOrbisAudioStream.isError(this.info.synthesis_headerin($$1, $$3))) {
            throw new IOException("Invalid Ogg identification packet");
        }
        for (int $$4 = 0; $$4 < 2; ++$$4) {
            Packet $$5 = this.readPacket();
            if ($$5 == null) {
                throw new IOException("Unexpected end of Ogg stream");
            }
            if (!JOrbisAudioStream.isError(this.info.synthesis_headerin($$1, $$5))) continue;
            throw new IOException("Invalid Ogg header packet " + $$4);
        }
        this.dspState.synthesis_init(this.info);
        this.block.init(this.dspState);
        this.audioFormat = new AudioFormat(this.info.rate, 16, this.info.channels, true, false);
    }

    private static boolean isError(int $$0) {
        return $$0 < 0;
    }

    @Override
    public AudioFormat getFormat() {
        return this.audioFormat;
    }

    private boolean readToBuffer() throws IOException {
        byte[] $$1 = this.syncState.data;
        int $$0 = this.syncState.buffer(8192);
        int $$2 = this.input.read($$1, $$0, 8192);
        if ($$2 == -1) {
            return false;
        }
        this.syncState.wrote($$2);
        return true;
    }

    @Nullable
    private Page readPage() throws IOException {
        int $$0;
        block5: while (true) {
            $$0 = this.syncState.pageout(this.page);
            switch ($$0) {
                case 1: {
                    if (this.page.eos() != 0) {
                        this.totalSamplesInStream = this.page.granulepos();
                    }
                    return this.page;
                }
                case 0: {
                    if (this.readToBuffer()) continue block5;
                    return null;
                }
                case -1: {
                    throw new IllegalStateException("Corrupt or missing data in bitstream");
                }
            }
            break;
        }
        throw new IllegalStateException("Unknown page decode result: " + $$0);
    }

    private Packet readIdentificationPacket(Page $$0) throws IOException {
        this.streamState.init($$0.serialno());
        if (JOrbisAudioStream.isError(this.streamState.pagein($$0))) {
            throw new IOException("Failed to parse page");
        }
        int $$1 = this.streamState.packetout(this.packet);
        if ($$1 != 1) {
            throw new IOException("Failed to read identification packet: " + $$1);
        }
        return this.packet;
    }

    @Nullable
    private Packet readPacket() throws IOException {
        block5: while (true) {
            int $$0 = this.streamState.packetout(this.packet);
            switch ($$0) {
                case 1: {
                    return this.packet;
                }
                case 0: {
                    Page $$1 = this.readPage();
                    if ($$1 != null) continue block5;
                    return null;
                    if (!JOrbisAudioStream.isError(this.streamState.pagein($$1))) continue block5;
                    throw new IOException("Failed to parse page");
                }
                case -1: {
                    throw new IOException("Failed to parse packet");
                }
                default: {
                    throw new IllegalStateException("Unknown packet decode result: " + $$0);
                }
            }
            break;
        }
    }

    private long getSamplesToWrite(int $$0) {
        long $$3;
        long $$1 = this.samplesWritten + (long)$$0;
        if ($$1 > this.totalSamplesInStream) {
            long $$2 = this.totalSamplesInStream - this.samplesWritten;
            this.samplesWritten = this.totalSamplesInStream;
        } else {
            this.samplesWritten = $$1;
            $$3 = $$0;
        }
        return $$3;
    }

    @Override
    public boolean readChunk(FloatConsumer $$0) throws IOException {
        int $$4;
        float[][][] $$1 = new float[1][][];
        int[] $$2 = new int[this.info.channels];
        Packet $$3 = this.readPacket();
        if ($$3 == null) {
            return false;
        }
        if (JOrbisAudioStream.isError(this.block.synthesis($$3))) {
            throw new IOException("Can't decode audio packet");
        }
        this.dspState.synthesis_blockin(this.block);
        while (($$4 = this.dspState.synthesis_pcmout((float[][][])$$1, $$2)) > 0) {
            float[][] $$5 = $$1[0];
            long $$6 = this.getSamplesToWrite($$4);
            switch (this.info.channels) {
                case 1: {
                    JOrbisAudioStream.a($$5[0], $$2[0], $$6, $$0);
                    break;
                }
                case 2: {
                    JOrbisAudioStream.a($$5[0], $$2[0], $$5[1], $$2[1], $$6, $$0);
                    break;
                }
                default: {
                    JOrbisAudioStream.a($$5, this.info.channels, $$2, $$6, $$0);
                }
            }
            this.dspState.synthesis_read($$4);
        }
        return true;
    }

    private static void a(float[][] $$0, int $$1, int[] $$2, long $$3, FloatConsumer $$4) {
        int $$5 = 0;
        while ((long)$$5 < $$3) {
            for (int $$6 = 0; $$6 < $$1; ++$$6) {
                int $$7 = $$2[$$6];
                float $$8 = $$0[$$6][$$7 + $$5];
                $$4.accept($$8);
            }
            ++$$5;
        }
    }

    private static void a(float[] $$0, int $$1, long $$2, FloatConsumer $$3) {
        int $$4 = $$1;
        while ((long)$$4 < (long)$$1 + $$2) {
            $$3.accept($$0[$$4]);
            ++$$4;
        }
    }

    private static void a(float[] $$0, int $$1, float[] $$2, int $$3, long $$4, FloatConsumer $$5) {
        int $$6 = 0;
        while ((long)$$6 < $$4) {
            $$5.accept($$0[$$1 + $$6]);
            $$5.accept($$2[$$3 + $$6]);
            ++$$6;
        }
    }

    @Override
    public void close() throws IOException {
        this.input.close();
    }
}

