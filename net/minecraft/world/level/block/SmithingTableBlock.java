/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SmithingTableBlock
extends CraftingTableBlock {
    public static final MapCodec<SmithingTableBlock> CODEC = SmithingTableBlock.simpleCodec(SmithingTableBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.upgrade");

    public MapCodec<SmithingTableBlock> codec() {
        return CODEC;
    }

    protected SmithingTableBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$22) {
        return new SimpleMenuProvider(($$2, $$3, $$4) -> new SmithingMenu($$2, $$3, ContainerLevelAccess.create($$1, $$22)), CONTAINER_TITLE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        if (!$$1.isClientSide) {
            $$3.openMenu($$0.getMenuProvider($$1, $$2));
            $$3.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);
        }
        return InteractionResult.SUCCESS;
    }
}

