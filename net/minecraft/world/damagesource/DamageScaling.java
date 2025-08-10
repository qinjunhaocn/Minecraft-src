/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class DamageScaling
extends Enum<DamageScaling>
implements StringRepresentable {
    public static final /* enum */ DamageScaling NEVER = new DamageScaling("never");
    public static final /* enum */ DamageScaling WHEN_CAUSED_BY_LIVING_NON_PLAYER = new DamageScaling("when_caused_by_living_non_player");
    public static final /* enum */ DamageScaling ALWAYS = new DamageScaling("always");
    public static final Codec<DamageScaling> CODEC;
    private final String id;
    private static final /* synthetic */ DamageScaling[] $VALUES;

    public static DamageScaling[] values() {
        return (DamageScaling[])$VALUES.clone();
    }

    public static DamageScaling valueOf(String $$0) {
        return Enum.valueOf(DamageScaling.class, $$0);
    }

    private DamageScaling(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ DamageScaling[] a() {
        return new DamageScaling[]{NEVER, WHEN_CAUSED_BY_LIVING_NON_PLAYER, ALWAYS};
    }

    static {
        $VALUES = DamageScaling.a();
        CODEC = StringRepresentable.fromEnum(DamageScaling::values);
    }
}

