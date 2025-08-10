/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase
extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;
    private int time;

    public DragonDeathPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            float $$0 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            float $$1 = (this.dragon.getRandom().nextFloat() - 0.5f) * 4.0f;
            float $$2 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            this.dragon.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getX() + (double)$$0, this.dragon.getY() + 2.0 + (double)$$1, this.dragon.getZ() + (double)$$2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void doServerTick(ServerLevel $$0) {
        double $$2;
        ++this.time;
        if (this.targetLocation == null) {
            BlockPos $$1 = $$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.dragon.getFightOrigin()));
            this.targetLocation = Vec3.atBottomCenterOf($$1);
        }
        if (($$2 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ())) < 100.0 || $$2 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        } else {
            this.dragon.setHealth(1.0f);
        }
    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }

    @Override
    public float getFlySpeed() {
        return 3.0f;
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonDeathPhase> getPhase() {
        return EnderDragonPhase.DYING;
    }
}

