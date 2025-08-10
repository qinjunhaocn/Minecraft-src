/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShovelItem
extends Item {
    protected static final Map<Block, BlockState> FLATTENABLES = Maps.newHashMap(new ImmutableMap.Builder<Block, BlockState>().put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.PODZOL, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.MYCELIUM, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.defaultBlockState()).build());

    public ShovelItem(ToolMaterial $$0, float $$1, float $$2, Item.Properties $$3) {
        super($$3.shovel($$0, $$1, $$2));
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$0.getClickedFace() != Direction.DOWN) {
            Player $$4 = $$0.getPlayer();
            BlockState $$5 = FLATTENABLES.get($$3.getBlock());
            BlockState $$6 = null;
            if ($$5 != null && $$1.getBlockState($$2.above()).isAir()) {
                $$1.playSound((Entity)$$4, $$2, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$6 = $$5;
            } else if ($$3.getBlock() instanceof CampfireBlock && $$3.getValue(CampfireBlock.LIT).booleanValue()) {
                if (!$$1.isClientSide()) {
                    $$1.levelEvent(null, 1009, $$2, 0);
                }
                CampfireBlock.dowse($$0.getPlayer(), $$1, $$2, $$3);
                $$6 = (BlockState)$$3.setValue(CampfireBlock.LIT, false);
            }
            if ($$6 != null) {
                if (!$$1.isClientSide) {
                    $$1.setBlock($$2, $$6, 11);
                    $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4, $$6));
                    if ($$4 != null) {
                        $$0.getItemInHand().hurtAndBreak(1, (LivingEntity)$$4, LivingEntity.getSlotForHand($$0.getHand()));
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }
}

