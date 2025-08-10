/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.equipment;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

public final class ArmorType
extends Enum<ArmorType>
implements StringRepresentable {
    public static final /* enum */ ArmorType HELMET = new ArmorType(EquipmentSlot.HEAD, 11, "helmet");
    public static final /* enum */ ArmorType CHESTPLATE = new ArmorType(EquipmentSlot.CHEST, 16, "chestplate");
    public static final /* enum */ ArmorType LEGGINGS = new ArmorType(EquipmentSlot.LEGS, 15, "leggings");
    public static final /* enum */ ArmorType BOOTS = new ArmorType(EquipmentSlot.FEET, 13, "boots");
    public static final /* enum */ ArmorType BODY = new ArmorType(EquipmentSlot.BODY, 16, "body");
    public static final Codec<ArmorType> CODEC;
    private final EquipmentSlot slot;
    private final String name;
    private final int unitDurability;
    private static final /* synthetic */ ArmorType[] $VALUES;

    public static ArmorType[] values() {
        return (ArmorType[])$VALUES.clone();
    }

    public static ArmorType valueOf(String $$0) {
        return Enum.valueOf(ArmorType.class, $$0);
    }

    private ArmorType(EquipmentSlot $$0, int $$1, String $$2) {
        this.slot = $$0;
        this.name = $$2;
        this.unitDurability = $$1;
    }

    public int getDurability(int $$0) {
        return this.unitDurability * $$0;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ ArmorType[] d() {
        return new ArmorType[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY};
    }

    static {
        $VALUES = ArmorType.d();
        CODEC = StringRepresentable.fromValues(ArmorType::values);
    }
}

