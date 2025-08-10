/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class BlockModelRotation
extends Enum<BlockModelRotation>
implements ModelState {
    public static final /* enum */ BlockModelRotation X0_Y0 = new BlockModelRotation(Quadrant.R0, Quadrant.R0);
    public static final /* enum */ BlockModelRotation X0_Y90 = new BlockModelRotation(Quadrant.R0, Quadrant.R90);
    public static final /* enum */ BlockModelRotation X0_Y180 = new BlockModelRotation(Quadrant.R0, Quadrant.R180);
    public static final /* enum */ BlockModelRotation X0_Y270 = new BlockModelRotation(Quadrant.R0, Quadrant.R270);
    public static final /* enum */ BlockModelRotation X90_Y0 = new BlockModelRotation(Quadrant.R90, Quadrant.R0);
    public static final /* enum */ BlockModelRotation X90_Y90 = new BlockModelRotation(Quadrant.R90, Quadrant.R90);
    public static final /* enum */ BlockModelRotation X90_Y180 = new BlockModelRotation(Quadrant.R90, Quadrant.R180);
    public static final /* enum */ BlockModelRotation X90_Y270 = new BlockModelRotation(Quadrant.R90, Quadrant.R270);
    public static final /* enum */ BlockModelRotation X180_Y0 = new BlockModelRotation(Quadrant.R180, Quadrant.R0);
    public static final /* enum */ BlockModelRotation X180_Y90 = new BlockModelRotation(Quadrant.R180, Quadrant.R90);
    public static final /* enum */ BlockModelRotation X180_Y180 = new BlockModelRotation(Quadrant.R180, Quadrant.R180);
    public static final /* enum */ BlockModelRotation X180_Y270 = new BlockModelRotation(Quadrant.R180, Quadrant.R270);
    public static final /* enum */ BlockModelRotation X270_Y0 = new BlockModelRotation(Quadrant.R270, Quadrant.R0);
    public static final /* enum */ BlockModelRotation X270_Y90 = new BlockModelRotation(Quadrant.R270, Quadrant.R90);
    public static final /* enum */ BlockModelRotation X270_Y180 = new BlockModelRotation(Quadrant.R270, Quadrant.R180);
    public static final /* enum */ BlockModelRotation X270_Y270 = new BlockModelRotation(Quadrant.R270, Quadrant.R270);
    private static final BlockModelRotation[][] XY_TABLE;
    private final Quadrant xRotation;
    private final Quadrant yRotation;
    final Transformation transformation;
    private final OctahedralGroup actualRotation;
    final Map<Direction, Matrix4fc> faceMapping = new EnumMap<Direction, Matrix4fc>(Direction.class);
    final Map<Direction, Matrix4fc> inverseFaceMapping = new EnumMap<Direction, Matrix4fc>(Direction.class);
    private final WithUvLock withUvLock = new WithUvLock(this);
    private static final /* synthetic */ BlockModelRotation[] $VALUES;

    public static BlockModelRotation[] values() {
        return (BlockModelRotation[])$VALUES.clone();
    }

    public static BlockModelRotation valueOf(String $$0) {
        return Enum.valueOf(BlockModelRotation.class, $$0);
    }

    private BlockModelRotation(Quadrant $$0, Quadrant $$1) {
        this.xRotation = $$0;
        this.yRotation = $$1;
        this.actualRotation = OctahedralGroup.fromXYAngles($$0, $$1);
        this.transformation = this.actualRotation != OctahedralGroup.IDENTITY ? new Transformation((Matrix4fc)new Matrix4f(this.actualRotation.transformation())) : Transformation.identity();
        for (Direction $$2 : Direction.values()) {
            Matrix4fc $$3 = BlockMath.getFaceTransformation(this.transformation, $$2).getMatrix();
            this.faceMapping.put($$2, $$3);
            this.inverseFaceMapping.put($$2, (Matrix4fc)$$3.invertAffine(new Matrix4f()));
        }
    }

    @Override
    public Transformation transformation() {
        return this.transformation;
    }

    public static BlockModelRotation by(Quadrant $$0, Quadrant $$1) {
        return XY_TABLE[$$0.ordinal()][$$1.ordinal()];
    }

    public OctahedralGroup actualRotation() {
        return this.actualRotation;
    }

    public ModelState withUvLock() {
        return this.withUvLock;
    }

    private static /* synthetic */ BlockModelRotation[] d() {
        return new BlockModelRotation[]{X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270};
    }

    static {
        $VALUES = BlockModelRotation.d();
        XY_TABLE = Util.make(new BlockModelRotation[Quadrant.values().length][Quadrant.values().length], $$0 -> {
            BlockModelRotation[] blockModelRotationArray = BlockModelRotation.values();
            int n = blockModelRotationArray.length;
            for (int i = 0; i < n; ++i) {
                BlockModelRotation $$1;
                $$0[$$1.xRotation.ordinal()][$$1.yRotation.ordinal()] = $$1 = blockModelRotationArray[i];
            }
        });
    }

    record WithUvLock(BlockModelRotation parent) implements ModelState
    {
        @Override
        public Transformation transformation() {
            return this.parent.transformation;
        }

        @Override
        public Matrix4fc faceTransformation(Direction $$0) {
            return this.parent.faceMapping.getOrDefault($$0, NO_TRANSFORM);
        }

        @Override
        public Matrix4fc inverseFaceTransformation(Direction $$0) {
            return this.parent.inverseFaceMapping.getOrDefault($$0, NO_TRANSFORM);
        }
    }
}

