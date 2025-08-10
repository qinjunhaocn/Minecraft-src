/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract sealed class SupportType
extends Enum<SupportType> {
    public static final /* enum */ SupportType FULL = new SupportType(){

        @Override
        public boolean isSupporting(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
            return Block.isFaceFull($$0.getBlockSupportShape($$1, $$2), $$3);
        }
    };
    public static final /* enum */ SupportType CENTER = new SupportType(){
        private final VoxelShape CENTER_SUPPORT_SHAPE = Block.column(2.0, 0.0, 10.0);

        @Override
        public boolean isSupporting(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
            return !Shapes.joinIsNotEmpty($$0.getBlockSupportShape($$1, $$2).getFaceShape($$3), this.CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
        }
    };
    public static final /* enum */ SupportType RIGID = new SupportType(){
        private final VoxelShape RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), Block.column(12.0, 0.0, 16.0), BooleanOp.ONLY_FIRST);

        @Override
        public boolean isSupporting(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
            return !Shapes.joinIsNotEmpty($$0.getBlockSupportShape($$1, $$2).getFaceShape($$3), this.RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
        }
    };
    private static final /* synthetic */ SupportType[] $VALUES;

    public static SupportType[] values() {
        return (SupportType[])$VALUES.clone();
    }

    public static SupportType valueOf(String $$0) {
        return Enum.valueOf(SupportType.class, $$0);
    }

    public abstract boolean isSupporting(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

    private static /* synthetic */ SupportType[] a() {
        return new SupportType[]{FULL, CENTER, RIGID};
    }

    static {
        $VALUES = SupportType.a();
    }
}

