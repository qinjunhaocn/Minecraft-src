/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantingTableBlock
extends BaseEntityBlock {
    public static final MapCodec<EnchantingTableBlock> CODEC = EnchantingTableBlock.simpleCodec(EnchantingTableBlock::new);
    public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2).filter($$0 -> Math.abs($$0.getX()) == 2 || Math.abs($$0.getZ()) == 2).map(BlockPos::immutable).toList();
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 12.0);

    public MapCodec<EnchantingTableBlock> codec() {
        return CODEC;
    }

    protected EnchantingTableBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    public static boolean isValidBookShelf(Level $$0, BlockPos $$1, BlockPos $$2) {
        return $$0.getBlockState($$1.offset($$2)).is(BlockTags.ENCHANTMENT_POWER_PROVIDER) && $$0.getBlockState($$1.offset($$2.getX() / 2, $$2.getY(), $$2.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        super.animateTick($$0, $$1, $$2, $$3);
        for (BlockPos $$4 : BOOKSHELF_OFFSETS) {
            if ($$3.nextInt(16) != 0 || !EnchantingTableBlock.isValidBookShelf($$1, $$2, $$4)) continue;
            $$1.addParticle(ParticleTypes.ENCHANT, (double)$$2.getX() + 0.5, (double)$$2.getY() + 2.0, (double)$$2.getZ() + 0.5, (double)((float)$$4.getX() + $$3.nextFloat()) - 0.5, (float)$$4.getY() - $$3.nextFloat() - 1.0f, (double)((float)$$4.getZ() + $$3.nextFloat()) - 0.5);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new EnchantingTableBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? EnchantingTableBlock.createTickerHelper($$2, BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntity::bookAnimationTick) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if (!$$1.isClientSide) {
            $$3.openMenu($$0.getMenuProvider($$1, $$2));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$22) {
        BlockEntity $$32 = $$1.getBlockEntity($$22);
        if ($$32 instanceof EnchantingTableBlockEntity) {
            Component $$42 = ((Nameable)((Object)$$32)).getDisplayName();
            return new SimpleMenuProvider(($$2, $$3, $$4) -> new EnchantmentMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)), $$42);
        }
        return null;
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

