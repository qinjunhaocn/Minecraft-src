/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonLandingPhase
extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;

    public DragonLandingPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doClientTick() {
        Vec3 $$0 = this.dragon.getHeadLookVector(1.0f).normalize();
        $$0.yRot(-0.7853982f);
        double $$1 = this.dragon.head.getX();
        double $$2 = this.dragon.head.getY(0.5);
        double $$3 = this.dragon.head.getZ();
        for (int $$4 = 0; $$4 < 8; ++$$4) {
            RandomSource $$5 = this.dragon.getRandom();
            double $$6 = $$1 + $$5.nextGaussian() / 2.0;
            double $$7 = $$2 + $$5.nextGaussian() / 2.0;
            double $$8 = $$3 + $$5.nextGaussian() / 2.0;
            Vec3 $$9 = this.dragon.getDeltaMovement();
            this.dragon.level().addParticle(ParticleTypes.DRAGON_BREATH, $$6, $$7, $$8, -$$0.x * (double)0.08f + $$9.x, -$$0.y * (double)0.3f + $$9.y, -$$0.z * (double)0.08f + $$9.z);
            $$0.yRot(0.19634955f);
        }
    }

    @Override
    public void doServerTick(ServerLevel $$0) {
        if (this.targetLocation == null) {
            this.targetLocation = Vec3.atBottomCenterOf($$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.dragon.getFightOrigin())));
        }
        if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
            this.dragon.getPhaseManager().getPhase(EnderDragonPhase.SITTING_FLAMING).resetFlameCount();
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
        }
    }

    @Override
    public float getFlySpeed() {
        return 1.5f;
    }

    @Override
    public float getTurnSpeed() {
        float $$0 = (float)this.dragon.getDeltaMovement().horizontalDistance() + 1.0f;
        float $$1 = Math.min($$0, 40.0f);
        return $$1 / $$0;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonLandingPhase> getPhase() {
        return EnderDragonPhase.LANDING;
    }
}

