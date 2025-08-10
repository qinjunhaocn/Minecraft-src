/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<? extends Mob>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
    private final EntityType<?> defaultType;

    public SpawnEggItem(EntityType<? extends Mob> $$0, Item.Properties $$1) {
        super($$1);
        this.defaultType = $$0;
        BY_ID.put($$0, this);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$9;
        Level $$1 = $$0.getLevel();
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack $$2 = $$0.getItemInHand();
        BlockPos $$3 = $$0.getClickedPos();
        Direction $$4 = $$0.getClickedFace();
        BlockState $$5 = $$1.getBlockState($$3);
        BlockEntity blockEntity = $$1.getBlockEntity($$3);
        if (blockEntity instanceof Spawner) {
            Spawner $$6 = (Spawner)((Object)blockEntity);
            EntityType<?> $$7 = this.getType($$1.registryAccess(), $$2);
            $$6.setEntityId($$7, $$1.getRandom());
            $$1.sendBlockUpdated($$3, $$5, $$5, 3);
            $$1.gameEvent((Entity)$$0.getPlayer(), GameEvent.BLOCK_CHANGE, $$3);
            $$2.shrink(1);
            return InteractionResult.SUCCESS;
        }
        if ($$5.getCollisionShape($$1, $$3).isEmpty()) {
            BlockPos $$8 = $$3;
        } else {
            $$9 = $$3.relative($$4);
        }
        EntityType<?> $$10 = this.getType($$1.registryAccess(), $$2);
        if ($$10.spawn((ServerLevel)$$1, $$2, $$0.getPlayer(), $$9, EntitySpawnReason.SPAWN_ITEM_USE, true, !Objects.equals($$3, $$9) && $$4 == Direction.UP) != null) {
            $$2.shrink(1);
            $$1.gameEvent((Entity)$$0.getPlayer(), GameEvent.ENTITY_PLACE, $$3);
        }
        return InteractionResult.SUCCESS;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
        void $$6;
        ItemStack $$3 = $$1.getItemInHand($$2);
        BlockHitResult $$4 = SpawnEggItem.getPlayerPOVHitResult($$0, $$1, ClipContext.Fluid.SOURCE_ONLY);
        if ($$4.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }
        if (!($$0 instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel $$5 = (ServerLevel)$$0;
        BlockHitResult $$7 = $$4;
        BlockPos $$8 = $$7.getBlockPos();
        if (!($$0.getBlockState($$8).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
        }
        if (!$$0.mayInteract($$1, $$8) || !$$1.mayUseItemAt($$8, $$7.getDirection(), $$3)) {
            return InteractionResult.FAIL;
        }
        EntityType<?> $$9 = this.getType($$6.registryAccess(), $$3);
        Object $$10 = $$9.spawn((ServerLevel)$$6, $$3, $$1, $$8, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
        if ($$10 == null) {
            return InteractionResult.PASS;
        }
        $$3.consume(1, $$1);
        $$1.awardStat(Stats.ITEM_USED.get(this));
        $$0.gameEvent((Entity)$$1, GameEvent.ENTITY_PLACE, ((Entity)$$10).position());
        return InteractionResult.SUCCESS;
    }

    public boolean spawnsEntity(HolderLookup.Provider $$0, ItemStack $$1, EntityType<?> $$2) {
        return Objects.equals(this.getType($$0, $$1), $$2);
    }

    @Nullable
    public static SpawnEggItem byId(@Nullable EntityType<?> $$0) {
        return BY_ID.get($$0);
    }

    public static Iterable<SpawnEggItem> eggs() {
        return Iterables.unmodifiableIterable(BY_ID.values());
    }

    public EntityType<?> getType(HolderLookup.Provider $$0, ItemStack $$1) {
        EntityType<?> $$3;
        CustomData $$2 = $$1.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!$$2.isEmpty() && ($$3 = $$2.parseEntityType($$0, Registries.ENTITY_TYPE)) != null) {
            return $$3;
        }
        return this.defaultType;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.defaultType.requiredFeatures();
    }

    public Optional<Mob> spawnOffspringFromSpawnEgg(Player $$0, Mob $$1, EntityType<? extends Mob> $$2, ServerLevel $$3, Vec3 $$4, ItemStack $$5) {
        Mob $$7;
        if (!this.spawnsEntity($$3.registryAccess(), $$5, $$2)) {
            return Optional.empty();
        }
        if ($$1 instanceof AgeableMob) {
            AgeableMob $$6 = ((AgeableMob)$$1).getBreedOffspring($$3, (AgeableMob)$$1);
        } else {
            $$7 = $$2.create($$3, EntitySpawnReason.SPAWN_ITEM_USE);
        }
        if ($$7 == null) {
            return Optional.empty();
        }
        $$7.setBaby(true);
        if (!$$7.isBaby()) {
            return Optional.empty();
        }
        $$7.snapTo($$4.x(), $$4.y(), $$4.z(), 0.0f, 0.0f);
        $$7.applyComponentsFromItemStack($$5);
        $$3.addFreshEntityWithPassengers($$7);
        $$5.consume(1, $$0);
        return Optional.of($$7);
    }

    @Override
    public boolean shouldPrintOpWarning(ItemStack $$0, @Nullable Player $$1) {
        CustomData $$2;
        if ($$1 != null && $$1.getPermissionLevel() >= 2 && ($$2 = $$0.get(DataComponents.ENTITY_DATA)) != null) {
            EntityType<?> $$3 = $$2.parseEntityType($$1.level().registryAccess(), Registries.ENTITY_TYPE);
            return $$3 != null && $$3.onlyOpCanSetNbt();
        }
        return false;
    }
}

