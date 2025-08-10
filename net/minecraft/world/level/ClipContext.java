/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClipContext {
    private final Vec3 from;
    private final Vec3 to;
    private final Block block;
    private final Fluid fluid;
    private final CollisionContext collisionContext;

    public ClipContext(Vec3 $$0, Vec3 $$1, Block $$2, Fluid $$3, Entity $$4) {
        this($$0, $$1, $$2, $$3, CollisionContext.of($$4));
    }

    public ClipContext(Vec3 $$0, Vec3 $$1, Block $$2, Fluid $$3, CollisionContext $$4) {
        this.from = $$0;
        this.to = $$1;
        this.block = $$2;
        this.fluid = $$3;
        this.collisionContext = $$4;
    }

    public Vec3 getTo() {
        return this.to;
    }

    public Vec3 getFrom() {
        return this.from;
    }

    public VoxelShape getBlockShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.block.get($$0, $$1, $$2, this.collisionContext);
    }

    public VoxelShape getFluidShape(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.fluid.canPick($$0) ? $$0.getShape($$1, $$2) : Shapes.empty();
    }

    public static final class Block
    extends Enum<Block>
    implements ShapeGetter {
        public static final /* enum */ Block COLLIDER = new Block(BlockBehaviour.BlockStateBase::getCollisionShape);
        public static final /* enum */ Block OUTLINE = new Block(BlockBehaviour.BlockStateBase::getShape);
        public static final /* enum */ Block VISUAL = new Block(BlockBehaviour.BlockStateBase::getVisualShape);
        public static final /* enum */ Block FALLDAMAGE_RESETTING = new Block(($$0, $$1, $$2, $$3) -> {
            if ($$0.is(BlockTags.FALL_DAMAGE_RESETTING)) {
                return Shapes.block();
            }
            return Shapes.empty();
        });
        private final ShapeGetter shapeGetter;
        private static final /* synthetic */ Block[] $VALUES;

        public static Block[] values() {
            return (Block[])$VALUES.clone();
        }

        public static Block valueOf(String $$0) {
            return Enum.valueOf(Block.class, $$0);
        }

        private Block(ShapeGetter $$0) {
            this.shapeGetter = $$0;
        }

        @Override
        public VoxelShape get(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
            return this.shapeGetter.get($$0, $$1, $$2, $$3);
        }

        private static /* synthetic */ Block[] a() {
            return new Block[]{COLLIDER, OUTLINE, VISUAL, FALLDAMAGE_RESETTING};
        }

        static {
            $VALUES = Block.a();
        }
    }

    public static final class Fluid
    extends Enum<Fluid> {
        public static final /* enum */ Fluid NONE = new Fluid($$0 -> false);
        public static final /* enum */ Fluid SOURCE_ONLY = new Fluid(FluidState::isSource);
        public static final /* enum */ Fluid ANY = new Fluid($$0 -> !$$0.isEmpty());
        public static final /* enum */ Fluid WATER = new Fluid($$0 -> $$0.is(FluidTags.WATER));
        private final Predicate<FluidState> canPick;
        private static final /* synthetic */ Fluid[] $VALUES;

        public static Fluid[] values() {
            return (Fluid[])$VALUES.clone();
        }

        public static Fluid valueOf(String $$0) {
            return Enum.valueOf(Fluid.class, $$0);
        }

        private Fluid(Predicate<FluidState> $$0) {
            this.canPick = $$0;
        }

        public boolean canPick(FluidState $$0) {
            return this.canPick.test($$0);
        }

        private static /* synthetic */ Fluid[] a() {
            return new Fluid[]{NONE, SOURCE_ONLY, ANY, WATER};
        }

        static {
            $VALUES = Fluid.a();
        }
    }

    public static interface ShapeGetter {
        public VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4);
    }
}

