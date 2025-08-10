/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class SolidBucketItem
extends BlockItem
implements DispensibleContainerItem {
    private final SoundEvent placeSound;

    public SolidBucketItem(Block $$0, SoundEvent $$1, Item.Properties $$2) {
        super($$0, $$2);
        this.placeSound = $$1;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        InteractionResult $$1 = super.useOn($$0);
        Player $$2 = $$0.getPlayer();
        if ($$1.consumesAction() && $$2 != null) {
            $$2.setItemInHand($$0.getHand(), BucketItem.getEmptySuccessItem($$0.getItemInHand(), $$2));
        }
        return $$1;
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState $$0) {
        return this.placeSound;
    }

    @Override
    public boolean emptyContents(@Nullable LivingEntity $$0, Level $$1, BlockPos $$2, @Nullable BlockHitResult $$3) {
        if ($$1.isInWorldBounds($$2) && $$1.isEmptyBlock($$2)) {
            if (!$$1.isClientSide) {
                $$1.setBlock($$2, this.getBlock().defaultBlockState(), 3);
            }
            $$1.gameEvent((Entity)$$0, GameEvent.FLUID_PLACE, $$2);
            $$1.playSound((Entity)$$0, $$2, this.placeSound, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}

