/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;

public abstract class FogEnvironment {
    public abstract void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6);

    public boolean providesColor() {
        return true;
    }

    public int getBaseColor(ClientLevel $$0, Camera $$1, int $$2, float $$3) {
        return -1;
    }

    public boolean modifiesDarkness() {
        return false;
    }

    public float getModifiedDarkness(LivingEntity $$0, float $$1, float $$2) {
        return $$1;
    }

    public abstract boolean isApplicable(@Nullable FogType var1, Entity var2);

    public void onNotApplicable() {
    }
}

