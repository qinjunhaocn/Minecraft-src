/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class AttachFace
extends Enum<AttachFace>
implements StringRepresentable {
    public static final /* enum */ AttachFace FLOOR = new AttachFace("floor");
    public static final /* enum */ AttachFace WALL = new AttachFace("wall");
    public static final /* enum */ AttachFace CEILING = new AttachFace("ceiling");
    private final String name;
    private static final /* synthetic */ AttachFace[] $VALUES;

    public static AttachFace[] values() {
        return (AttachFace[])$VALUES.clone();
    }

    public static AttachFace valueOf(String $$0) {
        return Enum.valueOf(AttachFace.class, $$0);
    }

    private AttachFace(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ AttachFace[] a() {
        return new AttachFace[]{FLOOR, WALL, CEILING};
    }

    static {
        $VALUES = AttachFace.a();
    }
}

