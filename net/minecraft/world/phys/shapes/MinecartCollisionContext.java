/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.phys.shapes;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinecartCollisionContext
extends EntityCollisionContext {
    @Nullable
    private BlockPos ingoreBelow;
    @Nullable
    private BlockPos slopeIgnore;

    protected MinecartCollisionContext(AbstractMinecart $$0, boolean $$1) {
        super($$0, $$1, false);
        this.setupContext($$0);
    }

    private void setupContext(AbstractMinecart $$0) {
        BlockPos $$1 = $$0.getCurrentBlockPosOrRailBelow();
        BlockState $$2 = $$0.level().getBlockState($$1);
        boolean $$3 = BaseRailBlock.isRail($$2);
        if ($$3) {
            this.ingoreBelow = $$1.below();
            RailShape $$4 = $$2.getValue(((BaseRailBlock)$$2.getBlock()).getShapeProperty());
            if ($$4.isSlope()) {
                this.slopeIgnore = switch ($$4) {
                    case RailShape.ASCENDING_EAST -> $$1.east();
                    case RailShape.ASCENDING_WEST -> $$1.west();
                    case RailShape.ASCENDING_NORTH -> $$1.north();
                    case RailShape.ASCENDING_SOUTH -> $$1.south();
                    default -> null;
                };
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, CollisionGetter $$1, BlockPos $$2) {
        if ($$2.equals(this.ingoreBelow) || $$2.equals(this.slopeIgnore)) {
            return Shapes.empty();
        }
        return super.getCollisionShape($$0, $$1, $$2);
    }
}

