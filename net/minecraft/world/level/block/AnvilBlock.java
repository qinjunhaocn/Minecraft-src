/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnvilBlock
extends FallingBlock {
    public static final MapCodec<AnvilBlock> CODEC = AnvilBlock.simpleCodec(AnvilBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Shapes.a(Block.column(12.0, 0.0, 4.0), Block.column(8.0, 10.0, 4.0, 5.0), Block.column(4.0, 8.0, 5.0, 10.0), Block.column(10.0, 16.0, 10.0, 16.0)));
    private static final Component CONTAINER_TITLE = Component.translatable("container.repair");
    private static final float FALL_DAMAGE_PER_DISTANCE = 2.0f;
    private static final int FALL_DAMAGE_MAX = 40;

    public MapCodec<AnvilBlock> codec() {
        return CODEC;
    }

    public AnvilBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getClockWise());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if (!$$1.isClientSide) {
            $$3.openMenu($$0.getMenuProvider($$1, $$2));
            $$3.awardStat(Stats.INTERACT_WITH_ANVIL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$22) {
        return new SimpleMenuProvider(($$2, $$3, $$4) -> new AnvilMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)), CONTAINER_TITLE);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(FACING).getAxis());
    }

    @Override
    protected void falling(FallingBlockEntity $$0) {
        $$0.setHurtsEntities(2.0f, 40);
    }

    @Override
    public void onLand(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3, FallingBlockEntity $$4) {
        if (!$$4.isSilent()) {
            $$0.levelEvent(1031, $$1, 0);
        }
    }

    @Override
    public void onBrokenAfterFall(Level $$0, BlockPos $$1, FallingBlockEntity $$2) {
        if (!$$2.isSilent()) {
            $$0.levelEvent(1029, $$1, 0);
        }
    }

    @Override
    public DamageSource getFallDamageSource(Entity $$0) {
        return $$0.damageSources().anvil($$0);
    }

    @Nullable
    public static BlockState damage(BlockState $$0) {
        if ($$0.is(Blocks.ANVIL)) {
            return (BlockState)Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, $$0.getValue(FACING));
        }
        if ($$0.is(Blocks.CHIPPED_ANVIL)) {
            return (BlockState)Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, $$0.getValue(FACING));
        }
        return null;
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    @Override
    public int getDustColor(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getMapColor((BlockGetter)$$1, (BlockPos)$$2).col;
    }
}

