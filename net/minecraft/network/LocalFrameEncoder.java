/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.HiddenByteBuf;

public class LocalFrameEncoder
extends ChannelOutboundHandlerAdapter {
    public void write(ChannelHandlerContext $$0, Object $$1, ChannelPromise $$2) {
        $$0.write(HiddenByteBuf.pack($$1), $$2);
    }
}

