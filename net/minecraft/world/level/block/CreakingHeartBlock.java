/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CreakingHeartBlock
extends BaseEntityBlock {
    public static final MapCodec<CreakingHeartBlock> CODEC = CreakingHeartBlock.simpleCodec(CreakingHeartBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<CreakingHeartState> STATE = BlockStateProperties.CREAKING_HEART_STATE;
    public static final BooleanProperty NATURAL = BlockStateProperties.NATURAL;

    public MapCodec<CreakingHeartBlock> codec() {
        return CODEC;
    }

    protected CreakingHeartBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y)).setValue(STATE, CreakingHeartState.UPROOTED)).setValue(NATURAL, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new CreakingHeartBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        if ($$0.isClientSide) {
            return null;
        }
        if ($$1.getValue(STATE) != CreakingHeartState.UPROOTED) {
            return CreakingHeartBlock.createTickerHelper($$2, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::serverTick);
        }
        return null;
    }

    public static boolean isNaturalNight(Level $$0) {
        return $$0.isMoonVisible();
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!CreakingHeartBlock.isNaturalNight($$1)) {
            return;
        }
        if ($$0.getValue(STATE) == CreakingHeartState.UPROOTED) {
            return;
        }
        if ($$3.nextInt(16) == 0 && CreakingHeartBlock.isSurroundedByLogs($$1, $$2)) {
            $$1.playLocalSound($$2.getX(), $$2.getY(), $$2.getZ(), SoundEvents.CREAKING_HEART_IDLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        $$2.scheduleTick($$3, this, 1);
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$4 = CreakingHeartBlock.updateState($$0, $$1, $$2);
        if ($$4 != $$0) {
            $$1.setBlock($$2, $$4, 3);
        }
    }

    private static BlockState updateState(BlockState $$0, Level $$1, BlockPos $$2) {
        boolean $$4;
        boolean $$3 = CreakingHeartBlock.hasRequiredLogs($$0, $$1, $$2);
        boolean bl = $$4 = $$0.getValue(STATE) == CreakingHeartState.UPROOTED;
        if ($$3 && $$4) {
            return (BlockState)$$0.setValue(STATE, CreakingHeartBlock.isNaturalNight($$1) ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT);
        }
        return $$0;
    }

    public static boolean hasRequiredLogs(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        Direction.Axis $$3 = $$0.getValue(AXIS);
        for (Direction $$4 : $$3.g()) {
            BlockState $$5 = $$1.getBlockState($$2.relative($$4));
            if ($$5.is(BlockTags.PALE_OAK_LOGS) && $$5.getValue(AXIS) == $$3) continue;
            return false;
        }
        return true;
    }

    private static boolean isSurroundedByLogs(LevelAccessor $$0, BlockPos $$1) {
        for (Direction $$2 : Direction.values()) {
            BlockPos $$3 = $$1.relative($$2);
            BlockState $$4 = $$0.getBlockState($$3);
            if ($$4.is(BlockTags.PALE_OAK_LOGS)) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return CreakingHeartBlock.updateState((BlockState)this.defaultBlockState().setValue(AXIS, $$0.getClickedFace().getAxis()), $$0.getLevel(), $$0.getClickedPos());
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return RotatedPillarBlock.rotatePillar($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AXIS, STATE, NATURAL);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    protected void onExplosionHit(BlockState $$0, ServerLevel $$1, BlockPos $$2, Explosion $$3, BiConsumer<ItemStack, BlockPos> $$4) {
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity $$5 = (CreakingHeartBlockEntity)blockEntity;
            if ($$3 instanceof ServerExplosion) {
                ServerExplosion $$6 = (ServerExplosion)$$3;
                if ($$3.getBlockInteraction().shouldAffectBlocklikeEntities()) {
                    $$5.removeProtector($$6.getDamageSource());
                    LivingEntity livingEntity = $$3.getIndirectSourceEntity();
                    if (livingEntity instanceof Player) {
                        Player $$7 = (Player)livingEntity;
                        if ($$3.getBlockInteraction().shouldAffectBlocklikeEntities()) {
                            this.tryAwardExperience($$7, $$0, $$1, $$2);
                        }
                    }
                }
            }
        }
        super.onExplosionHit($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockEntity blockEntity = $$0.getBlockEntity($$1);
        if (blockEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity $$4 = (CreakingHeartBlockEntity)blockEntity;
            $$4.removeProtector($$3.damageSources().playerAttack($$3));
            this.tryAwardExperience($$3, $$2, $$0, $$1);
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    private void tryAwardExperience(Player $$0, BlockState $$1, Level $$2, BlockPos $$3) {
        if (!$$0.preventsBlockDrops() && !$$0.isSpectator() && $$1.getValue(NATURAL).booleanValue() && $$2 instanceof ServerLevel) {
            ServerLevel $$4 = (ServerLevel)$$2;
            this.popExperience($$4, $$3, $$2.random.nextIntBetweenInclusive(20, 24));
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        void $$4;
        if ($$0.getValue(STATE) == CreakingHeartState.UPROOTED) {
            return 0;
        }
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof CreakingHeartBlockEntity)) {
            return 0;
        }
        CreakingHeartBlockEntity $$3 = (CreakingHeartBlockEntity)blockEntity;
        return $$4.getAnalogOutputSignal();
    }
}

