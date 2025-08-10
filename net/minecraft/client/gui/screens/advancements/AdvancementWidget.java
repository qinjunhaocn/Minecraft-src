/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementWidget {
    private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
    private static final int HEIGHT = 26;
    private static final int BOX_X = 0;
    private static final int BOX_WIDTH = 200;
    private static final int FRAME_WIDTH = 26;
    private static final int ICON_X = 8;
    private static final int ICON_Y = 5;
    private static final int ICON_WIDTH = 26;
    private static final int TITLE_PADDING_LEFT = 3;
    private static final int TITLE_PADDING_RIGHT = 5;
    private static final int TITLE_X = 32;
    private static final int TITLE_PADDING_TOP = 9;
    private static final int TITLE_PADDING_BOTTOM = 8;
    private static final int TITLE_MAX_WIDTH = 163;
    private static final int TITLE_MIN_WIDTH = 80;
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final AdvancementNode advancementNode;
    private final DisplayInfo display;
    private final List<FormattedCharSequence> titleLines;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    @Nullable
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    @Nullable
    private AdvancementProgress progress;
    private final int x;
    private final int y;

    public AdvancementWidget(AdvancementTab $$0, Minecraft $$1, AdvancementNode $$2, DisplayInfo $$3) {
        this.tab = $$0;
        this.advancementNode = $$2;
        this.display = $$3;
        this.minecraft = $$1;
        this.titleLines = $$1.font.split($$3.getTitle(), 163);
        this.x = Mth.floor($$3.getX() * 28.0f);
        this.y = Mth.floor($$3.getY() * 27.0f);
        int $$4 = Math.max(this.titleLines.stream().mapToInt($$1.font::width).max().orElse(0), 80);
        int $$5 = this.getMaxProgressWidth();
        int $$6 = 29 + $$4 + $$5;
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles($$3.getDescription().copy(), Style.EMPTY.withColor($$3.getType().getChatColor())), $$6));
        for (FormattedCharSequence $$7 : this.description) {
            $$6 = Math.max($$6, $$1.font.width($$7));
        }
        this.width = $$6 + 3 + 5;
    }

    private int getMaxProgressWidth() {
        int $$0 = this.advancementNode.advancement().requirements().size();
        if ($$0 <= 1) {
            return 0;
        }
        int $$1 = 8;
        MutableComponent $$2 = Component.a("advancements.progress", $$0, $$0);
        return this.minecraft.font.width($$2) + 8;
    }

    private static float getMaxWidth(StringSplitter $$0, List<FormattedText> $$1) {
        return (float)$$1.stream().mapToDouble($$0::stringWidth).max().orElse(0.0);
    }

    private List<FormattedText> findOptimalLines(Component $$0, int $$1) {
        StringSplitter $$2 = this.minecraft.font.getSplitter();
        List<FormattedText> $$3 = null;
        float $$4 = Float.MAX_VALUE;
        for (int $$5 : TEST_SPLIT_OFFSETS) {
            List<FormattedText> $$6 = $$2.splitLines($$0, $$1 - $$5, Style.EMPTY);
            float $$7 = Math.abs(AdvancementWidget.getMaxWidth($$2, $$6) - (float)$$1);
            if ($$7 <= 10.0f) {
                return $$6;
            }
            if (!($$7 < $$4)) continue;
            $$4 = $$7;
            $$3 = $$6;
        }
        return $$3;
    }

    @Nullable
    private AdvancementWidget getFirstVisibleParent(AdvancementNode $$0) {
        while (($$0 = $$0.parent()) != null && $$0.advancement().display().isEmpty()) {
        }
        if ($$0 == null || $$0.advancement().display().isEmpty()) {
            return null;
        }
        return this.tab.getWidget($$0.holder());
    }

    public void drawConnectivity(GuiGraphics $$0, int $$1, int $$2, boolean $$3) {
        if (this.parent != null) {
            int $$9;
            int $$4 = $$1 + this.parent.x + 13;
            int $$5 = $$1 + this.parent.x + 26 + 4;
            int $$6 = $$2 + this.parent.y + 13;
            int $$7 = $$1 + this.x + 13;
            int $$8 = $$2 + this.y + 13;
            int n = $$9 = $$3 ? -16777216 : -1;
            if ($$3) {
                $$0.hLine($$5, $$4, $$6 - 1, $$9);
                $$0.hLine($$5 + 1, $$4, $$6, $$9);
                $$0.hLine($$5, $$4, $$6 + 1, $$9);
                $$0.hLine($$7, $$5 - 1, $$8 - 1, $$9);
                $$0.hLine($$7, $$5 - 1, $$8, $$9);
                $$0.hLine($$7, $$5 - 1, $$8 + 1, $$9);
                $$0.vLine($$5 - 1, $$8, $$6, $$9);
                $$0.vLine($$5 + 1, $$8, $$6, $$9);
            } else {
                $$0.hLine($$5, $$4, $$6, $$9);
                $$0.hLine($$7, $$5, $$8, $$9);
                $$0.vLine($$5, $$8, $$6, $$9);
            }
        }
        for (AdvancementWidget $$10 : this.children) {
            $$10.drawConnectivity($$0, $$1, $$2, $$3);
        }
    }

    public void draw(GuiGraphics $$0, int $$1, int $$2) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            AdvancementWidgetType $$5;
            float $$3;
            float f = $$3 = this.progress == null ? 0.0f : this.progress.getPercent();
            if ($$3 >= 1.0f) {
                AdvancementWidgetType $$4 = AdvancementWidgetType.OBTAINED;
            } else {
                $$5 = AdvancementWidgetType.UNOBTAINED;
            }
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5.frameSprite(this.display.getType()), $$1 + this.x + 3, $$2 + this.y, 26, 26);
            $$0.renderFakeItem(this.display.getIcon(), $$1 + this.x + 8, $$2 + this.y + 5);
        }
        for (AdvancementWidget $$6 : this.children) {
            $$6.draw($$0, $$1, $$2);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void setProgress(AdvancementProgress $$0) {
        this.progress = $$0;
    }

    public void addChild(AdvancementWidget $$0) {
        this.children.add($$0);
    }

    public void drawHover(GuiGraphics $$0, int $$1, int $$2, float $$3, int $$4, int $$5) {
        int $$32;
        AdvancementWidgetType $$29;
        AdvancementWidgetType $$28;
        AdvancementWidgetType $$27;
        Font $$6 = this.minecraft.font;
        int $$7 = $$6.lineHeight * this.titleLines.size() + 9 + 8;
        int $$8 = $$2 + this.y + (26 - $$7) / 2;
        int $$9 = $$8 + $$7;
        int $$10 = this.description.size() * $$6.lineHeight;
        int $$11 = 6 + $$10;
        boolean $$12 = $$4 + $$1 + this.x + this.width + 26 >= this.tab.getScreen().width;
        Component $$13 = this.progress == null ? null : this.progress.getProgressText();
        int $$14 = $$13 == null ? 0 : $$6.width($$13);
        boolean $$15 = $$9 + $$11 >= 113;
        float $$16 = this.progress == null ? 0.0f : this.progress.getPercent();
        int $$17 = Mth.floor($$16 * (float)this.width);
        if ($$16 >= 1.0f) {
            $$17 = this.width / 2;
            AdvancementWidgetType $$18 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$19 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$20 = AdvancementWidgetType.OBTAINED;
        } else if ($$17 < 2) {
            $$17 = this.width / 2;
            AdvancementWidgetType $$21 = AdvancementWidgetType.UNOBTAINED;
            AdvancementWidgetType $$22 = AdvancementWidgetType.UNOBTAINED;
            AdvancementWidgetType $$23 = AdvancementWidgetType.UNOBTAINED;
        } else if ($$17 > this.width - 2) {
            $$17 = this.width / 2;
            AdvancementWidgetType $$24 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$25 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$26 = AdvancementWidgetType.UNOBTAINED;
        } else {
            $$27 = AdvancementWidgetType.OBTAINED;
            $$28 = AdvancementWidgetType.UNOBTAINED;
            $$29 = AdvancementWidgetType.UNOBTAINED;
        }
        int $$30 = this.width - $$17;
        if ($$12) {
            int $$31 = $$1 + this.x - this.width + 26 + 6;
        } else {
            $$32 = $$1 + this.x;
        }
        int $$33 = $$7 + $$11;
        if (!this.description.isEmpty()) {
            if ($$15) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TITLE_BOX_SPRITE, $$32, $$9 - $$33, this.width, $$33);
            } else {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TITLE_BOX_SPRITE, $$32, $$8, this.width, $$33);
            }
        }
        if ($$27 != $$28) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$27.boxSprite(), 200, $$7, 0, 0, $$32, $$8, $$17, $$7);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$28.boxSprite(), 200, $$7, 200 - $$30, 0, $$32 + $$17, $$8, $$30, $$7);
        } else {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$27.boxSprite(), $$32, $$8, this.width, $$7);
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$29.frameSprite(this.display.getType()), $$1 + this.x + 3, $$2 + this.y, 26, 26);
        int $$34 = $$32 + 5;
        if ($$12) {
            this.drawMultilineText($$0, this.titleLines, $$34, $$8 + 9, -1);
            if ($$13 != null) {
                $$0.drawString($$6, $$13, $$1 + this.x - $$14, $$8 + 9, -1);
            }
        } else {
            this.drawMultilineText($$0, this.titleLines, $$1 + this.x + 32, $$8 + 9, -1);
            if ($$13 != null) {
                $$0.drawString($$6, $$13, $$1 + this.x + this.width - $$14 - 5, $$8 + 9, -1);
            }
        }
        if ($$15) {
            this.drawMultilineText($$0, this.description, $$34, $$8 - $$10 + 1, -16711936);
        } else {
            this.drawMultilineText($$0, this.description, $$34, $$9, -16711936);
        }
        $$0.renderFakeItem(this.display.getIcon(), $$1 + this.x + 8, $$2 + this.y + 5);
    }

    private void drawMultilineText(GuiGraphics $$0, List<FormattedCharSequence> $$1, int $$2, int $$3, int $$4) {
        Font $$5 = this.minecraft.font;
        for (int $$6 = 0; $$6 < $$1.size(); ++$$6) {
            $$0.drawString($$5, $$1.get($$6), $$2, $$3 + $$6 * $$5.lineHeight, $$4);
        }
    }

    public boolean isMouseOver(int $$0, int $$1, int $$2, int $$3) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int $$4 = $$0 + this.x;
        int $$5 = $$4 + 26;
        int $$6 = $$1 + this.y;
        int $$7 = $$6 + 26;
        return $$2 >= $$4 && $$2 <= $$5 && $$3 >= $$6 && $$3 <= $$7;
    }

    public void attachToParent() {
        if (this.parent == null && this.advancementNode.parent() != null) {
            this.parent = this.getFirstVisibleParent(this.advancementNode);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}

