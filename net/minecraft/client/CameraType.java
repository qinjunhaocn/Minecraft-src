/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

public final class CameraType
extends Enum<CameraType> {
    public static final /* enum */ CameraType FIRST_PERSON = new CameraType(true, false);
    public static final /* enum */ CameraType THIRD_PERSON_BACK = new CameraType(false, false);
    public static final /* enum */ CameraType THIRD_PERSON_FRONT = new CameraType(false, true);
    private static final CameraType[] VALUES;
    private final boolean firstPerson;
    private final boolean mirrored;
    private static final /* synthetic */ CameraType[] $VALUES;

    public static CameraType[] values() {
        return (CameraType[])$VALUES.clone();
    }

    public static CameraType valueOf(String $$0) {
        return Enum.valueOf(CameraType.class, $$0);
    }

    private CameraType(boolean $$0, boolean $$1) {
        this.firstPerson = $$0;
        this.mirrored = $$1;
    }

    public boolean isFirstPerson() {
        return this.firstPerson;
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    public CameraType cycle() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    private static /* synthetic */ CameraType[] d() {
        return new CameraType[]{FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT};
    }

    static {
        $VALUES = CameraType.d();
        VALUES = CameraType.values();
    }
}

