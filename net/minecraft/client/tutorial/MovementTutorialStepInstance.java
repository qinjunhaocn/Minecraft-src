/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;

public class MovementTutorialStepInstance
implements TutorialStepInstance {
    private static final int MINIMUM_TIME_MOVED = 40;
    private static final int MINIMUM_TIME_LOOKED = 40;
    private static final int MOVE_HINT_DELAY = 100;
    private static final int LOOK_HINT_DELAY = 20;
    private static final int INCOMPLETE = -1;
    private static final Component MOVE_TITLE = Component.a("tutorial.move.title", Tutorial.key("forward"), Tutorial.key("left"), Tutorial.key("back"), Tutorial.key("right"));
    private static final Component MOVE_DESCRIPTION = Component.a("tutorial.move.description", Tutorial.key("jump"));
    private static final Component LOOK_TITLE = Component.translatable("tutorial.look.title");
    private static final Component LOOK_DESCRIPTION = Component.translatable("tutorial.look.description");
    private final Tutorial tutorial;
    @Nullable
    private TutorialToast moveToast;
    @Nullable
    private TutorialToast lookToast;
    private int timeWaiting;
    private int timeMoved;
    private int timeLooked;
    private boolean moved;
    private boolean turned;
    private int moveCompleted = -1;
    private int lookCompleted = -1;

    public MovementTutorialStepInstance(Tutorial $$0) {
        this.tutorial = $$0;
    }

    @Override
    public void tick() {
        ++this.timeWaiting;
        if (this.moved) {
            ++this.timeMoved;
            this.moved = false;
        }
        if (this.turned) {
            ++this.timeLooked;
            this.turned = false;
        }
        if (this.moveCompleted == -1 && this.timeMoved > 40) {
            if (this.moveToast != null) {
                this.moveToast.hide();
                this.moveToast = null;
            }
            this.moveCompleted = this.timeWaiting;
        }
        if (this.lookCompleted == -1 && this.timeLooked > 40) {
            if (this.lookToast != null) {
                this.lookToast.hide();
                this.lookToast = null;
            }
            this.lookCompleted = this.timeWaiting;
        }
        if (this.moveCompleted != -1 && this.lookCompleted != -1) {
            if (this.tutorial.isSurvival()) {
                this.tutorial.setStep(TutorialSteps.FIND_TREE);
            } else {
                this.tutorial.setStep(TutorialSteps.NONE);
            }
        }
        if (this.moveToast != null) {
            this.moveToast.updateProgress((float)this.timeMoved / 40.0f);
        }
        if (this.lookToast != null) {
            this.lookToast.updateProgress((float)this.timeLooked / 40.0f);
        }
        if (this.timeWaiting >= 100) {
            Minecraft $$0 = this.tutorial.getMinecraft();
            if (this.moveCompleted == -1 && this.moveToast == null) {
                this.moveToast = new TutorialToast($$0.font, TutorialToast.Icons.MOVEMENT_KEYS, MOVE_TITLE, MOVE_DESCRIPTION, true);
                $$0.getToastManager().addToast(this.moveToast);
            } else if (this.moveCompleted != -1 && this.timeWaiting - this.moveCompleted >= 20 && this.lookCompleted == -1 && this.lookToast == null) {
                this.lookToast = new TutorialToast($$0.font, TutorialToast.Icons.MOUSE, LOOK_TITLE, LOOK_DESCRIPTION, true);
                $$0.getToastManager().addToast(this.lookToast);
            }
        }
    }

    @Override
    public void clear() {
        if (this.moveToast != null) {
            this.moveToast.hide();
            this.moveToast = null;
        }
        if (this.lookToast != null) {
            this.lookToast.hide();
            this.lookToast = null;
        }
    }

    @Override
    public void onInput(ClientInput $$0) {
        if ($$0.keyPresses.forward() || $$0.keyPresses.backward() || $$0.keyPresses.left() || $$0.keyPresses.right() || $$0.keyPresses.jump()) {
            this.moved = true;
        }
    }

    @Override
    public void onMouse(double $$0, double $$1) {
        if (Math.abs($$0) > 0.01 || Math.abs($$1) > 0.01) {
            this.turned = true;
        }
    }
}

