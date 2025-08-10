/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.network.protocol.handshake;

public final class ClientIntent
extends Enum<ClientIntent> {
    public static final /* enum */ ClientIntent STATUS = new ClientIntent();
    public static final /* enum */ ClientIntent LOGIN = new ClientIntent();
    public static final /* enum */ ClientIntent TRANSFER = new ClientIntent();
    private static final int STATUS_ID = 1;
    private static final int LOGIN_ID = 2;
    private static final int TRANSFER_ID = 3;
    private static final /* synthetic */ ClientIntent[] $VALUES;

    public static ClientIntent[] values() {
        return (ClientIntent[])$VALUES.clone();
    }

    public static ClientIntent valueOf(String $$0) {
        return Enum.valueOf(ClientIntent.class, $$0);
    }

    public static ClientIntent byId(int $$0) {
        return switch ($$0) {
            case 1 -> STATUS;
            case 2 -> LOGIN;
            case 3 -> TRANSFER;
            default -> throw new IllegalArgumentException("Unknown connection intent: " + $$0);
        };
    }

    public int id() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
        };
    }

    private static /* synthetic */ ClientIntent[] b() {
        return new ClientIntent[]{STATUS, LOGIN, TRANSFER};
    }

    static {
        $VALUES = ClientIntent.b();
    }
}

