/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;

public interface SimpleUnboundProtocol<T extends PacketListener, B extends ByteBuf>
extends ProtocolInfo.DetailsProvider {
    public ProtocolInfo<T> bind(Function<ByteBuf, B> var1);
}

