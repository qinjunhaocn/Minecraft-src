/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs;

public final class PackType
extends Enum<PackType> {
    public static final /* enum */ PackType CLIENT_RESOURCES = new PackType("assets");
    public static final /* enum */ PackType SERVER_DATA = new PackType("data");
    private final String directory;
    private static final /* synthetic */ PackType[] $VALUES;

    public static PackType[] values() {
        return (PackType[])$VALUES.clone();
    }

    public static PackType valueOf(String $$0) {
        return Enum.valueOf(PackType.class, $$0);
    }

    private PackType(String $$0) {
        this.directory = $$0;
    }

    public String getDirectory() {
        return this.directory;
    }

    private static /* synthetic */ PackType[] b() {
        return new PackType[]{CLIENT_RESOURCES, SERVER_DATA};
    }

    static {
        $VALUES = PackType.b();
    }
}

