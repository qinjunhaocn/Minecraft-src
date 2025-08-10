/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.booleans.BooleanArrayList
 *  it.unimi.dsi.fastutil.booleans.BooleanList
 *  java.lang.MatchException
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 */
package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quadrant;
import com.mojang.math.SymmetricGroup3;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public final class OctahedralGroup
extends Enum<OctahedralGroup>
implements StringRepresentable {
    public static final /* enum */ OctahedralGroup IDENTITY = new OctahedralGroup("identity", SymmetricGroup3.P123, false, false, false);
    public static final /* enum */ OctahedralGroup ROT_180_FACE_XY = new OctahedralGroup("rot_180_face_xy", SymmetricGroup3.P123, true, true, false);
    public static final /* enum */ OctahedralGroup ROT_180_FACE_XZ = new OctahedralGroup("rot_180_face_xz", SymmetricGroup3.P123, true, false, true);
    public static final /* enum */ OctahedralGroup ROT_180_FACE_YZ = new OctahedralGroup("rot_180_face_yz", SymmetricGroup3.P123, false, true, true);
    public static final /* enum */ OctahedralGroup ROT_120_NNN = new OctahedralGroup("rot_120_nnn", SymmetricGroup3.P231, false, false, false);
    public static final /* enum */ OctahedralGroup ROT_120_NNP = new OctahedralGroup("rot_120_nnp", SymmetricGroup3.P312, true, false, true);
    public static final /* enum */ OctahedralGroup ROT_120_NPN = new OctahedralGroup("rot_120_npn", SymmetricGroup3.P312, false, true, true);
    public static final /* enum */ OctahedralGroup ROT_120_NPP = new OctahedralGroup("rot_120_npp", SymmetricGroup3.P231, true, false, true);
    public static final /* enum */ OctahedralGroup ROT_120_PNN = new OctahedralGroup("rot_120_pnn", SymmetricGroup3.P312, true, true, false);
    public static final /* enum */ OctahedralGroup ROT_120_PNP = new OctahedralGroup("rot_120_pnp", SymmetricGroup3.P231, true, true, false);
    public static final /* enum */ OctahedralGroup ROT_120_PPN = new OctahedralGroup("rot_120_ppn", SymmetricGroup3.P231, false, true, true);
    public static final /* enum */ OctahedralGroup ROT_120_PPP = new OctahedralGroup("rot_120_ppp", SymmetricGroup3.P312, false, false, false);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_XY_NEG = new OctahedralGroup("rot_180_edge_xy_neg", SymmetricGroup3.P213, true, true, true);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_XY_POS = new OctahedralGroup("rot_180_edge_xy_pos", SymmetricGroup3.P213, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_XZ_NEG = new OctahedralGroup("rot_180_edge_xz_neg", SymmetricGroup3.P321, true, true, true);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_XZ_POS = new OctahedralGroup("rot_180_edge_xz_pos", SymmetricGroup3.P321, false, true, false);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_YZ_NEG = new OctahedralGroup("rot_180_edge_yz_neg", SymmetricGroup3.P132, true, true, true);
    public static final /* enum */ OctahedralGroup ROT_180_EDGE_YZ_POS = new OctahedralGroup("rot_180_edge_yz_pos", SymmetricGroup3.P132, true, false, false);
    public static final /* enum */ OctahedralGroup ROT_90_X_NEG = new OctahedralGroup("rot_90_x_neg", SymmetricGroup3.P132, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_90_X_POS = new OctahedralGroup("rot_90_x_pos", SymmetricGroup3.P132, false, true, false);
    public static final /* enum */ OctahedralGroup ROT_90_Y_NEG = new OctahedralGroup("rot_90_y_neg", SymmetricGroup3.P321, true, false, false);
    public static final /* enum */ OctahedralGroup ROT_90_Y_POS = new OctahedralGroup("rot_90_y_pos", SymmetricGroup3.P321, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_90_Z_NEG = new OctahedralGroup("rot_90_z_neg", SymmetricGroup3.P213, false, true, false);
    public static final /* enum */ OctahedralGroup ROT_90_Z_POS = new OctahedralGroup("rot_90_z_pos", SymmetricGroup3.P213, true, false, false);
    public static final /* enum */ OctahedralGroup INVERSION = new OctahedralGroup("inversion", SymmetricGroup3.P123, true, true, true);
    public static final /* enum */ OctahedralGroup INVERT_X = new OctahedralGroup("invert_x", SymmetricGroup3.P123, true, false, false);
    public static final /* enum */ OctahedralGroup INVERT_Y = new OctahedralGroup("invert_y", SymmetricGroup3.P123, false, true, false);
    public static final /* enum */ OctahedralGroup INVERT_Z = new OctahedralGroup("invert_z", SymmetricGroup3.P123, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_60_REF_NNN = new OctahedralGroup("rot_60_ref_nnn", SymmetricGroup3.P312, true, true, true);
    public static final /* enum */ OctahedralGroup ROT_60_REF_NNP = new OctahedralGroup("rot_60_ref_nnp", SymmetricGroup3.P231, true, false, false);
    public static final /* enum */ OctahedralGroup ROT_60_REF_NPN = new OctahedralGroup("rot_60_ref_npn", SymmetricGroup3.P231, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_60_REF_NPP = new OctahedralGroup("rot_60_ref_npp", SymmetricGroup3.P312, false, false, true);
    public static final /* enum */ OctahedralGroup ROT_60_REF_PNN = new OctahedralGroup("rot_60_ref_pnn", SymmetricGroup3.P231, false, true, false);
    public static final /* enum */ OctahedralGroup ROT_60_REF_PNP = new OctahedralGroup("rot_60_ref_pnp", SymmetricGroup3.P312, true, false, false);
    public static final /* enum */ OctahedralGroup ROT_60_REF_PPN = new OctahedralGroup("rot_60_ref_ppn", SymmetricGroup3.P312, false, true, false);
    public static final /* enum */ OctahedralGroup ROT_60_REF_PPP = new OctahedralGroup("rot_60_ref_ppp", SymmetricGroup3.P231, true, true, true);
    public static final /* enum */ OctahedralGroup SWAP_XY = new OctahedralGroup("swap_xy", SymmetricGroup3.P213, false, false, false);
    public static final /* enum */ OctahedralGroup SWAP_YZ = new OctahedralGroup("swap_yz", SymmetricGroup3.P132, false, false, false);
    public static final /* enum */ OctahedralGroup SWAP_XZ = new OctahedralGroup("swap_xz", SymmetricGroup3.P321, false, false, false);
    public static final /* enum */ OctahedralGroup SWAP_NEG_XY = new OctahedralGroup("swap_neg_xy", SymmetricGroup3.P213, true, true, false);
    public static final /* enum */ OctahedralGroup SWAP_NEG_YZ = new OctahedralGroup("swap_neg_yz", SymmetricGroup3.P132, false, true, true);
    public static final /* enum */ OctahedralGroup SWAP_NEG_XZ = new OctahedralGroup("swap_neg_xz", SymmetricGroup3.P321, true, false, true);
    public static final /* enum */ OctahedralGroup ROT_90_REF_X_NEG = new OctahedralGroup("rot_90_ref_x_neg", SymmetricGroup3.P132, true, false, true);
    public static final /* enum */ OctahedralGroup ROT_90_REF_X_POS = new OctahedralGroup("rot_90_ref_x_pos", SymmetricGroup3.P132, true, true, false);
    public static final /* enum */ OctahedralGroup ROT_90_REF_Y_NEG = new OctahedralGroup("rot_90_ref_y_neg", SymmetricGroup3.P321, true, true, false);
    public static final /* enum */ OctahedralGroup ROT_90_REF_Y_POS = new OctahedralGroup("rot_90_ref_y_pos", SymmetricGroup3.P321, false, true, true);
    public static final /* enum */ OctahedralGroup ROT_90_REF_Z_NEG = new OctahedralGroup("rot_90_ref_z_neg", SymmetricGroup3.P213, false, true, true);
    public static final /* enum */ OctahedralGroup ROT_90_REF_Z_POS = new OctahedralGroup("rot_90_ref_z_pos", SymmetricGroup3.P213, true, false, true);
    private static final Direction.Axis[] AXES;
    private final Matrix3fc transformation;
    private final String name;
    @Nullable
    private Map<Direction, Direction> rotatedDirections;
    private final boolean invertX;
    private final boolean invertY;
    private final boolean invertZ;
    private final SymmetricGroup3 permutation;
    private static final OctahedralGroup[][] CAYLEY_TABLE;
    private static final OctahedralGroup[] INVERSE_TABLE;
    private static final OctahedralGroup[][] XY_TABLE;
    private static final /* synthetic */ OctahedralGroup[] $VALUES;

    public static OctahedralGroup[] values() {
        return (OctahedralGroup[])$VALUES.clone();
    }

    public static OctahedralGroup valueOf(String $$0) {
        return Enum.valueOf(OctahedralGroup.class, $$0);
    }

    private OctahedralGroup(String $$0, SymmetricGroup3 $$1, boolean $$2, boolean $$3, boolean $$4) {
        this.name = $$0;
        this.invertX = $$2;
        this.invertY = $$3;
        this.invertZ = $$4;
        this.permutation = $$1;
        Matrix3f $$5 = new Matrix3f().scaling($$2 ? -1.0f : 1.0f, $$3 ? -1.0f : 1.0f, $$4 ? -1.0f : 1.0f);
        $$5.mul($$1.transformation());
        this.transformation = $$5;
    }

    private BooleanList packInversions() {
        return new BooleanArrayList(new boolean[]{this.invertX, this.invertY, this.invertZ});
    }

    public OctahedralGroup compose(OctahedralGroup $$0) {
        return CAYLEY_TABLE[this.ordinal()][$$0.ordinal()];
    }

    public OctahedralGroup inverse() {
        return INVERSE_TABLE[this.ordinal()];
    }

    public Matrix3fc transformation() {
        return this.transformation;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Direction rotate(Direction $$02) {
        if (this.rotatedDirections == null) {
            this.rotatedDirections = Util.makeEnumMap(Direction.class, $$0 -> {
                Direction.Axis $$1 = $$0.getAxis();
                Direction.AxisDirection $$2 = $$0.getAxisDirection();
                Direction.Axis $$3 = this.permute($$1);
                Direction.AxisDirection $$4 = this.inverts($$3) ? $$2.opposite() : $$2;
                return Direction.fromAxisAndDirection($$3, $$4);
            });
        }
        return this.rotatedDirections.get($$02);
    }

    public boolean inverts(Direction.Axis $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> this.invertX;
            case Direction.Axis.Y -> this.invertY;
            case Direction.Axis.Z -> this.invertZ;
        };
    }

    public Direction.Axis permute(Direction.Axis $$0) {
        return AXES[this.permutation.permutation($$0.ordinal())];
    }

    public FrontAndTop rotate(FrontAndTop $$0) {
        return FrontAndTop.fromFrontAndTop(this.rotate($$0.front()), this.rotate($$0.top()));
    }

    public static OctahedralGroup fromXYAngles(Quadrant $$0, Quadrant $$1) {
        return XY_TABLE[$$0.ordinal()][$$1.ordinal()];
    }

    private static /* synthetic */ OctahedralGroup[] e() {
        return new OctahedralGroup[]{IDENTITY, ROT_180_FACE_XY, ROT_180_FACE_XZ, ROT_180_FACE_YZ, ROT_120_NNN, ROT_120_NNP, ROT_120_NPN, ROT_120_NPP, ROT_120_PNN, ROT_120_PNP, ROT_120_PPN, ROT_120_PPP, ROT_180_EDGE_XY_NEG, ROT_180_EDGE_XY_POS, ROT_180_EDGE_XZ_NEG, ROT_180_EDGE_XZ_POS, ROT_180_EDGE_YZ_NEG, ROT_180_EDGE_YZ_POS, ROT_90_X_NEG, ROT_90_X_POS, ROT_90_Y_NEG, ROT_90_Y_POS, ROT_90_Z_NEG, ROT_90_Z_POS, INVERSION, INVERT_X, INVERT_Y, INVERT_Z, ROT_60_REF_NNN, ROT_60_REF_NNP, ROT_60_REF_NPN, ROT_60_REF_NPP, ROT_60_REF_PNN, ROT_60_REF_PNP, ROT_60_REF_PPN, ROT_60_REF_PPP, SWAP_XY, SWAP_YZ, SWAP_XZ, SWAP_NEG_XY, SWAP_NEG_YZ, SWAP_NEG_XZ, ROT_90_REF_X_NEG, ROT_90_REF_X_POS, ROT_90_REF_Y_NEG, ROT_90_REF_Y_POS, ROT_90_REF_Z_NEG, ROT_90_REF_Z_POS};
    }

    static {
        $VALUES = OctahedralGroup.e();
        AXES = Direction.Axis.values();
        CAYLEY_TABLE = Util.make(new OctahedralGroup[OctahedralGroup.values().length][OctahedralGroup.values().length], $$02 -> {
            Map<Pair, OctahedralGroup> $$1 = Arrays.stream(OctahedralGroup.values()).collect(Collectors.toMap($$0 -> Pair.of((Object)((Object)$$0.permutation), (Object)$$0.packInversions()), $$0 -> $$0));
            for (OctahedralGroup $$2 : OctahedralGroup.values()) {
                for (OctahedralGroup $$3 : OctahedralGroup.values()) {
                    BooleanList $$4 = $$2.packInversions();
                    BooleanList $$5 = $$3.packInversions();
                    SymmetricGroup3 $$6 = $$3.permutation.compose($$2.permutation);
                    BooleanArrayList $$7 = new BooleanArrayList(3);
                    for (int $$8 = 0; $$8 < 3; ++$$8) {
                        $$7.add($$4.getBoolean($$8) ^ $$5.getBoolean($$2.permutation.permutation($$8)));
                    }
                    $$02[$$2.ordinal()][$$3.ordinal()] = $$1.get(Pair.of((Object)((Object)$$6), (Object)$$7));
                }
            }
        });
        INVERSE_TABLE = (OctahedralGroup[])Arrays.stream(OctahedralGroup.values()).map($$0 -> Arrays.stream(OctahedralGroup.values()).filter($$1 -> $$0.compose((OctahedralGroup)$$1) == IDENTITY).findAny().get()).toArray(OctahedralGroup[]::new);
        XY_TABLE = Util.make(new OctahedralGroup[Quadrant.values().length][Quadrant.values().length], $$0 -> {
            for (Quadrant $$1 : Quadrant.values()) {
                for (Quadrant $$2 : Quadrant.values()) {
                    OctahedralGroup $$3 = IDENTITY;
                    for (int $$4 = 0; $$4 < $$2.shift; ++$$4) {
                        $$3 = $$3.compose(ROT_90_Y_NEG);
                    }
                    for (int $$5 = 0; $$5 < $$1.shift; ++$$5) {
                        $$3 = $$3.compose(ROT_90_X_NEG);
                    }
                    $$0[$$1.ordinal()][$$2.ordinal()] = $$3;
                }
            }
        });
    }
}

