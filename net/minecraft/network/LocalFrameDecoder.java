/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.HiddenByteBuf;

public class LocalFrameDecoder
extends ChannelInboundHandlerAdapter {
    public void channelRead(ChannelHandlerContext $$0, Object $$1) {
        $$0.fireChannelRead(HiddenByteBuf.unpack($$1));
    }
}

