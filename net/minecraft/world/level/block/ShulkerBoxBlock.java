/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock
extends BaseEntityBlock {
    public static final MapCodec<ShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)DyeColor.CODEC.optionalFieldOf("color").forGetter($$0 -> Optional.ofNullable($$0.color)), ShulkerBoxBlock.propertiesCodec()).apply((Applicative)$$02, ($$0, $$1) -> new ShulkerBoxBlock($$0.orElse(null), (BlockBehaviour.Properties)$$1)));
    public static final Map<Direction, VoxelShape> SHAPES_OPEN_SUPPORT = Shapes.rotateAll(Block.boxZ(16.0, 0.0, 1.0));
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final ResourceLocation CONTENTS = ResourceLocation.withDefaultNamespace("contents");
    @Nullable
    private final DyeColor color;

    public MapCodec<ShulkerBoxBlock> codec() {
        return CODEC;
    }

    public ShulkerBoxBlock(@Nullable DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ShulkerBoxBlockEntity(this.color, $$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return ShulkerBoxBlock.createTickerHelper($$2, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if ($$1 instanceof ServerLevel) {
            ShulkerBoxBlockEntity $$6;
            ServerLevel $$5 = (ServerLevel)$$1;
            BlockEntity blockEntity = $$1.getBlockEntity($$2);
            if (blockEntity instanceof ShulkerBoxBlockEntity && ShulkerBoxBlock.canOpen($$0, $$1, $$2, $$6 = (ShulkerBoxBlockEntity)blockEntity)) {
                $$3.openMenu($$6);
                $$3.awardStat(Stats.OPEN_SHULKER_BOX);
                PiglinAi.angerNearbyPiglins($$5, $$3, true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean canOpen(BlockState $$0, Level $$1, BlockPos $$2, ShulkerBoxBlockEntity $$3) {
        if ($$3.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
            return true;
        }
        AABB $$4 = Shulker.getProgressDeltaAabb(1.0f, $$0.getValue(FACING), 0.0f, 0.5f, $$2.getBottomCenter()).deflate(1.0E-6);
        return $$1.noCollision($$4);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING);
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockEntity $$4 = $$0.getBlockEntity($$1);
        if ($$4 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$5 = (ShulkerBoxBlockEntity)$$4;
            if (!$$0.isClientSide && $$3.preventsBlockDrops() && !$$5.isEmpty()) {
                ItemStack $$6 = ShulkerBoxBlock.getColoredItemStack(this.getColor());
                $$6.applyComponents($$4.collectComponents());
                ItemEntity $$7 = new ItemEntity($$0, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, $$6);
                $$7.setDefaultPickUpDelay();
                $$0.addFreshEntity($$7);
            } else {
                $$5.unpackLootTable($$3);
            }
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState $$0, LootParams.Builder $$12) {
        BlockEntity $$2 = $$12.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if ($$2 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$3 = (ShulkerBoxBlockEntity)$$2;
            $$12 = $$12.withDynamicDrop(CONTENTS, $$1 -> {
                for (int $$2 = 0; $$2 < $$3.getContainerSize(); ++$$2) {
                    $$1.accept($$3.getItem($$2));
                }
            });
        }
        return super.getDrops($$0, $$12);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        ShulkerBoxBlockEntity $$4;
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof ShulkerBoxBlockEntity && !($$4 = (ShulkerBoxBlockEntity)$$3).isClosed()) {
            return SHAPES_OPEN_SUPPORT.get($$0.getValue(FACING).getOpposite());
        }
        return Shapes.block();
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if ($$4 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$5 = (ShulkerBoxBlockEntity)$$4;
            return Shapes.create($$5.getBoundingBox($$0));
        }
        return Shapes.block();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity($$1.getBlockEntity($$2));
    }

    public static Block getBlockByColor(@Nullable DyeColor $$0) {
        if ($$0 == null) {
            return Blocks.SHULKER_BOX;
        }
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case DyeColor.WHITE -> Blocks.WHITE_SHULKER_BOX;
            case DyeColor.ORANGE -> Blocks.ORANGE_SHULKER_BOX;
            case DyeColor.MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
            case DyeColor.LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case DyeColor.YELLOW -> Blocks.YELLOW_SHULKER_BOX;
            case DyeColor.LIME -> Blocks.LIME_SHULKER_BOX;
            case DyeColor.PINK -> Blocks.PINK_SHULKER_BOX;
            case DyeColor.GRAY -> Blocks.GRAY_SHULKER_BOX;
            case DyeColor.LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case DyeColor.CYAN -> Blocks.CYAN_SHULKER_BOX;
            case DyeColor.BLUE -> Blocks.BLUE_SHULKER_BOX;
            case DyeColor.BROWN -> Blocks.BROWN_SHULKER_BOX;
            case DyeColor.GREEN -> Blocks.GREEN_SHULKER_BOX;
            case DyeColor.RED -> Blocks.RED_SHULKER_BOX;
            case DyeColor.BLACK -> Blocks.BLACK_SHULKER_BOX;
            case DyeColor.PURPLE -> Blocks.PURPLE_SHULKER_BOX;
        };
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getColoredItemStack(@Nullable DyeColor $$0) {
        return new ItemStack(ShulkerBoxBlock.getBlockByColor($$0));
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }
}

