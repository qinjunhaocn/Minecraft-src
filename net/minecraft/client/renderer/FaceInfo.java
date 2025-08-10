/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import net.minecraft.Util;
import net.minecraft.core.Direction;

public final class FaceInfo
extends Enum<FaceInfo> {
    public static final /* enum */ FaceInfo DOWN = new FaceInfo(new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z));
    public static final /* enum */ FaceInfo UP = new FaceInfo(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z));
    public static final /* enum */ FaceInfo NORTH = new FaceInfo(new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z));
    public static final /* enum */ FaceInfo SOUTH = new FaceInfo(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z));
    public static final /* enum */ FaceInfo WEST = new FaceInfo(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z));
    public static final /* enum */ FaceInfo EAST = new FaceInfo(new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z));
    private static final FaceInfo[] BY_FACING;
    private final VertexInfo[] infos;
    private static final /* synthetic */ FaceInfo[] $VALUES;

    public static FaceInfo[] values() {
        return (FaceInfo[])$VALUES.clone();
    }

    public static FaceInfo valueOf(String $$0) {
        return Enum.valueOf(FaceInfo.class, $$0);
    }

    public static FaceInfo fromFacing(Direction $$0) {
        return BY_FACING[$$0.get3DDataValue()];
    }

    private FaceInfo(VertexInfo ... $$0) {
        this.infos = $$0;
    }

    public VertexInfo getVertexInfo(int $$0) {
        return this.infos[$$0];
    }

    private static /* synthetic */ FaceInfo[] a() {
        return new FaceInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        $VALUES = FaceInfo.a();
        BY_FACING = Util.make(new FaceInfo[6], $$0 -> {
            $$0[Constants.MIN_Y] = DOWN;
            $$0[Constants.MAX_Y] = UP;
            $$0[Constants.MIN_Z] = NORTH;
            $$0[Constants.MAX_Z] = SOUTH;
            $$0[Constants.MIN_X] = WEST;
            $$0[Constants.MAX_X] = EAST;
        });
    }

    public static class VertexInfo {
        public final int xFace;
        public final int yFace;
        public final int zFace;

        VertexInfo(int $$0, int $$1, int $$2) {
            this.xFace = $$0;
            this.yFace = $$1;
            this.zFace = $$2;
        }
    }

    public static final class Constants {
        public static final int MAX_Z = Direction.SOUTH.get3DDataValue();
        public static final int MAX_Y = Direction.UP.get3DDataValue();
        public static final int MAX_X = Direction.EAST.get3DDataValue();
        public static final int MIN_Z = Direction.NORTH.get3DDataValue();
        public static final int MIN_Y = Direction.DOWN.get3DDataValue();
        public static final int MIN_X = Direction.WEST.get3DDataValue();
    }
}

