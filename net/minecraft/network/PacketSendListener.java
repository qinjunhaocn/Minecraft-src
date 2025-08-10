/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.channel.ChannelFutureListener
 */
package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Supplier;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public class PacketSendListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ChannelFutureListener thenRun(Runnable $$0) {
        return $$1 -> {
            $$0.run();
            if (!$$1.isSuccess()) {
                $$1.channel().pipeline().fireExceptionCaught($$1.cause());
            }
        };
    }

    public static ChannelFutureListener exceptionallySend(Supplier<Packet<?>> $$0) {
        return $$1 -> {
            if (!$$1.isSuccess()) {
                Packet $$2 = (Packet)$$0.get();
                if ($$2 != null) {
                    LOGGER.warn("Failed to deliver packet, sending fallback {}", (Object)$$2.type(), (Object)$$1.cause());
                    $$1.channel().writeAndFlush((Object)$$2, $$1.channel().voidPromise());
                } else {
                    $$1.channel().pipeline().fireExceptionCaught($$1.cause());
                }
            }
        };
    }
}

