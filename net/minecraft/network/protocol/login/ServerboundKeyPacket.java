/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.LoginPacketTypes;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ServerboundKeyPacket
implements Packet<ServerLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundKeyPacket> STREAM_CODEC = Packet.codec(ServerboundKeyPacket::write, ServerboundKeyPacket::new);
    private final byte[] keybytes;
    private final byte[] encryptedChallenge;

    public ServerboundKeyPacket(SecretKey $$0, PublicKey $$1, byte[] $$2) throws CryptException {
        this.keybytes = Crypt.a($$1, $$0.getEncoded());
        this.encryptedChallenge = Crypt.a($$1, $$2);
    }

    private ServerboundKeyPacket(FriendlyByteBuf $$0) {
        this.keybytes = $$0.b();
        this.encryptedChallenge = $$0.b();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.a(this.keybytes);
        $$0.a(this.encryptedChallenge);
    }

    @Override
    public PacketType<ServerboundKeyPacket> type() {
        return LoginPacketTypes.SERVERBOUND_KEY;
    }

    @Override
    public void handle(ServerLoginPacketListener $$0) {
        $$0.handleKey(this);
    }

    public SecretKey getSecretKey(PrivateKey $$0) throws CryptException {
        return Crypt.a($$0, this.keybytes);
    }

    public boolean a(byte[] $$0, PrivateKey $$1) {
        try {
            return Arrays.equals($$0, Crypt.b($$1, this.encryptedChallenge));
        } catch (CryptException $$2) {
            return false;
        }
    }
}

