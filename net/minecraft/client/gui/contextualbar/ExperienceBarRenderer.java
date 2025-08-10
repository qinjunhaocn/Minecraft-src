/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.contextualbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public class ExperienceBarRenderer
implements ContextualBarRenderer {
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
    private final Minecraft minecraft;

    public ExperienceBarRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void renderBackground(GuiGraphics $$0, DeltaTracker $$1) {
        LocalPlayer $$2 = this.minecraft.player;
        int $$3 = this.left(this.minecraft.getWindow());
        int $$4 = this.top(this.minecraft.getWindow());
        int $$5 = $$2.getXpNeededForNextLevel();
        if ($$5 > 0) {
            int $$6 = (int)($$2.experienceProgress * 183.0f);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, $$3, $$4, 182, 5);
            if ($$6 > 0) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, $$3, $$4, $$6, 5);
            }
        }
    }

    @Override
    public void render(GuiGraphics $$0, DeltaTracker $$1) {
    }
}

