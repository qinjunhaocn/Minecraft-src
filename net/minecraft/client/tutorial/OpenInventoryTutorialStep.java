/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;

public class OpenInventoryTutorialStep
implements TutorialStepInstance {
    private static final int HINT_DELAY = 600;
    private static final Component TITLE = Component.translatable("tutorial.open_inventory.title");
    private static final Component DESCRIPTION = Component.a("tutorial.open_inventory.description", Tutorial.key("inventory"));
    private final Tutorial tutorial;
    @Nullable
    private TutorialToast toast;
    private int timeWaiting;

    public OpenInventoryTutorialStep(Tutorial $$0) {
        this.tutorial = $$0;
    }

    @Override
    public void tick() {
        ++this.timeWaiting;
        if (!this.tutorial.isSurvival()) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        if (this.timeWaiting >= 600 && this.toast == null) {
            Minecraft $$0 = this.tutorial.getMinecraft();
            this.toast = new TutorialToast($$0.font, TutorialToast.Icons.RECIPE_BOOK, TITLE, DESCRIPTION, false);
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
    public void onOpenInventory() {
        this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
    }
}

