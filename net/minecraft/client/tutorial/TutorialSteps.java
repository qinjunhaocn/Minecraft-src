/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.minecraft.client.tutorial.CompletedTutorialStepInstance;
import net.minecraft.client.tutorial.CraftPlanksTutorialStep;
import net.minecraft.client.tutorial.FindTreeTutorialStepInstance;
import net.minecraft.client.tutorial.MovementTutorialStepInstance;
import net.minecraft.client.tutorial.OpenInventoryTutorialStep;
import net.minecraft.client.tutorial.PunchTreeTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;

public final class TutorialSteps
extends Enum<TutorialSteps> {
    public static final /* enum */ TutorialSteps MOVEMENT = new TutorialSteps("movement", MovementTutorialStepInstance::new);
    public static final /* enum */ TutorialSteps FIND_TREE = new TutorialSteps("find_tree", FindTreeTutorialStepInstance::new);
    public static final /* enum */ TutorialSteps PUNCH_TREE = new TutorialSteps("punch_tree", PunchTreeTutorialStepInstance::new);
    public static final /* enum */ TutorialSteps OPEN_INVENTORY = new TutorialSteps("open_inventory", OpenInventoryTutorialStep::new);
    public static final /* enum */ TutorialSteps CRAFT_PLANKS = new TutorialSteps("craft_planks", CraftPlanksTutorialStep::new);
    public static final /* enum */ TutorialSteps NONE = new TutorialSteps("none", CompletedTutorialStepInstance::new);
    private final String name;
    private final Function<Tutorial, ? extends TutorialStepInstance> constructor;
    private static final /* synthetic */ TutorialSteps[] $VALUES;

    public static TutorialSteps[] values() {
        return (TutorialSteps[])$VALUES.clone();
    }

    public static TutorialSteps valueOf(String $$0) {
        return Enum.valueOf(TutorialSteps.class, $$0);
    }

    private <T extends TutorialStepInstance> TutorialSteps(String $$0, Function<Tutorial, T> $$1) {
        this.name = $$0;
        this.constructor = $$1;
    }

    public TutorialStepInstance create(Tutorial $$0) {
        return this.constructor.apply($$0);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialSteps getByName(String $$0) {
        for (TutorialSteps $$1 : TutorialSteps.values()) {
            if (!$$1.name.equals($$0)) continue;
            return $$1;
        }
        return NONE;
    }

    private static /* synthetic */ TutorialSteps[] b() {
        return new TutorialSteps[]{MOVEMENT, FIND_TREE, PUNCH_TREE, OPEN_INVENTORY, CRAFT_PLANKS, NONE};
    }

    static {
        $VALUES = TutorialSteps.b();
    }
}

