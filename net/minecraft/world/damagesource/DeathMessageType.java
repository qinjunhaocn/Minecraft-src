/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class DeathMessageType
extends Enum<DeathMessageType>
implements StringRepresentable {
    public static final /* enum */ DeathMessageType DEFAULT = new DeathMessageType("default");
    public static final /* enum */ DeathMessageType FALL_VARIANTS = new DeathMessageType("fall_variants");
    public static final /* enum */ DeathMessageType INTENTIONAL_GAME_DESIGN = new DeathMessageType("intentional_game_design");
    public static final Codec<DeathMessageType> CODEC;
    private final String id;
    private static final /* synthetic */ DeathMessageType[] $VALUES;

    public static DeathMessageType[] values() {
        return (DeathMessageType[])$VALUES.clone();
    }

    public static DeathMessageType valueOf(String $$0) {
        return Enum.valueOf(DeathMessageType.class, $$0);
    }

    private DeathMessageType(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ DeathMessageType[] a() {
        return new DeathMessageType[]{DEFAULT, FALL_VARIANTS, INTENTIONAL_GAME_DESIGN};
    }

    static {
        $VALUES = DeathMessageType.a();
        CODEC = StringRepresentable.fromEnum(DeathMessageType::values);
    }
}

