/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.spectator;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class SpectatorGui
implements SpectatorMenuListener {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final long FADE_OUT_DELAY = 5000L;
    private static final long FADE_OUT_TIME = 2000L;
    private final Minecraft minecraft;
    private long lastSelectionTime;
    @Nullable
    private SpectatorMenu menu;

    public SpectatorGui(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void onHotbarSelected(int $$0) {
        this.lastSelectionTime = Util.getMillis();
        if (this.menu != null) {
            this.menu.selectSlot($$0);
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }

    private float getHotbarAlpha() {
        long $$0 = this.lastSelectionTime - Util.getMillis() + 5000L;
        return Mth.clamp((float)$$0 / 2000.0f, 0.0f, 1.0f);
    }

    public void renderHotbar(GuiGraphics $$0) {
        if (this.menu == null) {
            return;
        }
        float $$1 = this.getHotbarAlpha();
        if ($$1 <= 0.0f) {
            this.menu.exit();
            return;
        }
        int $$2 = $$0.guiWidth() / 2;
        int $$3 = Mth.floor((float)$$0.guiHeight() - 22.0f * $$1);
        SpectatorPage $$4 = this.menu.getCurrentPage();
        this.renderPage($$0, $$1, $$2, $$3, $$4);
    }

    protected void renderPage(GuiGraphics $$0, float $$1, int $$2, int $$3, SpectatorPage $$4) {
        int $$5 = ARGB.white($$1);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, $$2 - 91, $$3, 182, 22, $$5);
        if ($$4.getSelectedSlot() >= 0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_SPRITE, $$2 - 91 - 1 + $$4.getSelectedSlot() * 20, $$3 - 1, 24, 23, $$5);
        }
        for (int $$6 = 0; $$6 < 9; ++$$6) {
            this.renderSlot($$0, $$6, $$0.guiWidth() / 2 - 90 + $$6 * 20 + 2, $$3 + 3, $$1, $$4.getItem($$6));
        }
    }

    private void renderSlot(GuiGraphics $$0, int $$1, int $$2, float $$3, float $$4, SpectatorMenuItem $$5) {
        if ($$5 != SpectatorMenu.EMPTY_SLOT) {
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)$$2, $$3);
            float $$6 = $$5.isEnabled() ? 1.0f : 0.25f;
            $$5.renderIcon($$0, $$6, $$4);
            $$0.pose().popMatrix();
            if ($$4 > 0.0f && $$5.isEnabled()) {
                Component $$7 = this.minecraft.options.keyHotbarSlots[$$1].getTranslatedKeyMessage();
                $$0.drawString(this.minecraft.font, $$7, $$2 + 19 - 2 - this.minecraft.font.width($$7), (int)$$3 + 6 + 3, ARGB.color($$4, -1));
            }
        }
    }

    public void renderAction(GuiGraphics $$0) {
        float $$1 = this.getHotbarAlpha();
        if ($$1 > 0.0f && this.menu != null) {
            SpectatorMenuItem $$2 = this.menu.getSelectedItem();
            Component $$3 = $$2 == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : $$2.getName();
            int $$4 = this.minecraft.font.width($$3);
            int $$5 = ($$0.guiWidth() - $$4) / 2;
            int $$6 = $$0.guiHeight() - 35;
            $$0.drawStringWithBackdrop(this.minecraft.font, $$3, $$5, $$6, $$4, ARGB.color($$1, -1));
        }
    }

    @Override
    public void onSpectatorMenuClosed(SpectatorMenu $$0) {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }

    public boolean isMenuActive() {
        return this.menu != null;
    }

    public void onMouseScrolled(int $$0) {
        int $$1;
        for ($$1 = this.menu.getSelectedSlot() + $$0; !($$1 < 0 || $$1 > 8 || this.menu.getItem($$1) != SpectatorMenu.EMPTY_SLOT && this.menu.getItem($$1).isEnabled()); $$1 += $$0) {
        }
        if ($$1 >= 0 && $$1 <= 8) {
            this.menu.selectSlot($$1);
            this.lastSelectionTime = Util.getMillis();
        }
    }

    public void onMouseMiddleClick() {
        this.lastSelectionTime = Util.getMillis();
        if (this.isMenuActive()) {
            int $$0 = this.menu.getSelectedSlot();
            if ($$0 != -1) {
                this.menu.selectSlot($$0);
            }
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }
}

