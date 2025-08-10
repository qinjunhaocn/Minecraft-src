/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;

public interface ServerEntityGetter
extends EntityGetter {
    public ServerLevel getLevel();

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, LivingEntity $$1) {
        return this.getNearestEntity(this.players(), $$0, $$1, $$1.getX(), $$1.getY(), $$1.getZ());
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, LivingEntity $$1, double $$2, double $$3, double $$4) {
        return this.getNearestEntity(this.players(), $$0, $$1, $$2, $$3, $$4);
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, double $$1, double $$2, double $$3) {
        return this.getNearestEntity(this.players(), $$0, null, $$1, $$2, $$3);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(Class<? extends T> $$02, TargetingConditions $$1, @Nullable LivingEntity $$2, double $$3, double $$4, double $$5, AABB $$6) {
        return (T)this.getNearestEntity(this.getEntitiesOfClass($$02, $$6, $$0 -> true), $$1, $$2, $$3, $$4, $$5);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(List<? extends T> $$0, TargetingConditions $$1, @Nullable LivingEntity $$2, double $$3, double $$4, double $$5) {
        double $$6 = -1.0;
        LivingEntity $$7 = null;
        for (LivingEntity $$8 : $$0) {
            if (!$$1.test(this.getLevel(), $$2, $$8)) continue;
            double $$9 = $$8.distanceToSqr($$3, $$4, $$5);
            if ($$6 != -1.0 && !($$9 < $$6)) continue;
            $$6 = $$9;
            $$7 = $$8;
        }
        return (T)$$7;
    }

    default public List<Player> getNearbyPlayers(TargetingConditions $$0, LivingEntity $$1, AABB $$2) {
        ArrayList<Player> $$3 = new ArrayList<Player>();
        for (Player player : this.players()) {
            if (!$$2.contains(player.getX(), player.getY(), player.getZ()) || !$$0.test(this.getLevel(), $$1, player)) continue;
            $$3.add(player);
        }
        return $$3;
    }

    default public <T extends LivingEntity> List<T> getNearbyEntities(Class<T> $$02, TargetingConditions $$1, LivingEntity $$2, AABB $$3) {
        List<LivingEntity> $$4 = this.getEntitiesOfClass($$02, $$3, $$0 -> true);
        ArrayList<LivingEntity> $$5 = new ArrayList<LivingEntity>();
        for (LivingEntity $$6 : $$4) {
            if (!$$1.test(this.getLevel(), $$2, $$6)) continue;
            $$5.add($$6);
        }
        return $$5;
    }
}

