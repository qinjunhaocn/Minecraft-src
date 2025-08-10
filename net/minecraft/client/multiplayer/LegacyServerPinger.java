/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.util.concurrent.GenericFutureListener
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.List;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.server.network.LegacyProtocolUtils;
import net.minecraft.util.Mth;

public class LegacyServerPinger
extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
    private final ServerAddress address;
    private final Output output;

    public LegacyServerPinger(ServerAddress $$0, Output $$1) {
        this.address = $$0;
        this.output = $$1;
    }

    public void channelActive(ChannelHandlerContext $$0) throws Exception {
        super.channelActive($$0);
        ByteBuf $$1 = $$0.alloc().buffer();
        try {
            $$1.writeByte(254);
            $$1.writeByte(1);
            $$1.writeByte(250);
            LegacyProtocolUtils.writeLegacyString($$1, "MC|PingHost");
            int $$2 = $$1.writerIndex();
            $$1.writeShort(0);
            int $$3 = $$1.writerIndex();
            $$1.writeByte(127);
            LegacyProtocolUtils.writeLegacyString($$1, this.address.getHost());
            $$1.writeInt(this.address.getPort());
            int $$4 = $$1.writerIndex() - $$3;
            $$1.setShort($$2, $$4);
            $$0.channel().writeAndFlush((Object)$$1).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
        } catch (Exception $$5) {
            $$1.release();
            throw $$5;
        }
    }

    protected void channelRead0(ChannelHandlerContext $$0, ByteBuf $$1) {
        String $$3;
        List<String> $$4;
        short $$2 = $$1.readUnsignedByte();
        if ($$2 == 255 && "\u00a71".equals(($$4 = SPLITTER.splitToList($$3 = LegacyProtocolUtils.readLegacyString($$1))).get(0))) {
            int $$5 = Mth.getInt($$4.get(1), 0);
            String $$6 = $$4.get(2);
            String $$7 = $$4.get(3);
            int $$8 = Mth.getInt($$4.get(4), -1);
            int $$9 = Mth.getInt($$4.get(5), -1);
            this.output.handleResponse($$5, $$6, $$7, $$8, $$9);
        }
        $$0.close();
    }

    public void exceptionCaught(ChannelHandlerContext $$0, Throwable $$1) {
        $$0.close();
    }

    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.channelRead0(channelHandlerContext, (ByteBuf)object);
    }

    @FunctionalInterface
    public static interface Output {
        public void handleResponse(int var1, String var2, String var3, int var4, int var5);
    }
}

