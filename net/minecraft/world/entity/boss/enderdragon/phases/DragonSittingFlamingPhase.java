/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;

public class DragonSittingFlamingPhase
extends AbstractDragonSittingPhase {
    private static final int FLAME_DURATION = 200;
    private static final int SITTING_FLAME_ATTACKS_COUNT = 4;
    private static final int WARMUP_TIME = 10;
    private int flameTicks;
    private int flameCount;
    @Nullable
    private AreaEffectCloud flame;

    public DragonSittingFlamingPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doClientTick() {
        ++this.flameTicks;
        if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
            Vec3 $$0 = this.dragon.getHeadLookVector(1.0f).normalize();
            $$0.yRot(-0.7853982f);
            double $$1 = this.dragon.head.getX();
            double $$2 = this.dragon.head.getY(0.5);
            double $$3 = this.dragon.head.getZ();
            for (int $$4 = 0; $$4 < 8; ++$$4) {
                double $$5 = $$1 + this.dragon.getRandom().nextGaussian() / 2.0;
                double $$6 = $$2 + this.dragon.getRandom().nextGaussian() / 2.0;
                double $$7 = $$3 + this.dragon.getRandom().nextGaussian() / 2.0;
                for (int $$8 = 0; $$8 < 6; ++$$8) {
                    this.dragon.level().addParticle(ParticleTypes.DRAGON_BREATH, $$5, $$6, $$7, -$$0.x * (double)0.08f * (double)$$8, -$$0.y * (double)0.6f, -$$0.z * (double)0.08f * (double)$$8);
                }
                $$0.yRot(0.19634955f);
            }
        }
    }

    @Override
    public void doServerTick(ServerLevel $$0) {
        ++this.flameTicks;
        if (this.flameTicks >= 200) {
            if (this.flameCount >= 4) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
            } else {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
            }
        } else if (this.flameTicks == 10) {
            double $$5;
            Vec3 $$1 = new Vec3(this.dragon.head.getX() - this.dragon.getX(), 0.0, this.dragon.head.getZ() - this.dragon.getZ()).normalize();
            float $$2 = 5.0f;
            double $$3 = this.dragon.head.getX() + $$1.x * 5.0 / 2.0;
            double $$4 = this.dragon.head.getZ() + $$1.z * 5.0 / 2.0;
            double $$6 = $$5 = this.dragon.head.getY(0.5);
            BlockPos.MutableBlockPos $$7 = new BlockPos.MutableBlockPos($$3, $$6, $$4);
            while ($$0.isEmptyBlock($$7)) {
                if (($$6 -= 1.0) < 0.0) {
                    $$6 = $$5;
                    break;
                }
                $$7.set($$3, $$6, $$4);
            }
            $$6 = Mth.floor($$6) + 1;
            this.flame = new AreaEffectCloud($$0, $$3, $$6, $$4);
            this.flame.setOwner(this.dragon);
            this.flame.setRadius(5.0f);
            this.flame.setDuration(200);
            this.flame.setCustomParticle(ParticleTypes.DRAGON_BREATH);
            this.flame.setPotionDurationScale(0.25f);
            this.flame.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE));
            $$0.addFreshEntity(this.flame);
        }
    }

    @Override
    public void begin() {
        this.flameTicks = 0;
        ++this.flameCount;
    }

    @Override
    public void end() {
        if (this.flame != null) {
            this.flame.discard();
            this.flame = null;
        }
    }

    public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
        return EnderDragonPhase.SITTING_FLAMING;
    }

    public void resetFlameCount() {
        this.flameCount = 0;
    }
}

