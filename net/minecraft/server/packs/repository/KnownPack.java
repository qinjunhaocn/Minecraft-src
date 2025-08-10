/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.server.packs.repository;

import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KnownPack(String namespace, String id, String version) {
    public static final StreamCodec<ByteBuf, KnownPack> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, KnownPack::namespace, ByteBufCodecs.STRING_UTF8, KnownPack::id, ByteBufCodecs.STRING_UTF8, KnownPack::version, KnownPack::new);
    public static final String VANILLA_NAMESPACE = "minecraft";

    public static KnownPack vanilla(String $$0) {
        return new KnownPack(VANILLA_NAMESPACE, $$0, SharedConstants.getCurrentVersion().id());
    }

    public boolean isVanilla() {
        return this.namespace.equals(VANILLA_NAMESPACE);
    }

    public String toString() {
        return this.namespace + ":" + this.id + ":" + this.version;
    }
}

