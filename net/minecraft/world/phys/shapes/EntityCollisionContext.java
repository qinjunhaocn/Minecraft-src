/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.phys.shapes;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityCollisionContext
implements CollisionContext {
    protected static final CollisionContext EMPTY = new EntityCollisionContext(false, false, -1.7976931348623157E308, ItemStack.EMPTY, $$0 -> false, null){

        @Override
        public boolean isAbove(VoxelShape $$0, BlockPos $$1, boolean $$2) {
            return $$2;
        }
    };
    private final boolean descending;
    private final double entityBottom;
    private final boolean placement;
    private final ItemStack heldItem;
    private final Predicate<FluidState> canStandOnFluid;
    @Nullable
    private final Entity entity;

    protected EntityCollisionContext(boolean $$0, boolean $$1, double $$2, ItemStack $$3, Predicate<FluidState> $$4, @Nullable Entity $$5) {
        this.descending = $$0;
        this.placement = $$1;
        this.entityBottom = $$2;
        this.heldItem = $$3;
        this.canStandOnFluid = $$4;
        this.entity = $$5;
    }

    @Deprecated
    protected EntityCollisionContext(Entity $$02, boolean $$12, boolean $$2) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        boolean bl = $$02.isDescending();
        double d = $$02.getY();
        if ($$02 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$02;
            itemStack = $$3.getMainHandItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if ($$12) {
            predicate = $$0 -> true;
        } else if ($$02 instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)$$02;
            predicate = $$1 -> $$4.canStandOnFluid((FluidState)$$1);
        } else {
            predicate = $$0 -> false;
        }
        this(bl, $$2, d, itemStack, predicate, $$02);
    }

    @Override
    public boolean isHoldingItem(Item $$0) {
        return this.heldItem.is($$0);
    }

    @Override
    public boolean canStandOnFluid(FluidState $$0, FluidState $$1) {
        return this.canStandOnFluid.test($$1) && !$$0.getType().isSame($$1.getType());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, CollisionGetter $$1, BlockPos $$2) {
        return $$0.getCollisionShape($$1, $$2, this);
    }

    @Override
    public boolean isDescending() {
        return this.descending;
    }

    @Override
    public boolean isAbove(VoxelShape $$0, BlockPos $$1, boolean $$2) {
        return this.entityBottom > (double)$$1.getY() + $$0.max(Direction.Axis.Y) - (double)1.0E-5f;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean isPlacement() {
        return this.placement;
    }
}

