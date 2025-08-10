/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Record
 */
package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public final class MessageSignature
extends Record {
    private final byte[] bytes;
    public static final Codec<MessageSignature> CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::c);
    public static final int BYTES = 256;

    public MessageSignature(byte[] $$0) {
        Preconditions.checkState($$0.length == 256, "Invalid message signature size");
        this.bytes = $$0;
    }

    public static MessageSignature read(FriendlyByteBuf $$0) {
        byte[] $$1 = new byte[256];
        $$0.b($$1);
        return new MessageSignature($$1);
    }

    public static void write(FriendlyByteBuf $$0, MessageSignature $$1) {
        $$0.c($$1.bytes);
    }

    public boolean verify(SignatureValidator $$0, SignatureUpdater $$1) {
        return $$0.validate($$1, this.bytes);
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.bytes);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) return true;
        if (!($$0 instanceof MessageSignature)) return false;
        MessageSignature $$1 = (MessageSignature)((Object)$$0);
        if (!Arrays.equals(this.bytes, $$1.bytes)) return false;
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }

    public static String describe(@Nullable MessageSignature $$0) {
        if ($$0 == null) {
            return "<no signature>";
        }
        return $$0.toString();
    }

    public Packed pack(MessageSignatureCache $$0) {
        int $$1 = $$0.pack(this);
        return $$1 != -1 ? new Packed($$1) : new Packed(this);
    }

    public int checksum() {
        return Arrays.hashCode(this.bytes);
    }

    public byte[] c() {
        return this.bytes;
    }

    public record Packed(int id, @Nullable MessageSignature fullSignature) {
        public static final int FULL_SIGNATURE = -1;

        public Packed(MessageSignature $$0) {
            this(-1, $$0);
        }

        public Packed(int $$0) {
            this($$0, null);
        }

        public static Packed read(FriendlyByteBuf $$0) {
            int $$1 = $$0.readVarInt() - 1;
            if ($$1 == -1) {
                return new Packed(MessageSignature.read($$0));
            }
            return new Packed($$1);
        }

        public static void write(FriendlyByteBuf $$0, Packed $$1) {
            $$0.writeVarInt($$1.id() + 1);
            if ($$1.fullSignature() != null) {
                MessageSignature.write($$0, $$1.fullSignature());
            }
        }

        public Optional<MessageSignature> unpack(MessageSignatureCache $$0) {
            if (this.fullSignature != null) {
                return Optional.of(this.fullSignature);
            }
            return Optional.ofNullable($$0.unpack(this.id));
        }

        @Nullable
        public MessageSignature fullSignature() {
            return this.fullSignature;
        }
    }
}

