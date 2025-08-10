/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
    public List<Entity> getEntities(@Nullable Entity var1, AABB var2, Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3);

    default public <T extends Entity> List<T> getEntitiesOfClass(Class<T> $$0, AABB $$1, Predicate<? super T> $$2) {
        return this.getEntities(EntityTypeTest.forClass($$0), $$1, $$2);
    }

    public List<? extends Player> players();

    default public List<Entity> getEntities(@Nullable Entity $$0, AABB $$1) {
        return this.getEntities($$0, $$1, EntitySelector.NO_SPECTATORS);
    }

    default public boolean isUnobstructed(@Nullable Entity $$0, VoxelShape $$1) {
        if ($$1.isEmpty()) {
            return true;
        }
        for (Entity $$2 : this.getEntities($$0, $$1.bounds())) {
            if ($$2.isRemoved() || !$$2.blocksBuilding || $$0 != null && $$2.isPassengerOfSameVehicle($$0) || !Shapes.joinIsNotEmpty($$1, Shapes.create($$2.getBoundingBox()), BooleanOp.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getEntitiesOfClass(Class<T> $$0, AABB $$1) {
        return this.getEntitiesOfClass($$0, $$1, EntitySelector.NO_SPECTATORS);
    }

    default public List<VoxelShape> getEntityCollisions(@Nullable Entity $$0, AABB $$1) {
        if ($$1.getSize() < 1.0E-7) {
            return List.of();
        }
        Predicate<Entity> $$2 = $$0 == null ? EntitySelector.CAN_BE_COLLIDED_WITH : EntitySelector.NO_SPECTATORS.and($$0::canCollideWith);
        List<Entity> $$3 = this.getEntities($$0, $$1.inflate(1.0E-7), $$2);
        if ($$3.isEmpty()) {
            return List.of();
        }
        ImmutableList.Builder $$4 = ImmutableList.builderWithExpectedSize($$3.size());
        for (Entity $$5 : $$3) {
            $$4.add(Shapes.create($$5.getBoundingBox()));
        }
        return $$4.build();
    }

    @Nullable
    default public Player getNearestPlayer(double $$0, double $$1, double $$2, double $$3, @Nullable Predicate<Entity> $$4) {
        double $$5 = -1.0;
        Player $$6 = null;
        for (Player player : this.players()) {
            if ($$4 != null && !$$4.test(player)) continue;
            double $$8 = player.distanceToSqr($$0, $$1, $$2);
            if (!($$3 < 0.0) && !($$8 < $$3 * $$3) || $$5 != -1.0 && !($$8 < $$5)) continue;
            $$5 = $$8;
            $$6 = player;
        }
        return $$6;
    }

    @Nullable
    default public Player getNearestPlayer(Entity $$0, double $$1) {
        return this.getNearestPlayer($$0.getX(), $$0.getY(), $$0.getZ(), $$1, false);
    }

    @Nullable
    default public Player getNearestPlayer(double $$0, double $$1, double $$2, double $$3, boolean $$4) {
        Predicate<Entity> $$5 = $$4 ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
        return this.getNearestPlayer($$0, $$1, $$2, $$3, $$5);
    }

    default public boolean hasNearbyAlivePlayer(double $$0, double $$1, double $$2, double $$3) {
        for (Player player : this.players()) {
            if (!EntitySelector.NO_SPECTATORS.test(player) || !EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player)) continue;
            double $$5 = player.distanceToSqr($$0, $$1, $$2);
            if (!($$3 < 0.0) && !($$5 < $$3 * $$3)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public Player getPlayerByUUID(UUID $$0) {
        for (int $$1 = 0; $$1 < this.players().size(); ++$$1) {
            Player $$2 = this.players().get($$1);
            if (!$$0.equals($$2.getUUID())) continue;
            return $$2;
        }
        return null;
    }
}

