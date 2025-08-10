/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CipherBase {
    private final Cipher cipher;
    private byte[] heapIn = new byte[0];
    private byte[] heapOut = new byte[0];

    protected CipherBase(Cipher $$0) {
        this.cipher = $$0;
    }

    private byte[] a(ByteBuf $$0) {
        int $$1 = $$0.readableBytes();
        if (this.heapIn.length < $$1) {
            this.heapIn = new byte[$$1];
        }
        $$0.readBytes(this.heapIn, 0, $$1);
        return this.heapIn;
    }

    protected ByteBuf decipher(ChannelHandlerContext $$0, ByteBuf $$1) throws ShortBufferException {
        int $$2 = $$1.readableBytes();
        byte[] $$3 = this.a($$1);
        ByteBuf $$4 = $$0.alloc().heapBuffer(this.cipher.getOutputSize($$2));
        $$4.writerIndex(this.cipher.update($$3, 0, $$2, $$4.array(), $$4.arrayOffset()));
        return $$4;
    }

    protected void encipher(ByteBuf $$0, ByteBuf $$1) throws ShortBufferException {
        int $$2 = $$0.readableBytes();
        byte[] $$3 = this.a($$0);
        int $$4 = this.cipher.getOutputSize($$2);
        if (this.heapOut.length < $$4) {
            this.heapOut = new byte[$$4];
        }
        $$1.writeBytes(this.heapOut, 0, this.cipher.update($$3, 0, $$2, this.heapOut));
    }
}

