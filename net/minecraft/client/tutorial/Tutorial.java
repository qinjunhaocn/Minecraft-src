/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class Tutorial {
    private final Minecraft minecraft;
    @Nullable
    private TutorialStepInstance instance;

    public Tutorial(Minecraft $$0, Options $$1) {
        this.minecraft = $$0;
    }

    public void onInput(ClientInput $$0) {
        if (this.instance != null) {
            this.instance.onInput($$0);
        }
    }

    public void onMouse(double $$0, double $$1) {
        if (this.instance != null) {
            this.instance.onMouse($$0, $$1);
        }
    }

    public void onLookAt(@Nullable ClientLevel $$0, @Nullable HitResult $$1) {
        if (this.instance != null && $$1 != null && $$0 != null) {
            this.instance.onLookAt($$0, $$1);
        }
    }

    public void onDestroyBlock(ClientLevel $$0, BlockPos $$1, BlockState $$2, float $$3) {
        if (this.instance != null) {
            this.instance.onDestroyBlock($$0, $$1, $$2, $$3);
        }
    }

    public void onOpenInventory() {
        if (this.instance != null) {
            this.instance.onOpenInventory();
        }
    }

    public void onGetItem(ItemStack $$0) {
        if (this.instance != null) {
            this.instance.onGetItem($$0);
        }
    }

    public void stop() {
        if (this.instance == null) {
            return;
        }
        this.instance.clear();
        this.instance = null;
    }

    public void start() {
        if (this.instance != null) {
            this.stop();
        }
        this.instance = this.minecraft.options.tutorialStep.create(this);
    }

    public void tick() {
        if (this.instance != null) {
            if (this.minecraft.level != null) {
                this.instance.tick();
            } else {
                this.stop();
            }
        } else if (this.minecraft.level != null) {
            this.start();
        }
    }

    public void setStep(TutorialSteps $$0) {
        this.minecraft.options.tutorialStep = $$0;
        this.minecraft.options.save();
        if (this.instance != null) {
            this.instance.clear();
            this.instance = $$0.create(this);
        }
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public boolean isSurvival() {
        if (this.minecraft.gameMode == null) {
            return false;
        }
        return this.minecraft.gameMode.getPlayerMode() == GameType.SURVIVAL;
    }

    public static Component key(String $$0) {
        return Component.keybind("key." + $$0).withStyle(ChatFormatting.BOLD);
    }

    public void onInventoryAction(ItemStack $$0, ItemStack $$1, ClickAction $$2) {
    }
}

