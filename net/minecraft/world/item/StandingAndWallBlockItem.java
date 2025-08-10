/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class StandingAndWallBlockItem
extends BlockItem {
    protected final Block wallBlock;
    private final Direction attachmentDirection;

    public StandingAndWallBlockItem(Block $$0, Block $$1, Direction $$2, Item.Properties $$3) {
        super($$0, $$3);
        this.wallBlock = $$1;
        this.attachmentDirection = $$2;
    }

    protected boolean canPlace(LevelReader $$0, BlockState $$1, BlockPos $$2) {
        return $$1.canSurvive($$0, $$2);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext $$0) {
        BlockState $$1 = this.wallBlock.getStateForPlacement($$0);
        BlockState $$2 = null;
        Level $$3 = $$0.getLevel();
        BlockPos $$4 = $$0.getClickedPos();
        for (Direction $$5 : $$0.f()) {
            BlockState $$6;
            if ($$5 == this.attachmentDirection.getOpposite()) continue;
            BlockState blockState = $$6 = $$5 == this.attachmentDirection ? this.getBlock().getStateForPlacement($$0) : $$1;
            if ($$6 == null || !this.canPlace($$3, $$6, $$4)) continue;
            $$2 = $$6;
            break;
        }
        return $$2 != null && $$3.isUnobstructed($$2, $$4, CollisionContext.empty()) ? $$2 : null;
    }

    @Override
    public void registerBlocks(Map<Block, Item> $$0, Item $$1) {
        super.registerBlocks($$0, $$1);
        $$0.put(this.wallBlock, $$1);
    }
}

