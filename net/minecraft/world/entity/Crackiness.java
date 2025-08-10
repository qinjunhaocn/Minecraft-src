/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import net.minecraft.world.item.ItemStack;

public class Crackiness {
    public static final Crackiness GOLEM = new Crackiness(0.75f, 0.5f, 0.25f);
    public static final Crackiness WOLF_ARMOR = new Crackiness(0.95f, 0.69f, 0.32f);
    private final float fractionLow;
    private final float fractionMedium;
    private final float fractionHigh;

    private Crackiness(float $$0, float $$1, float $$2) {
        this.fractionLow = $$0;
        this.fractionMedium = $$1;
        this.fractionHigh = $$2;
    }

    public Level byFraction(float $$0) {
        if ($$0 < this.fractionHigh) {
            return Level.HIGH;
        }
        if ($$0 < this.fractionMedium) {
            return Level.MEDIUM;
        }
        if ($$0 < this.fractionLow) {
            return Level.LOW;
        }
        return Level.NONE;
    }

    public Level byDamage(ItemStack $$0) {
        if (!$$0.isDamageableItem()) {
            return Level.NONE;
        }
        return this.byDamage($$0.getDamageValue(), $$0.getMaxDamage());
    }

    public Level byDamage(int $$0, int $$1) {
        return this.byFraction((float)($$1 - $$0) / (float)$$1);
    }

    public static final class Level
    extends Enum<Level> {
        public static final /* enum */ Level NONE = new Level();
        public static final /* enum */ Level LOW = new Level();
        public static final /* enum */ Level MEDIUM = new Level();
        public static final /* enum */ Level HIGH = new Level();
        private static final /* synthetic */ Level[] $VALUES;

        public static Level[] values() {
            return (Level[])$VALUES.clone();
        }

        public static Level valueOf(String $$0) {
            return Enum.valueOf(Level.class, $$0);
        }

        private static /* synthetic */ Level[] a() {
            return new Level[]{NONE, LOW, MEDIUM, HIGH};
        }

        static {
            $VALUES = Level.a();
        }
    }
}

