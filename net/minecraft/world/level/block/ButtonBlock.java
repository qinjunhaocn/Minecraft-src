/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ButtonBlock
extends FaceAttachedHorizontalDirectionalBlock {
    public static final MapCodec<ButtonBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter($$0 -> $$0.type), (App)Codec.intRange((int)1, (int)1024).fieldOf("ticks_to_stay_pressed").forGetter($$0 -> $$0.ticksToStayPressed), ButtonBlock.propertiesCodec()).apply((Applicative)$$02, ButtonBlock::new));
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final BlockSetType type;
    private final int ticksToStayPressed;
    private final Function<BlockState, VoxelShape> shapes;

    public MapCodec<ButtonBlock> codec() {
        return CODEC;
    }

    protected ButtonBlock(BlockSetType $$0, int $$1, BlockBehaviour.Properties $$2) {
        super($$2.sound($$0.soundType()));
        this.type = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
        this.ticksToStayPressed = $$1;
        this.shapes = this.makeShapes();
    }

    private Function<BlockState, VoxelShape> makeShapes() {
        VoxelShape $$0 = Block.cube(14.0);
        VoxelShape $$1 = Block.cube(12.0);
        Map<AttachFace, Map<Direction, VoxelShape>> $$2 = Shapes.rotateAttachFace(Block.boxZ(6.0, 4.0, 8.0, 16.0));
        return this.getShapeForEachState($$3 -> Shapes.join((VoxelShape)((Map)$$2.get($$3.getValue(FACE))).get($$3.getValue(FACING)), $$3.getValue(POWERED) != false ? $$0 : $$1, BooleanOp.ONLY_FIRST));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if ($$0.getValue(POWERED).booleanValue()) {
            return InteractionResult.CONSUME;
        }
        this.press($$0, $$1, $$2, $$3);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onExplosionHit(BlockState $$0, ServerLevel $$1, BlockPos $$2, Explosion $$3, BiConsumer<ItemStack, BlockPos> $$4) {
        if ($$3.canTriggerBlocks() && !$$0.getValue(POWERED).booleanValue()) {
            this.press($$0, $$1, $$2, null);
        }
        super.onExplosionHit($$0, $$1, $$2, $$3, $$4);
    }

    public void press(BlockState $$0, Level $$1, BlockPos $$2, @Nullable Player $$3) {
        $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, true), 3);
        this.updateNeighbours($$0, $$1, $$2);
        $$1.scheduleTick($$2, this, this.ticksToStayPressed);
        this.playSound($$3, $$1, $$2, true);
        $$1.gameEvent((Entity)$$3, GameEvent.BLOCK_ACTIVATE, $$2);
    }

    protected void playSound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2, boolean $$3) {
        $$1.playSound($$3 ? $$0 : null, $$2, this.getSound($$3), SoundSource.BLOCKS);
    }

    protected SoundEvent getSound(boolean $$0) {
        return $$0 ? this.type.buttonClickOn() : this.type.buttonClickOff();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        if (!$$3 && $$0.getValue(POWERED).booleanValue()) {
            this.updateNeighbours($$0, $$1, $$2);
        }
    }

    @Override
    protected int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(POWERED).booleanValue() && ButtonBlock.getConnectedDirection($$0) == $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    protected boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$0, $$1, $$2);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$1.isClientSide || !this.type.canButtonBeActivatedByArrows() || $$0.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed($$0, $$1, $$2);
    }

    protected void checkPressed(BlockState $$0, Level $$1, BlockPos $$2) {
        boolean $$5;
        AbstractArrow $$3 = this.type.canButtonBeActivatedByArrows() ? (AbstractArrow)$$1.getEntitiesOfClass(AbstractArrow.class, $$0.getShape($$1, $$2).bounds().move($$2)).stream().findFirst().orElse(null) : null;
        boolean $$4 = $$3 != null;
        if ($$4 != ($$5 = $$0.getValue(POWERED).booleanValue())) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$4), 3);
            this.updateNeighbours($$0, $$1, $$2);
            this.playSound(null, $$1, $$2, $$4);
            $$1.gameEvent((Entity)$$3, $$4 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, $$2);
        }
        if ($$4) {
            $$1.scheduleTick(new BlockPos($$2), this, this.ticksToStayPressed);
        }
    }

    private void updateNeighbours(BlockState $$0, Level $$1, BlockPos $$2) {
        Direction $$3;
        Orientation $$4 = ExperimentalRedstoneUtils.initialOrientation($$1, $$3, ($$3 = ButtonBlock.getConnectedDirection($$0).getOpposite()).getAxis().isHorizontal() ? Direction.UP : (Direction)$$0.getValue(FACING));
        $$1.updateNeighborsAt($$2, this, $$4);
        $$1.updateNeighborsAt($$2.relative($$3), this, $$4);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, POWERED, FACE);
    }
}

