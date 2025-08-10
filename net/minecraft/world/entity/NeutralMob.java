/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface NeutralMob {
    public static final String TAG_ANGER_TIME = "AngerTime";
    public static final String TAG_ANGRY_AT = "AngryAt";

    public int getRemainingPersistentAngerTime();

    public void setRemainingPersistentAngerTime(int var1);

    @Nullable
    public UUID getPersistentAngerTarget();

    public void setPersistentAngerTarget(@Nullable UUID var1);

    public void startPersistentAngerTimer();

    default public void addPersistentAngerSaveData(ValueOutput $$0) {
        $$0.putInt(TAG_ANGER_TIME, this.getRemainingPersistentAngerTime());
        $$0.storeNullable(TAG_ANGRY_AT, UUIDUtil.CODEC, this.getPersistentAngerTarget());
    }

    /*
     * WARNING - void declaration
     */
    default public void readPersistentAngerSaveData(Level $$0, ValueInput $$1) {
        void $$3;
        Entity $$5;
        this.setRemainingPersistentAngerTime($$1.getIntOr(TAG_ANGER_TIME, 0));
        if (!($$0 instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$2 = (ServerLevel)$$0;
        UUID $$4 = $$1.read(TAG_ANGRY_AT, UUIDUtil.CODEC).orElse(null);
        this.setPersistentAngerTarget($$4);
        Entity entity = $$5 = $$4 != null ? $$3.getEntity($$4) : null;
        if ($$5 instanceof LivingEntity) {
            LivingEntity $$6 = (LivingEntity)$$5;
            this.setTarget($$6);
        }
    }

    default public void updatePersistentAnger(ServerLevel $$0, boolean $$1) {
        LivingEntity $$2 = this.getTarget();
        UUID $$3 = this.getPersistentAngerTarget();
        if (($$2 == null || $$2.isDeadOrDying()) && $$3 != null && $$0.getEntity($$3) instanceof Mob) {
            this.stopBeingAngry();
            return;
        }
        if ($$2 != null && !Objects.equals($$3, $$2.getUUID())) {
            this.setPersistentAngerTarget($$2.getUUID());
            this.startPersistentAngerTimer();
        }
        if (!(this.getRemainingPersistentAngerTime() <= 0 || $$2 != null && $$2.getType() == EntityType.PLAYER && $$1)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
                this.stopBeingAngry();
            }
        }
    }

    default public boolean isAngryAt(LivingEntity $$0, ServerLevel $$1) {
        if (!this.canAttack($$0)) {
            return false;
        }
        if ($$0.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers($$1)) {
            return true;
        }
        return $$0.getUUID().equals(this.getPersistentAngerTarget());
    }

    default public boolean isAngryAtAllPlayers(ServerLevel $$0) {
        return $$0.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    default public boolean isAngry() {
        return this.getRemainingPersistentAngerTime() > 0;
    }

    default public void playerDied(ServerLevel $$0, Player $$1) {
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            return;
        }
        if (!$$1.getUUID().equals(this.getPersistentAngerTarget())) {
            return;
        }
        this.stopBeingAngry();
    }

    default public void forgetCurrentTargetAndRefreshUniversalAnger() {
        this.stopBeingAngry();
        this.startPersistentAngerTimer();
    }

    default public void stopBeingAngry() {
        this.setLastHurtByMob(null);
        this.setPersistentAngerTarget(null);
        this.setTarget(null);
        this.setRemainingPersistentAngerTime(0);
    }

    @Nullable
    public LivingEntity getLastHurtByMob();

    public void setLastHurtByMob(@Nullable LivingEntity var1);

    public void setTarget(@Nullable LivingEntity var1);

    public boolean canAttack(LivingEntity var1);

    @Nullable
    public LivingEntity getTarget();
}

