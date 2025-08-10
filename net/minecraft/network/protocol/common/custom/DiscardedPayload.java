/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DiscardedPayload(ResourceLocation id) implements CustomPacketPayload
{
    public static <T extends FriendlyByteBuf> StreamCodec<T, DiscardedPayload> codec(ResourceLocation $$02, int $$12) {
        return CustomPacketPayload.codec((T $$0, B $$1) -> {}, (B $$2) -> {
            int $$3 = $$2.readableBytes();
            if ($$3 < 0 || $$3 > $$12) {
                throw new IllegalArgumentException("Payload may not be larger than " + $$12 + " bytes");
            }
            $$2.skipBytes($$3);
            return new DiscardedPayload($$02);
        });
    }

    public CustomPacketPayload.Type<DiscardedPayload> type() {
        return new CustomPacketPayload.Type<DiscardedPayload>(this.id);
    }
}

