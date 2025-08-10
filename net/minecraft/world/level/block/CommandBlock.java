/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public class CommandBlock
extends BaseEntityBlock
implements GameMasterBlock {
    public static final MapCodec<CommandBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.fieldOf("automatic").forGetter($$0 -> $$0.automatic), CommandBlock.propertiesCodec()).apply((Applicative)$$02, CommandBlock::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;
    private final boolean automatic;

    public MapCodec<CommandBlock> codec() {
        return CODEC;
    }

    public CommandBlock(boolean $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CONDITIONAL, false));
        this.automatic = $$0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        CommandBlockEntity $$2 = new CommandBlockEntity($$0, $$1);
        $$2.setAutomatic(this.automatic);
        return $$2;
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof CommandBlockEntity) {
            CommandBlockEntity $$7 = (CommandBlockEntity)$$6;
            this.setPoweredAndUpdate($$1, $$2, $$7, $$1.hasNeighborSignal($$2));
        }
    }

    private void setPoweredAndUpdate(Level $$0, BlockPos $$1, CommandBlockEntity $$2, boolean $$3) {
        boolean $$4 = $$2.isPowered();
        if ($$3 == $$4) {
            return;
        }
        $$2.setPowered($$3);
        if ($$3) {
            if ($$2.isAutomatic() || $$2.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
                return;
            }
            $$2.markConditionMet();
            $$0.scheduleTick($$1, this, 1);
        }
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if ($$4 instanceof CommandBlockEntity) {
            CommandBlockEntity $$5 = (CommandBlockEntity)$$4;
            BaseCommandBlock $$6 = $$5.getCommandBlock();
            boolean $$7 = !StringUtil.isNullOrEmpty($$6.getCommand());
            CommandBlockEntity.Mode $$8 = $$5.getMode();
            boolean $$9 = $$5.wasConditionMet();
            if ($$8 == CommandBlockEntity.Mode.AUTO) {
                $$5.markConditionMet();
                if ($$9) {
                    this.execute($$0, $$1, $$2, $$6, $$7);
                } else if ($$5.isConditional()) {
                    $$6.setSuccessCount(0);
                }
                if ($$5.isPowered() || $$5.isAutomatic()) {
                    $$1.scheduleTick($$2, this, 1);
                }
            } else if ($$8 == CommandBlockEntity.Mode.REDSTONE) {
                if ($$9) {
                    this.execute($$0, $$1, $$2, $$6, $$7);
                } else if ($$5.isConditional()) {
                    $$6.setSuccessCount(0);
                }
            }
            $$1.updateNeighbourForOutputSignal($$2, this);
        }
    }

    private void execute(BlockState $$0, ServerLevel $$1, BlockPos $$2, BaseCommandBlock $$3, boolean $$4) {
        if ($$4) {
            $$3.performCommand($$1);
        } else {
            $$3.setSuccessCount(0);
        }
        CommandBlock.executeChain($$1, $$2, $$0.getValue(FACING));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof CommandBlockEntity && $$3.canUseGameMasterBlocks()) {
            $$3.openCommandBlock((CommandBlockEntity)$$5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof CommandBlockEntity) {
            return ((CommandBlockEntity)$$3).getCommandBlock().getSuccessCount();
        }
        return 0;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        void $$7;
        BlockEntity $$5 = $$0.getBlockEntity($$1);
        if (!($$5 instanceof CommandBlockEntity)) {
            return;
        }
        CommandBlockEntity $$6 = (CommandBlockEntity)$$5;
        BaseCommandBlock $$8 = $$7.getCommandBlock();
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$9 = (ServerLevel)$$0;
            if (!$$4.has(DataComponents.BLOCK_ENTITY_DATA)) {
                $$8.setTrackOutput($$9.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
                $$7.setAutomatic(this.automatic);
            }
            boolean $$10 = $$0.hasNeighborSignal($$1);
            this.setPoweredAndUpdate($$0, $$1, (CommandBlockEntity)$$7, $$10);
        }
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getNearestLookingDirection().getOpposite());
    }

    private static void executeChain(ServerLevel $$0, BlockPos $$1, Direction $$2) {
        BlockPos.MutableBlockPos $$3 = $$1.mutable();
        GameRules $$4 = $$0.getGameRules();
        int $$5 = $$4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
        while ($$5-- > 0) {
            CommandBlockEntity $$9;
            BlockEntity $$8;
            $$3.move($$2);
            BlockState $$6 = $$0.getBlockState($$3);
            Block $$7 = $$6.getBlock();
            if (!$$6.is(Blocks.CHAIN_COMMAND_BLOCK) || !(($$8 = $$0.getBlockEntity($$3)) instanceof CommandBlockEntity) || ($$9 = (CommandBlockEntity)$$8).getMode() != CommandBlockEntity.Mode.SEQUENCE) break;
            if ($$9.isPowered() || $$9.isAutomatic()) {
                BaseCommandBlock $$10 = $$9.getCommandBlock();
                if ($$9.markConditionMet()) {
                    if (!$$10.performCommand($$0)) break;
                    $$0.updateNeighbourForOutputSignal($$3, $$7);
                } else if ($$9.isConditional()) {
                    $$10.setSuccessCount(0);
                }
            }
            $$2 = $$6.getValue(FACING);
        }
        if ($$5 <= 0) {
            int $$11 = Math.max($$4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)$$11);
        }
    }
}

