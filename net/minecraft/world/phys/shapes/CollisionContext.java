/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.world.phys.shapes;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.MinecartCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionContext {
    public static CollisionContext empty() {
        return EntityCollisionContext.EMPTY;
    }

    public static CollisionContext of(Entity $$0) {
        Entity entity = $$0;
        Objects.requireNonNull(entity);
        Entity entity2 = entity;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractMinecart.class}, (Object)entity2, (int)n)) {
            case 0 -> {
                AbstractMinecart $$1 = (AbstractMinecart)entity2;
                if (AbstractMinecart.useExperimentalMovement($$1.level())) {
                    yield new MinecartCollisionContext($$1, false);
                }
                yield new EntityCollisionContext($$0, false, false);
            }
            default -> new EntityCollisionContext($$0, false, false);
        };
    }

    public static CollisionContext of(Entity $$0, boolean $$1) {
        return new EntityCollisionContext($$0, $$1, false);
    }

    public static CollisionContext placementContext(@Nullable Player $$02) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        boolean bl = $$02 != null ? $$02.isDescending() : false;
        double d = $$02 != null ? $$02.getY() : -1.7976931348623157E308;
        if ($$02 instanceof LivingEntity) {
            Player $$12 = $$02;
            itemStack = $$12.getMainHandItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if ($$02 instanceof LivingEntity) {
            Player $$2 = $$02;
            predicate = $$1 -> $$2.canStandOnFluid((FluidState)$$1);
        } else {
            predicate = $$0 -> false;
        }
        return new EntityCollisionContext(bl, true, d, itemStack, predicate, $$02);
    }

    public static CollisionContext withPosition(@Nullable Entity $$02, double $$12) {
        Predicate<FluidState> predicate;
        ItemStack itemStack;
        boolean bl = $$02 != null ? $$02.isDescending() : false;
        double d = $$02 != null ? $$12 : -1.7976931348623157E308;
        if ($$02 instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)$$02;
            itemStack = $$2.getMainHandItem();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if ($$02 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$02;
            predicate = $$1 -> $$3.canStandOnFluid((FluidState)$$1);
        } else {
            predicate = $$0 -> false;
        }
        return new EntityCollisionContext(bl, true, d, itemStack, predicate, $$02);
    }

    public boolean isDescending();

    public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

    public boolean isHoldingItem(Item var1);

    public boolean canStandOnFluid(FluidState var1, FluidState var2);

    public VoxelShape getCollisionShape(BlockState var1, CollisionGetter var2, BlockPos var3);

    default public boolean isPlacement() {
        return false;
    }
}

