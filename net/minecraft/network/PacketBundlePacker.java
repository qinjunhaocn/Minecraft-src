/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.MessageToMessageDecoder
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundlePacker
extends MessageToMessageDecoder<Packet<?>> {
    private final BundlerInfo bundlerInfo;
    @Nullable
    private BundlerInfo.Bundler currentBundler;

    public PacketBundlePacker(BundlerInfo $$0) {
        this.bundlerInfo = $$0;
    }

    protected void decode(ChannelHandlerContext $$0, Packet<?> $$1, List<Object> $$2) throws Exception {
        if (this.currentBundler != null) {
            PacketBundlePacker.verifyNonTerminalPacket($$1);
            Packet<?> $$3 = this.currentBundler.addPacket($$1);
            if ($$3 != null) {
                this.currentBundler = null;
                $$2.add($$3);
            }
        } else {
            BundlerInfo.Bundler $$4 = this.bundlerInfo.startPacketBundling($$1);
            if ($$4 != null) {
                PacketBundlePacker.verifyNonTerminalPacket($$1);
                this.currentBundler = $$4;
            } else {
                $$2.add($$1);
                if ($$1.isTerminal()) {
                    $$0.pipeline().remove($$0.name());
                }
            }
        }
    }

    private static void verifyNonTerminalPacket(Packet<?> $$0) {
        if ($$0.isTerminal()) {
            throw new DecoderException("Terminal message received in bundle");
        }
    }

    protected /* synthetic */ void decode(ChannelHandlerContext channelHandlerContext, Object object, List list) throws Exception {
        this.decode(channelHandlerContext, (Packet)object, (List<Object>)list);
    }
}

