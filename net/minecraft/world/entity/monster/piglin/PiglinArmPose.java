/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.monster.piglin;

public final class PiglinArmPose
extends Enum<PiglinArmPose> {
    public static final /* enum */ PiglinArmPose ATTACKING_WITH_MELEE_WEAPON = new PiglinArmPose();
    public static final /* enum */ PiglinArmPose CROSSBOW_HOLD = new PiglinArmPose();
    public static final /* enum */ PiglinArmPose CROSSBOW_CHARGE = new PiglinArmPose();
    public static final /* enum */ PiglinArmPose ADMIRING_ITEM = new PiglinArmPose();
    public static final /* enum */ PiglinArmPose DANCING = new PiglinArmPose();
    public static final /* enum */ PiglinArmPose DEFAULT = new PiglinArmPose();
    private static final /* synthetic */ PiglinArmPose[] $VALUES;

    public static PiglinArmPose[] values() {
        return (PiglinArmPose[])$VALUES.clone();
    }

    public static PiglinArmPose valueOf(String $$0) {
        return Enum.valueOf(PiglinArmPose.class, $$0);
    }

    private static /* synthetic */ PiglinArmPose[] a() {
        return new PiglinArmPose[]{ATTACKING_WITH_MELEE_WEAPON, CROSSBOW_HOLD, CROSSBOW_CHARGE, ADMIRING_ITEM, DANCING, DEFAULT};
    }

    static {
        $VALUES = PiglinArmPose.a();
    }
}

