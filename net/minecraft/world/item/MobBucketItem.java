/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class MobBucketItem
extends BucketItem {
    private final EntityType<? extends Mob> type;
    private final SoundEvent emptySound;

    public MobBucketItem(EntityType<? extends Mob> $$0, Fluid $$1, SoundEvent $$2, Item.Properties $$3) {
        super($$1, $$3);
        this.type = $$0;
        this.emptySound = $$2;
    }

    @Override
    public void checkExtraContent(@Nullable LivingEntity $$0, Level $$1, ItemStack $$2, BlockPos $$3) {
        if ($$1 instanceof ServerLevel) {
            this.spawn((ServerLevel)$$1, $$2, $$3);
            $$1.gameEvent((Entity)$$0, GameEvent.ENTITY_PLACE, $$3);
        }
    }

    @Override
    protected void playEmptySound(@Nullable LivingEntity $$0, LevelAccessor $$1, BlockPos $$2) {
        $$1.playSound($$0, $$2, this.emptySound, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawn(ServerLevel $$0, ItemStack $$1, BlockPos $$2) {
        Mob $$3 = this.type.create($$0, EntityType.createDefaultStackConfig($$0, $$1, null), $$2, EntitySpawnReason.BUCKET, true, false);
        if ($$3 instanceof Bucketable) {
            Bucketable $$4 = (Bucketable)((Object)$$3);
            CustomData $$5 = $$1.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
            $$4.loadFromBucketTag($$5.copyTag());
            $$4.setFromBucket(true);
        }
        if ($$3 != null) {
            $$0.addFreshEntityWithPassengers($$3);
            $$3.playAmbientSound();
        }
    }
}

