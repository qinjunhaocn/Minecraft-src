/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network;

public final class ConnectionProtocol
extends Enum<ConnectionProtocol> {
    public static final /* enum */ ConnectionProtocol HANDSHAKING = new ConnectionProtocol("handshake");
    public static final /* enum */ ConnectionProtocol PLAY = new ConnectionProtocol("play");
    public static final /* enum */ ConnectionProtocol STATUS = new ConnectionProtocol("status");
    public static final /* enum */ ConnectionProtocol LOGIN = new ConnectionProtocol("login");
    public static final /* enum */ ConnectionProtocol CONFIGURATION = new ConnectionProtocol("configuration");
    private final String id;
    private static final /* synthetic */ ConnectionProtocol[] $VALUES;

    public static ConnectionProtocol[] values() {
        return (ConnectionProtocol[])$VALUES.clone();
    }

    public static ConnectionProtocol valueOf(String $$0) {
        return Enum.valueOf(ConnectionProtocol.class, $$0);
    }

    private ConnectionProtocol(String $$0) {
        this.id = $$0;
    }

    public String id() {
        return this.id;
    }

    private static /* synthetic */ ConnectionProtocol[] b() {
        return new ConnectionProtocol[]{HANDSHAKING, PLAY, STATUS, LOGIN, CONFIGURATION};
    }

    static {
        $VALUES = ConnectionProtocol.b();
    }
}

