/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AbstractSchoolingFish
extends AbstractFish {
    @Nullable
    private AbstractSchoolingFish leader;
    private int schoolSize = 1;

    public AbstractSchoolingFish(EntityType<? extends AbstractSchoolingFish> $$0, Level $$1) {
        super((EntityType<? extends AbstractFish>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return this.getMaxSchoolSize();
    }

    public int getMaxSchoolSize() {
        return super.getMaxSpawnClusterSize();
    }

    @Override
    protected boolean canRandomSwim() {
        return !this.isFollower();
    }

    public boolean isFollower() {
        return this.leader != null && this.leader.isAlive();
    }

    public AbstractSchoolingFish startFollowing(AbstractSchoolingFish $$0) {
        this.leader = $$0;
        $$0.addFollower();
        return $$0;
    }

    public void stopFollowing() {
        this.leader.removeFollower();
        this.leader = null;
    }

    private void addFollower() {
        ++this.schoolSize;
    }

    private void removeFollower() {
        --this.schoolSize;
    }

    public boolean canBeFollowed() {
        return this.hasFollowers() && this.schoolSize < this.getMaxSchoolSize();
    }

    @Override
    public void tick() {
        List<?> $$0;
        super.tick();
        if (this.hasFollowers() && this.level().random.nextInt(200) == 1 && ($$0 = this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(8.0, 8.0, 8.0))).size() <= 1) {
            this.schoolSize = 1;
        }
    }

    public boolean hasFollowers() {
        return this.schoolSize > 1;
    }

    public boolean inRangeOfLeader() {
        return this.distanceToSqr(this.leader) <= 121.0;
    }

    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, 1.0);
        }
    }

    public void addFollowers(Stream<? extends AbstractSchoolingFish> $$02) {
        $$02.limit(this.getMaxSchoolSize() - this.schoolSize).filter($$0 -> $$0 != this).forEach($$0 -> $$0.startFollowing(this));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, EntitySpawnReason $$2, @Nullable SpawnGroupData $$3) {
        super.finalizeSpawn($$0, $$1, $$2, $$3);
        if ($$3 == null) {
            $$3 = new SchoolSpawnGroupData(this);
        } else {
            this.startFollowing(((SchoolSpawnGroupData)$$3).leader);
        }
        return $$3;
    }

    public static class SchoolSpawnGroupData
    implements SpawnGroupData {
        public final AbstractSchoolingFish leader;

        public SchoolSpawnGroupData(AbstractSchoolingFish $$0) {
            this.leader = $$0;
        }
    }
}

