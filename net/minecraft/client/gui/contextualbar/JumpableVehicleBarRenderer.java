/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.contextualbar;

import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PlayerRideableJumping;

public class JumpableVehicleBarRenderer
implements ContextualBarRenderer {
    private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
    private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_cooldown");
    private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");
    private final Minecraft minecraft;
    private final PlayerRideableJumping playerJumpableVehicle;

    public JumpableVehicleBarRenderer(Minecraft $$0) {
        this.minecraft = $$0;
        this.playerJumpableVehicle = Objects.requireNonNull($$0.player).jumpableVehicle();
    }

    @Override
    public void renderBackground(GuiGraphics $$0, DeltaTracker $$1) {
        int $$2 = this.left(this.minecraft.getWindow());
        int $$3 = this.top(this.minecraft.getWindow());
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_BACKGROUND_SPRITE, $$2, $$3, 182, 5);
        if (this.playerJumpableVehicle.getJumpCooldown() > 0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_COOLDOWN_SPRITE, $$2, $$3, 182, 5);
            return;
        }
        int $$4 = (int)(this.minecraft.player.getJumpRidingScale() * 183.0f);
        if ($$4 > 0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, $$2, $$3, $$4, 5);
        }
    }

    @Override
    public void render(GuiGraphics $$0, DeltaTracker $$1) {
    }
}

