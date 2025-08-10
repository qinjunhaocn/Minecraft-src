/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core.dispenser;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior
extends OptionalDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource $$02, ItemStack $$1) {
        ServerLevel $$2 = $$02.level();
        if (!$$2.isClientSide()) {
            BlockPos $$3 = $$02.pos().relative($$02.state().getValue(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenseItemBehavior.tryShearBeehive($$2, $$3) || ShearsDispenseItemBehavior.tryShearEntity($$2, $$3, $$1));
            if (this.isSuccess()) {
                $$1.hurtAndBreak(1, $$2, null, $$0 -> {});
            }
        }
        return $$1;
    }

    private static boolean tryShearBeehive(ServerLevel $$02, BlockPos $$1) {
        int $$3;
        BlockState $$2 = $$02.getBlockState($$1);
        if ($$2.is(BlockTags.BEEHIVES, $$0 -> $$0.hasProperty(BeehiveBlock.HONEY_LEVEL) && $$0.getBlock() instanceof BeehiveBlock) && ($$3 = $$2.getValue(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            $$02.playSound(null, $$1, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb($$02, $$1);
            ((BeehiveBlock)$$2.getBlock()).releaseBeesAndResetHoneyLevel($$02, $$2, $$1, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            $$02.gameEvent(null, GameEvent.SHEAR, $$1);
            return true;
        }
        return false;
    }

    private static boolean tryShearEntity(ServerLevel $$0, BlockPos $$1, ItemStack $$2) {
        List<Entity> $$3 = $$0.getEntitiesOfClass(Entity.class, new AABB($$1), EntitySelector.NO_SPECTATORS);
        for (Entity $$4 : $$3) {
            Shearable $$5;
            if ($$4.shearOffAllLeashConnections(null)) {
                return true;
            }
            if (!($$4 instanceof Shearable) || !($$5 = (Shearable)((Object)$$4)).readyForShearing()) continue;
            $$5.shear($$0, SoundSource.BLOCKS, $$2);
            $$0.gameEvent(null, GameEvent.SHEAR, $$1);
            return true;
        }
        return false;
    }
}

