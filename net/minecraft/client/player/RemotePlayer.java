/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class RemotePlayer
extends AbstractClientPlayer {
    private Vec3 lerpDeltaMovement = Vec3.ZERO;
    private int lerpDeltaMovementSteps;

    public RemotePlayer(ClientLevel $$0, GameProfile $$1) {
        super($$0, $$1);
        this.noPhysics = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN($$1)) {
            $$1 = 1.0;
        }
        return $$0 < ($$1 *= 64.0 * RemotePlayer.getViewScale()) * $$1;
    }

    @Override
    public boolean hurtClient(DamageSource $$0) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateEntityAnimation(false);
    }

    @Override
    public void aiStep() {
        float $$1;
        if (this.isInterpolating()) {
            this.getInterpolation().interpolate();
        }
        if (this.lerpHeadSteps > 0) {
            this.lerpHeadRotationStep(this.lerpHeadSteps, this.lerpYHeadRot);
            --this.lerpHeadSteps;
        }
        if (this.lerpDeltaMovementSteps > 0) {
            this.addDeltaMovement(new Vec3((this.lerpDeltaMovement.x - this.getDeltaMovement().x) / (double)this.lerpDeltaMovementSteps, (this.lerpDeltaMovement.y - this.getDeltaMovement().y) / (double)this.lerpDeltaMovementSteps, (this.lerpDeltaMovement.z - this.getDeltaMovement().z) / (double)this.lerpDeltaMovementSteps));
            --this.lerpDeltaMovementSteps;
        }
        this.oBob = this.bob;
        this.updateSwingTime();
        if (!this.onGround() || this.isDeadOrDying()) {
            float $$0 = 0.0f;
        } else {
            $$1 = (float)Math.min(0.1, this.getDeltaMovement().horizontalDistance());
        }
        this.bob += ($$1 - this.bob) * 0.4f;
        try (Zone $$2 = Profiler.get().zone("push");){
            this.pushEntities();
        }
    }

    @Override
    public void lerpMotion(double $$0, double $$1, double $$2) {
        this.lerpDeltaMovement = new Vec3($$0, $$1, $$2);
        this.lerpDeltaMovementSteps = this.getType().updateInterval() + 1;
    }

    @Override
    protected void updatePlayerPose() {
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.setOldPosAndRot();
    }
}

