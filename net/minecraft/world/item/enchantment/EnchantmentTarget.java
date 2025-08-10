/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class EnchantmentTarget
extends Enum<EnchantmentTarget>
implements StringRepresentable {
    public static final /* enum */ EnchantmentTarget ATTACKER = new EnchantmentTarget("attacker");
    public static final /* enum */ EnchantmentTarget DAMAGING_ENTITY = new EnchantmentTarget("damaging_entity");
    public static final /* enum */ EnchantmentTarget VICTIM = new EnchantmentTarget("victim");
    public static final Codec<EnchantmentTarget> CODEC;
    private final String id;
    private static final /* synthetic */ EnchantmentTarget[] $VALUES;

    public static EnchantmentTarget[] values() {
        return (EnchantmentTarget[])$VALUES.clone();
    }

    public static EnchantmentTarget valueOf(String $$0) {
        return Enum.valueOf(EnchantmentTarget.class, $$0);
    }

    private EnchantmentTarget(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ EnchantmentTarget[] a() {
        return new EnchantmentTarget[]{ATTACKER, DAMAGING_ENTITY, VICTIM};
    }

    static {
        $VALUES = EnchantmentTarget.a();
        CODEC = StringRepresentable.fromEnum(EnchantmentTarget::values);
    }
}

