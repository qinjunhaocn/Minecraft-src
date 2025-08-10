/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CraftPlanksTutorialStep
implements TutorialStepInstance {
    private static final int HINT_DELAY = 1200;
    private static final Component CRAFT_TITLE = Component.translatable("tutorial.craft_planks.title");
    private static final Component CRAFT_DESCRIPTION = Component.translatable("tutorial.craft_planks.description");
    private final Tutorial tutorial;
    @Nullable
    private TutorialToast toast;
    private int timeWaiting;

    public CraftPlanksTutorialStep(Tutorial $$0) {
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
            if ($$1.getInventory().contains(ItemTags.PLANKS)) {
                this.tutorial.setStep(TutorialSteps.NONE);
                return;
            }
            if (CraftPlanksTutorialStep.hasCraftedPlanksPreviously($$1, ItemTags.PLANKS)) {
                this.tutorial.setStep(TutorialSteps.NONE);
                return;
            }
        }
        if (this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast($$0.font, TutorialToast.Icons.WOODEN_PLANKS, CRAFT_TITLE, CRAFT_DESCRIPTION, false);
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
    public void onGetItem(ItemStack $$0) {
        if ($$0.is(ItemTags.PLANKS)) {
            this.tutorial.setStep(TutorialSteps.NONE);
        }
    }

    public static boolean hasCraftedPlanksPreviously(LocalPlayer $$0, TagKey<Item> $$1) {
        for (Holder<Item> $$2 : BuiltInRegistries.ITEM.getTagOrEmpty($$1)) {
            if ($$0.getStats().getValue(Stats.ITEM_CRAFTED.get($$2.value())) <= 0) continue;
            return true;
        }
        return false;
    }
}

