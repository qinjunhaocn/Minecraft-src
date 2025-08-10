/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AxeItem
extends Item {
    protected static final Map<Block, Block> STRIPPABLES = new ImmutableMap.Builder<Block, Block>().put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD).put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build();

    public AxeItem(ToolMaterial $$0, float $$1, float $$2, Item.Properties $$3) {
        super($$3.axe($$0, $$1, $$2));
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Player $$3 = $$0.getPlayer();
        if (AxeItem.playerHasBlockingItemUseIntent($$0)) {
            return InteractionResult.PASS;
        }
        Optional<BlockState> $$4 = this.evaluateNewBlockState($$1, $$2, $$3, $$1.getBlockState($$2));
        if ($$4.isEmpty()) {
            return InteractionResult.PASS;
        }
        ItemStack $$5 = $$0.getItemInHand();
        if ($$3 instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)$$3, $$2, $$5);
        }
        $$1.setBlock($$2, $$4.get(), 11);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$3, $$4.get()));
        if ($$3 != null) {
            $$5.hurtAndBreak(1, (LivingEntity)$$3, LivingEntity.getSlotForHand($$0.getHand()));
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean playerHasBlockingItemUseIntent(UseOnContext $$0) {
        Player $$1 = $$0.getPlayer();
        return $$0.getHand().equals((Object)InteractionHand.MAIN_HAND) && $$1.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS) && !$$1.isSecondaryUseActive();
    }

    private Optional<BlockState> evaluateNewBlockState(Level $$0, BlockPos $$12, @Nullable Player $$2, BlockState $$3) {
        Optional<BlockState> $$4 = this.getStripped($$3);
        if ($$4.isPresent()) {
            $$0.playSound((Entity)$$2, $$12, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            return $$4;
        }
        Optional<BlockState> $$5 = WeatheringCopper.getPrevious($$3);
        if ($$5.isPresent()) {
            $$0.playSound((Entity)$$2, $$12, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$0.levelEvent($$2, 3005, $$12, 0);
            return $$5;
        }
        Optional<BlockState> $$6 = Optional.ofNullable((Block)HoneycombItem.WAX_OFF_BY_BLOCK.get().get($$3.getBlock())).map($$1 -> $$1.withPropertiesOf($$3));
        if ($$6.isPresent()) {
            $$0.playSound((Entity)$$2, $$12, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$0.levelEvent($$2, 3004, $$12, 0);
            return $$6;
        }
        return Optional.empty();
    }

    private Optional<BlockState> getStripped(BlockState $$0) {
        return Optional.ofNullable(STRIPPABLES.get($$0.getBlock())).map($$1 -> (BlockState)$$1.defaultBlockState().setValue(RotatedPillarBlock.AXIS, $$0.getValue(RotatedPillarBlock.AXIS)));
    }
}

