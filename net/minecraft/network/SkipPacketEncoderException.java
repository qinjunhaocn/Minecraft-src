/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network;

import io.netty.handler.codec.EncoderException;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.codec.IdDispatchCodec;

public class SkipPacketEncoderException
extends EncoderException
implements SkipPacketException,
IdDispatchCodec.DontDecorateException {
    public SkipPacketEncoderException(String $$0) {
        super($$0);
    }

    public SkipPacketEncoderException(Throwable $$0) {
        super($$0);
    }
}

