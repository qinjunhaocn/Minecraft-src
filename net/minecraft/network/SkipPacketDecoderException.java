/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.network;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.codec.IdDispatchCodec;

public class SkipPacketDecoderException
extends DecoderException
implements SkipPacketException,
IdDispatchCodec.DontDecorateException {
    public SkipPacketDecoderException(String $$0) {
        super($$0);
    }

    public SkipPacketDecoderException(Throwable $$0) {
        super($$0);
    }
}

