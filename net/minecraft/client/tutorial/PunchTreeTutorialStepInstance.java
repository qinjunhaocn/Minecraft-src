/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.FindTreeTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PunchTreeTutorialStepInstance
implements TutorialStepInstance {
    private static final int HINT_DELAY = 600;
    private static final Component TITLE = Component.translatable("tutorial.punch_tree.title");
    private static final Component DESCRIPTION = Component.a("tutorial.punch_tree.description", Tutorial.key("attack"));
    private final Tutorial tutorial;
    @Nullable
    private TutorialToast toast;
    private int timeWaiting;
    private int resetCount;

    public PunchTreeTutorialStepInstance(Tutorial $$0) {
        this.tutorial = $$0;
    }

    @Override
    public void tick() {
        LocalPlayer $$1;
        ++this.timeWaiting;
        if (!this.tutorial.isSurvival()) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        Minecraft $$0 = this.tutorial.getMinecraft();
        if (this.timeWaiting == 1 && ($$1 = $$0.player) != null) {
            if ($$1.getInventory().contains(ItemTags.LOGS)) {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepInstance.hasPunchedTreesPreviously($$1)) {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                return;
            }
        }
        if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast($$0.font, TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
            $$0.getToastManager().addToast(this.toast);
        }
    }

    @Override
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onDestroyBlock(ClientLevel $$0, BlockPos $$1, BlockState $$2, float $$3) {
        boolean $$4 = $$2.is(BlockTags.LOGS);
        if ($$4 && $$3 > 0.0f) {
            if (this.toast != null) {
                this.toast.updateProgress($$3);
            }
            if ($$3 >= 1.0f) {
                this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
            }
        } else if (this.toast != null) {
            this.toast.updateProgress(0.0f);
        } else if ($$4) {
            ++this.resetCount;
        }
    }

    @Override
    public void onGetItem(ItemStack $$0) {
        if ($$0.is(ItemTags.LOGS)) {
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            return;
        }
    }
}

