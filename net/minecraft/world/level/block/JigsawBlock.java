/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock
extends Block
implements EntityBlock,
GameMasterBlock {
    public static final MapCodec<JigsawBlock> CODEC = JigsawBlock.simpleCodec(JigsawBlock::new);
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public MapCodec<JigsawBlock> codec() {
        return CODEC;
    }

    protected JigsawBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(ORIENTATION);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ORIENTATION, $$1.rotation().rotate($$0.getValue(ORIENTATION)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ORIENTATION, $$1.rotation().rotate($$0.getValue(ORIENTATION)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$3;
        Direction $$1 = $$0.getClickedFace();
        if ($$1.getAxis() == Direction.Axis.Y) {
            Direction $$2 = $$0.getHorizontalDirection().getOpposite();
        } else {
            $$3 = Direction.UP;
        }
        return (BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop($$1, $$3));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new JigsawBlockEntity($$0, $$1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof JigsawBlockEntity && $$3.canUseGameMasterBlocks()) {
            $$3.openJigsawBlock((JigsawBlockEntity)$$5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static boolean canAttach(StructureTemplate.JigsawBlockInfo $$0, StructureTemplate.JigsawBlockInfo $$1) {
        Direction $$2 = JigsawBlock.getFrontFacing($$0.info().state());
        Direction $$3 = JigsawBlock.getFrontFacing($$1.info().state());
        Direction $$4 = JigsawBlock.getTopFacing($$0.info().state());
        Direction $$5 = JigsawBlock.getTopFacing($$1.info().state());
        JigsawBlockEntity.JointType $$6 = $$0.jointType();
        boolean $$7 = $$6 == JigsawBlockEntity.JointType.ROLLABLE;
        return $$2 == $$3.getOpposite() && ($$7 || $$4 == $$5) && $$0.target().equals($$1.name());
    }

    public static Direction getFrontFacing(BlockState $$0) {
        return $$0.getValue(ORIENTATION).front();
    }

    public static Direction getTopFacing(BlockState $$0) {
        return $$0.getValue(ORIENTATION).top();
    }
}

