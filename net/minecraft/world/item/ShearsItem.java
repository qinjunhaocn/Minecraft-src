/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShearsItem
extends Item {
    public ShearsItem(Item.Properties $$0) {
        super($$0);
    }

    public static Tool createToolProperties() {
        HolderGetter<Block> $$0 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return new Tool(List.of((Object)((Object)Tool.Rule.minesAndDrops(HolderSet.a(Blocks.COBWEB.builtInRegistryHolder()), 15.0f)), (Object)((Object)Tool.Rule.overrideSpeed($$0.getOrThrow(BlockTags.LEAVES), 15.0f)), (Object)((Object)Tool.Rule.overrideSpeed($$0.getOrThrow(BlockTags.WOOL), 5.0f)), (Object)((Object)Tool.Rule.overrideSpeed(HolderSet.a(Blocks.VINE.builtInRegistryHolder(), Blocks.GLOW_LICHEN.builtInRegistryHolder()), 2.0f))), 1.0f, 1, true);
    }

    @Override
    public boolean mineBlock(ItemStack $$0, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        Tool $$5 = $$0.get(DataComponents.TOOL);
        if ($$5 == null) {
            return false;
        }
        if (!$$1.isClientSide() && !$$2.is(BlockTags.FIRE) && $$5.damagePerBlock() > 0) {
            $$0.hurtAndBreak($$5.damagePerBlock(), $$4, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        GrowingPlantHeadBlock $$5;
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        Block $$4 = $$3.getBlock();
        if ($$4 instanceof GrowingPlantHeadBlock && !($$5 = (GrowingPlantHeadBlock)$$4).isMaxAge($$3)) {
            Player $$6 = $$0.getPlayer();
            ItemStack $$7 = $$0.getItemInHand();
            if ($$6 instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)$$6, $$2, $$7);
            }
            $$1.playSound((Entity)$$6, $$2, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0f, 1.0f);
            BlockState $$8 = $$5.getMaxAgeState($$3);
            $$1.setBlockAndUpdate($$2, $$8);
            $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$0.getPlayer(), $$8));
            if ($$6 != null) {
                $$7.hurtAndBreak(1, (LivingEntity)$$6, LivingEntity.getSlotForHand($$0.getHand()));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn($$0);
    }
}

