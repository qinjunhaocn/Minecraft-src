/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.packs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList
extends ObjectSelectionList<PackEntry> {
    static final ResourceLocation SELECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/select_highlighted");
    static final ResourceLocation SELECT_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/select");
    static final ResourceLocation UNSELECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/unselect_highlighted");
    static final ResourceLocation UNSELECT_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/unselect");
    static final ResourceLocation MOVE_UP_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_up_highlighted");
    static final ResourceLocation MOVE_UP_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_up");
    static final ResourceLocation MOVE_DOWN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_down_highlighted");
    static final ResourceLocation MOVE_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("transferable_list/move_down");
    static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
    static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
    private final Component title;
    final PackSelectionScreen screen;

    public TransferableSelectionList(Minecraft $$0, PackSelectionScreen $$1, int $$2, int $$3, Component $$4) {
        Objects.requireNonNull($$0.font);
        super($$0, $$2, $$3, 33, 36, (int)(9.0f * 1.5f));
        this.screen = $$1;
        this.title = $$4;
        this.centerListVertically = false;
    }

    @Override
    protected void renderHeader(GuiGraphics $$0, int $$1, int $$2) {
        MutableComponent $$3 = Component.empty().append(this.title).a(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        $$0.drawString(this.minecraft.font, $$3, $$1 + this.width / 2 - this.minecraft.font.width($$3) / 2, Math.min(this.getY() + 3, $$2), -1);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int scrollBarX() {
        return this.getRight() - 6;
    }

    @Override
    protected void renderSelection(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if (this.scrollbarVisible()) {
            int $$6 = 2;
            int $$7 = this.getRowLeft() - 2;
            int $$8 = this.getRight() - 6 - 1;
            int $$9 = $$1 - 2;
            int $$10 = $$1 + $$3 + 2;
            $$0.fill($$7, $$9, $$8, $$10, $$4);
            $$0.fill($$7 + 1, $$9 + 1, $$8 - 1, $$10 - 1, $$5);
        } else {
            super.renderSelection($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.getSelected() != null) {
            switch ($$0) {
                case 32: 
                case 257: {
                    ((PackEntry)this.getSelected()).keyboardSelection();
                    return true;
                }
            }
            if (Screen.hasShiftDown()) {
                switch ($$0) {
                    case 265: {
                        ((PackEntry)this.getSelected()).keyboardMoveUp();
                        return true;
                    }
                    case 264: {
                        ((PackEntry)this.getSelected()).keyboardMoveDown();
                        return true;
                    }
                }
            }
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    public static class PackEntry
    extends ObjectSelectionList.Entry<PackEntry> {
        private static final int MAX_DESCRIPTION_WIDTH_PIXELS = 157;
        private static final int MAX_NAME_WIDTH_PIXELS = 157;
        private static final String TOO_LONG_NAME_SUFFIX = "...";
        private final TransferableSelectionList parent;
        protected final Minecraft minecraft;
        private final PackSelectionModel.Entry pack;
        private final FormattedCharSequence nameDisplayCache;
        private final MultiLineLabel descriptionDisplayCache;
        private final FormattedCharSequence incompatibleNameDisplayCache;
        private final MultiLineLabel incompatibleDescriptionDisplayCache;

        public PackEntry(Minecraft $$0, TransferableSelectionList $$1, PackSelectionModel.Entry $$2) {
            this.minecraft = $$0;
            this.pack = $$2;
            this.parent = $$1;
            this.nameDisplayCache = PackEntry.cacheName($$0, $$2.getTitle());
            this.descriptionDisplayCache = PackEntry.cacheDescription($$0, $$2.getExtendedDescription());
            this.incompatibleNameDisplayCache = PackEntry.cacheName($$0, INCOMPATIBLE_TITLE);
            this.incompatibleDescriptionDisplayCache = PackEntry.cacheDescription($$0, $$2.getCompatibility().getDescription());
        }

        private static FormattedCharSequence cacheName(Minecraft $$0, Component $$1) {
            int $$2 = $$0.font.width($$1);
            if ($$2 > 157) {
                FormattedText $$3 = FormattedText.a($$0.font.substrByWidth($$1, 157 - $$0.font.width(TOO_LONG_NAME_SUFFIX)), FormattedText.of(TOO_LONG_NAME_SUFFIX));
                return Language.getInstance().getVisualOrder($$3);
            }
            return $$1.getVisualOrderText();
        }

        private static MultiLineLabel cacheDescription(Minecraft $$0, Component $$1) {
            return MultiLineLabel.a($$0.font, 157, 2, $$1);
        }

        @Override
        public Component getNarration() {
            return Component.a("narrator.select", this.pack.getTitle());
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            PackCompatibility $$10 = this.pack.getCompatibility();
            if (!$$10.isCompatible()) {
                int $$11 = $$3 + $$4 - 3 - (this.parent.scrollbarVisible() ? 7 : 0);
                $$0.fill($$3 - 1, $$2 - 1, $$11, $$2 + $$5 + 1, -8978432);
            }
            $$0.blit(RenderPipelines.GUI_TEXTURED, this.pack.getIconTexture(), $$3, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
            FormattedCharSequence $$12 = this.nameDisplayCache;
            MultiLineLabel $$13 = this.descriptionDisplayCache;
            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen().get().booleanValue() || $$8 || this.parent.getSelected() == this && this.parent.isFocused())) {
                $$0.fill($$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                int $$14 = $$6 - $$3;
                int $$15 = $$7 - $$2;
                if (!this.pack.getCompatibility().isCompatible()) {
                    $$12 = this.incompatibleNameDisplayCache;
                    $$13 = this.incompatibleDescriptionDisplayCache;
                }
                if (this.pack.canSelect()) {
                    if ($$14 < 32) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SELECT_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                    } else {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SELECT_SPRITE, $$3, $$2, 32, 32);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if ($$14 < 16) {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, UNSELECT_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                        } else {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, UNSELECT_SPRITE, $$3, $$2, 32, 32);
                        }
                    }
                    if (this.pack.canMoveUp()) {
                        if ($$14 < 32 && $$14 > 16 && $$15 < 16) {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_UP_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                        } else {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_UP_SPRITE, $$3, $$2, 32, 32);
                        }
                    }
                    if (this.pack.canMoveDown()) {
                        if ($$14 < 32 && $$14 > 16 && $$15 > 16) {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                        } else {
                            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_SPRITE, $$3, $$2, 32, 32);
                        }
                    }
                }
            }
            $$0.drawString(this.minecraft.font, $$12, $$3 + 32 + 2, $$2 + 1, -1);
            $$13.renderLeftAligned($$0, $$3 + 32 + 2, $$2 + 12, 10, -8355712);
        }

        public String getPackId() {
            return this.pack.getId();
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        public void keyboardSelection() {
            if (this.pack.canSelect() && this.handlePackSelection()) {
                this.parent.screen.updateFocus(this.parent);
            } else if (this.pack.canUnselect()) {
                this.pack.unselect();
                this.parent.screen.updateFocus(this.parent);
            }
        }

        void keyboardMoveUp() {
            if (this.pack.canMoveUp()) {
                this.pack.moveUp();
            }
        }

        void keyboardMoveDown() {
            if (this.pack.canMoveDown()) {
                this.pack.moveDown();
            }
        }

        private boolean handlePackSelection() {
            if (this.pack.getCompatibility().isCompatible()) {
                this.pack.select();
                return true;
            }
            Component $$02 = this.pack.getCompatibility().getConfirmation();
            this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                this.minecraft.setScreen(this.parent.screen);
                if ($$0) {
                    this.pack.select();
                }
            }, INCOMPATIBLE_CONFIRM_TITLE, $$02));
            return false;
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            double $$3 = $$0 - (double)this.parent.getRowLeft();
            double $$4 = $$1 - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
            if (this.showHoverOverlay() && $$3 <= 32.0) {
                this.parent.screen.clearSelected();
                if (this.pack.canSelect()) {
                    this.handlePackSelection();
                    return true;
                }
                if ($$3 < 16.0 && this.pack.canUnselect()) {
                    this.pack.unselect();
                    return true;
                }
                if ($$3 > 16.0 && $$4 < 16.0 && this.pack.canMoveUp()) {
                    this.pack.moveUp();
                    return true;
                }
                if ($$3 > 16.0 && $$4 > 16.0 && this.pack.canMoveDown()) {
                    this.pack.moveDown();
                    return true;
                }
            }
            return super.mouseClicked($$0, $$1, $$2);
        }
    }
}

