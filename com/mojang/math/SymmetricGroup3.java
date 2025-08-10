/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 */
package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public final class SymmetricGroup3
extends Enum<SymmetricGroup3> {
    public static final /* enum */ SymmetricGroup3 P123 = new SymmetricGroup3(0, 1, 2);
    public static final /* enum */ SymmetricGroup3 P213 = new SymmetricGroup3(1, 0, 2);
    public static final /* enum */ SymmetricGroup3 P132 = new SymmetricGroup3(0, 2, 1);
    public static final /* enum */ SymmetricGroup3 P231 = new SymmetricGroup3(1, 2, 0);
    public static final /* enum */ SymmetricGroup3 P312 = new SymmetricGroup3(2, 0, 1);
    public static final /* enum */ SymmetricGroup3 P321 = new SymmetricGroup3(2, 1, 0);
    private final int[] permutation;
    private final Matrix3fc transformation;
    private static final int ORDER = 3;
    private static final SymmetricGroup3[][] CAYLEY_TABLE;
    private static final /* synthetic */ SymmetricGroup3[] $VALUES;

    public static SymmetricGroup3[] values() {
        return (SymmetricGroup3[])$VALUES.clone();
    }

    public static SymmetricGroup3 valueOf(String $$0) {
        return Enum.valueOf(SymmetricGroup3.class, $$0);
    }

    private SymmetricGroup3(int $$0, int $$1, int $$2) {
        this.permutation = new int[]{$$0, $$1, $$2};
        Matrix3f $$3 = new Matrix3f().zero();
        $$3.set(this.permutation(0), 0, 1.0f);
        $$3.set(this.permutation(1), 1, 1.0f);
        $$3.set(this.permutation(2), 2, 1.0f);
        this.transformation = $$3;
    }

    public SymmetricGroup3 compose(SymmetricGroup3 $$0) {
        return CAYLEY_TABLE[this.ordinal()][$$0.ordinal()];
    }

    public int permutation(int $$0) {
        return this.permutation[$$0];
    }

    public Matrix3fc transformation() {
        return this.transformation;
    }

    private static /* synthetic */ SymmetricGroup3[] b() {
        return new SymmetricGroup3[]{P123, P213, P132, P231, P312, P321};
    }

    static {
        $VALUES = SymmetricGroup3.b();
        CAYLEY_TABLE = Util.make(new SymmetricGroup3[SymmetricGroup3.values().length][SymmetricGroup3.values().length], $$0 -> {
            for (SymmetricGroup3 $$12 : SymmetricGroup3.values()) {
                for (SymmetricGroup3 $$2 : SymmetricGroup3.values()) {
                    SymmetricGroup3 $$5;
                    int[] $$3 = new int[3];
                    for (int $$4 = 0; $$4 < 3; ++$$4) {
                        $$3[$$4] = $$12.permutation[$$2.permutation[$$4]];
                    }
                    $$0[$$12.ordinal()][$$2.ordinal()] = $$5 = Arrays.stream(SymmetricGroup3.values()).filter($$1 -> Arrays.equals($$1.permutation, $$3)).findFirst().get();
                }
            }
        });
    }
}

