/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol;

public final class PacketFlow
extends Enum<PacketFlow> {
    public static final /* enum */ PacketFlow SERVERBOUND = new PacketFlow("serverbound");
    public static final /* enum */ PacketFlow CLIENTBOUND = new PacketFlow("clientbound");
    private final String id;
    private static final /* synthetic */ PacketFlow[] $VALUES;

    public static PacketFlow[] values() {
        return (PacketFlow[])$VALUES.clone();
    }

    public static PacketFlow valueOf(String $$0) {
        return Enum.valueOf(PacketFlow.class, $$0);
    }

    private PacketFlow(String $$0) {
        this.id = $$0;
    }

    public PacketFlow getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }

    public String id() {
        return this.id;
    }

    private static /* synthetic */ PacketFlow[] c() {
        return new PacketFlow[]{SERVERBOUND, CLIENTBOUND};
    }

    static {
        $VALUES = PacketFlow.c();
    }
}

