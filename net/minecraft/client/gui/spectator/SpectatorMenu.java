/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.RootSpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class SpectatorMenu {
    static final ResourceLocation CLOSE_SPRITE = ResourceLocation.withDefaultNamespace("spectator/close");
    static final ResourceLocation SCROLL_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("spectator/scroll_left");
    static final ResourceLocation SCROLL_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("spectator/scroll_right");
    private static final SpectatorMenuItem CLOSE_ITEM = new CloseSpectatorItem();
    private static final SpectatorMenuItem SCROLL_LEFT = new ScrollMenuItem(-1, true);
    private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new ScrollMenuItem(1, true);
    private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new ScrollMenuItem(1, false);
    private static final int MAX_PER_PAGE = 8;
    static final Component CLOSE_MENU_TEXT = Component.translatable("spectatorMenu.close");
    static final Component PREVIOUS_PAGE_TEXT = Component.translatable("spectatorMenu.previous_page");
    static final Component NEXT_PAGE_TEXT = Component.translatable("spectatorMenu.next_page");
    public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem(){

        @Override
        public void selectItem(SpectatorMenu $$0) {
        }

        @Override
        public Component getName() {
            return CommonComponents.EMPTY;
        }

        @Override
        public void renderIcon(GuiGraphics $$0, float $$1, float $$2) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    private final SpectatorMenuListener listener;
    private SpectatorMenuCategory category = new RootSpectatorMenuCategory();
    private int selectedSlot = -1;
    int page;

    public SpectatorMenu(SpectatorMenuListener $$0) {
        this.listener = $$0;
    }

    public SpectatorMenuItem getItem(int $$0) {
        int $$1 = $$0 + this.page * 6;
        if (this.page > 0 && $$0 == 0) {
            return SCROLL_LEFT;
        }
        if ($$0 == 7) {
            if ($$1 < this.category.getItems().size()) {
                return SCROLL_RIGHT_ENABLED;
            }
            return SCROLL_RIGHT_DISABLED;
        }
        if ($$0 == 8) {
            return CLOSE_ITEM;
        }
        if ($$1 < 0 || $$1 >= this.category.getItems().size()) {
            return EMPTY_SLOT;
        }
        return MoreObjects.firstNonNull(this.category.getItems().get($$1), EMPTY_SLOT);
    }

    public List<SpectatorMenuItem> getItems() {
        ArrayList<SpectatorMenuItem> $$0 = Lists.newArrayList();
        for (int $$1 = 0; $$1 <= 8; ++$$1) {
            $$0.add(this.getItem($$1));
        }
        return $$0;
    }

    public SpectatorMenuItem getSelectedItem() {
        return this.getItem(this.selectedSlot);
    }

    public SpectatorMenuCategory getSelectedCategory() {
        return this.category;
    }

    public void selectSlot(int $$0) {
        SpectatorMenuItem $$1 = this.getItem($$0);
        if ($$1 != EMPTY_SLOT) {
            if (this.selectedSlot == $$0 && $$1.isEnabled()) {
                $$1.selectItem(this);
            } else {
                this.selectedSlot = $$0;
            }
        }
    }

    public void exit() {
        this.listener.onSpectatorMenuClosed(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectCategory(SpectatorMenuCategory $$0) {
        this.category = $$0;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorPage getCurrentPage() {
        return new SpectatorPage(this.getItems(), this.selectedSlot);
    }

    static class CloseSpectatorItem
    implements SpectatorMenuItem {
        CloseSpectatorItem() {
        }

        @Override
        public void selectItem(SpectatorMenu $$0) {
            $$0.exit();
        }

        @Override
        public Component getName() {
            return CLOSE_MENU_TEXT;
        }

        @Override
        public void renderIcon(GuiGraphics $$0, float $$1, float $$2) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, CLOSE_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat($$2, $$1, $$1, $$1));
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    static class ScrollMenuItem
    implements SpectatorMenuItem {
        private final int direction;
        private final boolean enabled;

        public ScrollMenuItem(int $$0, boolean $$1) {
            this.direction = $$0;
            this.enabled = $$1;
        }

        @Override
        public void selectItem(SpectatorMenu $$0) {
            $$0.page += this.direction;
        }

        @Override
        public Component getName() {
            return this.direction < 0 ? PREVIOUS_PAGE_TEXT : NEXT_PAGE_TEXT;
        }

        @Override
        public void renderIcon(GuiGraphics $$0, float $$1, float $$2) {
            int $$3 = ARGB.colorFromFloat($$2, $$1, $$1, $$1);
            if (this.direction < 0) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_LEFT_SPRITE, 0, 0, 16, 16, $$3);
            } else {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_RIGHT_SPRITE, 0, 0, 16, 16, $$3);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

