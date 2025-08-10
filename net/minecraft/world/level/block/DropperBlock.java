/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class DropperBlock
extends DispenserBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<DropperBlock> CODEC = DropperBlock.simpleCodec(DropperBlock::new);
    private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

    public MapCodec<DropperBlock> codec() {
        return CODEC;
    }

    public DropperBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(Level $$0, ItemStack $$1) {
        return DISPENSE_BEHAVIOUR;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new DropperBlockEntity($$0, $$1);
    }

    @Override
    protected void dispenseFrom(ServerLevel $$0, BlockState $$1, BlockPos $$2) {
        ItemStack $$10;
        DispenserBlockEntity $$3 = $$0.getBlockEntity($$2, BlockEntityType.DROPPER).orElse(null);
        if ($$3 == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dropper without matching block entity at {}", (Object)$$2);
            return;
        }
        BlockSource $$4 = new BlockSource($$0, $$2, $$1, $$3);
        int $$5 = $$3.getRandomSlot($$0.random);
        if ($$5 < 0) {
            $$0.levelEvent(1001, $$2, 0);
            return;
        }
        ItemStack $$6 = $$3.getItem($$5);
        if ($$6.isEmpty()) {
            return;
        }
        Direction $$7 = (Direction)$$0.getBlockState($$2).getValue(FACING);
        Container $$8 = HopperBlockEntity.getContainerAt($$0, $$2.relative($$7));
        if ($$8 == null) {
            ItemStack $$9 = DISPENSE_BEHAVIOUR.dispense($$4, $$6);
        } else {
            $$10 = HopperBlockEntity.addItem($$3, $$8, $$6.copyWithCount(1), $$7.getOpposite());
            if ($$10.isEmpty()) {
                $$10 = $$6.copy();
                $$10.shrink(1);
            } else {
                $$10 = $$6.copy();
            }
        }
        $$3.setItem($$5, $$10);
    }
}

