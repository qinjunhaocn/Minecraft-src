/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.monster;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllager
extends Raider {
    protected AbstractIllager(EntityType<? extends AbstractIllager> $$0, Level $$1) {
        super((EntityType<? extends Raider>)$$0, $$1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public IllagerArmPose getArmPose() {
        return IllagerArmPose.CROSSED;
    }

    @Override
    public boolean canAttack(LivingEntity $$0) {
        if ($$0 instanceof AbstractVillager && $$0.isBaby()) {
            return false;
        }
        return super.canAttack($$0);
    }

    @Override
    protected boolean considersEntityAsAlly(Entity $$0) {
        if (super.considersEntityAsAlly($$0)) {
            return true;
        }
        if ($$0.getType().is(EntityTypeTags.ILLAGER_FRIENDS)) {
            return this.getTeam() == null && $$0.getTeam() == null;
        }
        return false;
    }

    public static final class IllagerArmPose
    extends Enum<IllagerArmPose> {
        public static final /* enum */ IllagerArmPose CROSSED = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose ATTACKING = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose SPELLCASTING = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose BOW_AND_ARROW = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose CROSSBOW_HOLD = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose CROSSBOW_CHARGE = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose CELEBRATING = new IllagerArmPose();
        public static final /* enum */ IllagerArmPose NEUTRAL = new IllagerArmPose();
        private static final /* synthetic */ IllagerArmPose[] $VALUES;

        public static IllagerArmPose[] values() {
            return (IllagerArmPose[])$VALUES.clone();
        }

        public static IllagerArmPose valueOf(String $$0) {
            return Enum.valueOf(IllagerArmPose.class, $$0);
        }

        private static /* synthetic */ IllagerArmPose[] a() {
            return new IllagerArmPose[]{CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL};
        }

        static {
            $VALUES = IllagerArmPose.a();
        }
    }

    protected class RaiderOpenDoorGoal
    extends OpenDoorGoal {
        public RaiderOpenDoorGoal(Raider $$1) {
            super($$1, false);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && AbstractIllager.this.hasActiveRaid();
        }
    }
}

