/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DragonChargePlayerPhase
extends AbstractDragonPhaseInstance {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CHARGE_RECOVERY_TIME = 10;
    @Nullable
    private Vec3 targetLocation;
    private int timeSinceCharge;

    public DragonChargePlayerPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doServerTick(ServerLevel $$0) {
        if (this.targetLocation == null) {
            LOGGER.warn("Aborting charge player as no target was set.");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        double $$1 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if ($$1 < 100.0 || $$1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.timeSinceCharge;
        }
    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.timeSinceCharge = 0;
    }

    public void setTarget(Vec3 $$0) {
        this.targetLocation = $$0;
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

    public EnderDragonPhase<DragonChargePlayerPhase> getPhase() {
        return EnderDragonPhase.CHARGING_PLAYER;
    }
}

