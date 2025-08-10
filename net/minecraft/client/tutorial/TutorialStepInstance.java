/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface TutorialStepInstance {
    default public void clear() {
    }

    default public void tick() {
    }

    default public void onInput(ClientInput $$0) {
    }

    default public void onMouse(double $$0, double $$1) {
    }

    default public void onLookAt(ClientLevel $$0, HitResult $$1) {
    }

    default public void onDestroyBlock(ClientLevel $$0, BlockPos $$1, BlockState $$2, float $$3) {
    }

    default public void onOpenInventory() {
    }

    default public void onGetItem(ItemStack $$0) {
    }
}

