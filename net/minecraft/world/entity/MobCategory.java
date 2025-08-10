/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class MobCategory
extends Enum<MobCategory>
implements StringRepresentable {
    public static final /* enum */ MobCategory MONSTER = new MobCategory("monster", 70, false, false, 128);
    public static final /* enum */ MobCategory CREATURE = new MobCategory("creature", 10, true, true, 128);
    public static final /* enum */ MobCategory AMBIENT = new MobCategory("ambient", 15, true, false, 128);
    public static final /* enum */ MobCategory AXOLOTLS = new MobCategory("axolotls", 5, true, false, 128);
    public static final /* enum */ MobCategory UNDERGROUND_WATER_CREATURE = new MobCategory("underground_water_creature", 5, true, false, 128);
    public static final /* enum */ MobCategory WATER_CREATURE = new MobCategory("water_creature", 5, true, false, 128);
    public static final /* enum */ MobCategory WATER_AMBIENT = new MobCategory("water_ambient", 20, true, false, 64);
    public static final /* enum */ MobCategory MISC = new MobCategory("misc", -1, true, true, 128);
    public static final Codec<MobCategory> CODEC;
    private final int max;
    private final boolean isFriendly;
    private final boolean isPersistent;
    private final String name;
    private final int noDespawnDistance = 32;
    private final int despawnDistance;
    private static final /* synthetic */ MobCategory[] $VALUES;

    public static MobCategory[] values() {
        return (MobCategory[])$VALUES.clone();
    }

    public static MobCategory valueOf(String $$0) {
        return Enum.valueOf(MobCategory.class, $$0);
    }

    private MobCategory(String $$0, int $$1, boolean $$2, boolean $$3, int $$4) {
        this.name = $$0;
        this.max = $$1;
        this.isFriendly = $$2;
        this.isPersistent = $$3;
        this.despawnDistance = $$4;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getMaxInstancesPerChunk() {
        return this.max;
    }

    public boolean isFriendly() {
        return this.isFriendly;
    }

    public boolean isPersistent() {
        return this.isPersistent;
    }

    public int getDespawnDistance() {
        return this.despawnDistance;
    }

    public int getNoDespawnDistance() {
        return 32;
    }

    private static /* synthetic */ MobCategory[] h() {
        return new MobCategory[]{MONSTER, CREATURE, AMBIENT, AXOLOTLS, UNDERGROUND_WATER_CREATURE, WATER_CREATURE, WATER_AMBIENT, MISC};
    }

    static {
        $VALUES = MobCategory.h();
        CODEC = StringRepresentable.fromEnum(MobCategory::values);
    }
}

