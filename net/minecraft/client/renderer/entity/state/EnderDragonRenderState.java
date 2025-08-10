/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory;
import net.minecraft.world.phys.Vec3;

public class EnderDragonRenderState
extends EntityRenderState {
    public float flapTime;
    public float deathTime;
    public boolean hasRedOverlay;
    @Nullable
    public Vec3 beamOffset;
    public boolean isLandingOrTakingOff;
    public boolean isSitting;
    public double distanceToEgg;
    public float partialTicks;
    public final DragonFlightHistory flightHistory = new DragonFlightHistory();

    public DragonFlightHistory.Sample getHistoricalPos(int $$0) {
        return this.flightHistory.get($$0, this.partialTicks);
    }

    public float getHeadPartYOffset(int $$0, DragonFlightHistory.Sample $$1, DragonFlightHistory.Sample $$2) {
        double $$6;
        if (this.isLandingOrTakingOff) {
            double $$3 = (double)$$0 / Math.max(this.distanceToEgg / 4.0, 1.0);
        } else if (this.isSitting) {
            double $$4 = $$0;
        } else if ($$0 == 6) {
            double $$5 = 0.0;
        } else {
            $$6 = $$2.y() - $$1.y();
        }
        return (float)$$6;
    }
}

