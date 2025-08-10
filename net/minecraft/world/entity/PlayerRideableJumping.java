/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import net.minecraft.world.entity.PlayerRideable;

public interface PlayerRideableJumping
extends PlayerRideable {
    public void onPlayerJump(int var1);

    public boolean canJump();

    public void handleStartJump(int var1);

    public void handleStopJump();

    default public int getJumpCooldown() {
        return 0;
    }
}

