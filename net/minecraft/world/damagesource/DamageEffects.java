/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;

public final class DamageEffects
extends Enum<DamageEffects>
implements StringRepresentable {
    public static final /* enum */ DamageEffects HURT = new DamageEffects("hurt", SoundEvents.PLAYER_HURT);
    public static final /* enum */ DamageEffects THORNS = new DamageEffects("thorns", SoundEvents.PLAYER_HURT);
    public static final /* enum */ DamageEffects DROWNING = new DamageEffects("drowning", SoundEvents.PLAYER_HURT_DROWN);
    public static final /* enum */ DamageEffects BURNING = new DamageEffects("burning", SoundEvents.PLAYER_HURT_ON_FIRE);
    public static final /* enum */ DamageEffects POKING = new DamageEffects("poking", SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH);
    public static final /* enum */ DamageEffects FREEZING = new DamageEffects("freezing", SoundEvents.PLAYER_HURT_FREEZE);
    public static final Codec<DamageEffects> CODEC;
    private final String id;
    private final SoundEvent sound;
    private static final /* synthetic */ DamageEffects[] $VALUES;

    public static DamageEffects[] values() {
        return (DamageEffects[])$VALUES.clone();
    }

    public static DamageEffects valueOf(String $$0) {
        return Enum.valueOf(DamageEffects.class, $$0);
    }

    private DamageEffects(String $$0, SoundEvent $$1) {
        this.id = $$0;
        this.sound = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public SoundEvent sound() {
        return this.sound;
    }

    private static /* synthetic */ DamageEffects[] b() {
        return new DamageEffects[]{HURT, THORNS, DROWNING, BURNING, POKING, FREEZING};
    }

    static {
        $VALUES = DamageEffects.b();
        CODEC = StringRepresentable.fromEnum(DamageEffects::values);
    }
}

