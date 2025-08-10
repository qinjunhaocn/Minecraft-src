/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.contextualbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;

public class LocatorBarRenderer
implements ContextualBarRenderer {
    private static final ResourceLocation LOCATOR_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("hud/locator_bar_background");
    private static final ResourceLocation LOCATOR_BAR_ARROW_UP = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up");
    private static final ResourceLocation LOCATOR_BAR_ARROW_DOWN = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down");
    private static final int DOT_SIZE = 9;
    private static final int VISIBLE_DEGREE_RANGE = 60;
    private static final int ARROW_WIDTH = 7;
    private static final int ARROW_HEIGHT = 5;
    private static final int ARROW_LEFT = 1;
    private static final int ARROW_PADDING = 1;
    private final Minecraft minecraft;

    public LocatorBarRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void renderBackground(GuiGraphics $$0, DeltaTracker $$1) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, LOCATOR_BAR_BACKGROUND, this.left(this.minecraft.getWindow()), this.top(this.minecraft.getWindow()), 182, 5);
    }

    @Override
    public void render(GuiGraphics $$0, DeltaTracker $$1) {
        int $$2 = this.top(this.minecraft.getWindow());
        Level $$32 = this.minecraft.cameraEntity.level();
        this.minecraft.player.connection.getWaypointManager().forEachWaypoint(this.minecraft.cameraEntity, $$3 -> {
            if ($$3.id().left().map($$0 -> $$0.equals(this.minecraft.cameraEntity.getUUID())).orElse(false).booleanValue()) {
                return;
            }
            double $$4 = $$3.yawAngleToCamera($$32, this.minecraft.gameRenderer.getMainCamera());
            if ($$4 <= -61.0 || $$4 > 60.0) {
                return;
            }
            int $$5 = Mth.ceil((float)($$0.guiWidth() - 9) / 2.0f);
            Waypoint.Icon $$6 = $$3.icon();
            WaypointStyle $$7 = this.minecraft.getWaypointStyles().get($$6.style);
            float $$8 = Mth.sqrt((float)$$3.distanceSquared(this.minecraft.cameraEntity));
            ResourceLocation $$9 = $$7.sprite($$8);
            int $$10 = $$6.color.orElseGet(() -> (Integer)$$3.id().map($$0 -> ARGB.setBrightness(ARGB.color(255, $$0.hashCode()), 0.9f), $$0 -> ARGB.setBrightness(ARGB.color(255, $$0.hashCode()), 0.9f)));
            int $$11 = (int)($$4 * 173.0 / 2.0 / 60.0);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$9, $$5 + $$11, $$2 - 2, 9, 9, $$10);
            TrackedWaypoint.PitchDirection $$12 = $$3.pitchDirectionToCamera($$32, this.minecraft.gameRenderer);
            if ($$12 != TrackedWaypoint.PitchDirection.NONE) {
                ResourceLocation $$16;
                int $$15;
                if ($$12 == TrackedWaypoint.PitchDirection.DOWN) {
                    int $$13 = 6;
                    ResourceLocation $$14 = LOCATOR_BAR_ARROW_DOWN;
                } else {
                    $$15 = -6;
                    $$16 = LOCATOR_BAR_ARROW_UP;
                }
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$16, $$5 + $$11 + 1, $$2 + $$15, 7, 5);
            }
        });
    }
}

