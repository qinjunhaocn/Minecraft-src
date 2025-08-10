/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.narration;

public final class NarratedElementType
extends Enum<NarratedElementType> {
    public static final /* enum */ NarratedElementType TITLE = new NarratedElementType();
    public static final /* enum */ NarratedElementType POSITION = new NarratedElementType();
    public static final /* enum */ NarratedElementType HINT = new NarratedElementType();
    public static final /* enum */ NarratedElementType USAGE = new NarratedElementType();
    private static final /* synthetic */ NarratedElementType[] $VALUES;

    public static NarratedElementType[] values() {
        return (NarratedElementType[])$VALUES.clone();
    }

    public static NarratedElementType valueOf(String $$0) {
        return Enum.valueOf(NarratedElementType.class, $$0);
    }

    private static /* synthetic */ NarratedElementType[] a() {
        return new NarratedElementType[]{TITLE, POSITION, HINT, USAGE};
    }

    static {
        $VALUES = NarratedElementType.a();
    }
}

