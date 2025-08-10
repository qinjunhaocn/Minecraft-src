/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget
implements Renderable,
GuiEventListener,
LayoutElement,
NarratableEntry {
    private static final double PERIOD_PER_SCROLLED_PIXEL = 0.5;
    private static final double MIN_SCROLL_PERIOD = 3.0;
    protected int width;
    protected int height;
    private int x;
    private int y;
    private Component message;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    private int tabOrderGroup;
    private boolean focused;
    private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

    public AbstractWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        this.x = $$0;
        this.y = $$1;
        this.width = $$2;
        this.height = $$3;
        this.message = $$4;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public final void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.visible) {
            return;
        }
        this.isHovered = $$0.containsPointInScissor($$1, $$2) && this.areCoordinatesInRectangle($$1, $$2);
        this.renderWidget($$0, $$1, $$2, $$3);
        this.tooltip.refreshTooltipForNextRenderPass($$0, $$1, $$2, this.isHovered(), this.isFocused(), this.getRectangle());
    }

    public void setTooltip(@Nullable Tooltip $$0) {
        this.tooltip.set($$0);
    }

    public void setTooltipDelay(Duration $$0) {
        this.tooltip.setDelay($$0);
    }

    protected MutableComponent createNarrationMessage() {
        return AbstractWidget.wrapDefaultNarrationMessage(this.getMessage());
    }

    public static MutableComponent wrapDefaultNarrationMessage(Component $$0) {
        return Component.a("gui.narrate.button", $$0);
    }

    protected abstract void renderWidget(GuiGraphics var1, int var2, int var3, float var4);

    protected static void renderScrollingString(GuiGraphics $$0, Font $$1, Component $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        AbstractWidget.renderScrollingString($$0, $$1, $$2, ($$3 + $$5) / 2, $$3, $$4, $$5, $$6, $$7);
    }

    protected static void renderScrollingString(GuiGraphics $$0, Font $$1, Component $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        int $$9 = $$1.width($$2);
        int $$10 = ($$5 + $$7 - $$1.lineHeight) / 2 + 1;
        int $$11 = $$6 - $$4;
        if ($$9 > $$11) {
            int $$12 = $$9 - $$11;
            double $$13 = (double)Util.getMillis() / 1000.0;
            double $$14 = Math.max((double)$$12 * 0.5, 3.0);
            double $$15 = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * $$13 / $$14)) / 2.0 + 0.5;
            double $$16 = Mth.lerp($$15, 0.0, (double)$$12);
            $$0.enableScissor($$4, $$5, $$6, $$7);
            $$0.drawString($$1, $$2, $$4 - (int)$$16, $$10, $$8);
            $$0.disableScissor();
        } else {
            int $$17 = Mth.clamp($$3, $$4 + $$9 / 2, $$6 - $$9 / 2);
            $$0.drawCenteredString($$1, $$2, $$17, $$10, $$8);
        }
    }

    protected void renderScrollingString(GuiGraphics $$0, Font $$1, int $$2, int $$3) {
        int $$4 = this.getX() + $$2;
        int $$5 = this.getX() + this.getWidth() - $$2;
        AbstractWidget.renderScrollingString($$0, $$1, this.getMessage(), $$4, this.getY(), $$5, this.getY() + this.getHeight(), $$3);
    }

    public void onClick(double $$0, double $$1) {
    }

    public void onRelease(double $$0, double $$1) {
    }

    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        boolean $$3;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton($$2) && ($$3 = this.isMouseOver($$0, $$1))) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick($$0, $$1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.isValidClickButton($$2)) {
            this.onRelease($$0, $$1);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int $$0) {
        return $$0 == 0;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.isValidClickButton($$2)) {
            this.onDrag($$0, $$1, $$3, $$4);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        if (!this.active || !this.visible) {
            return null;
        }
        if (!this.isFocused()) {
            return ComponentPath.leaf(this);
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return this.active && this.visible && this.areCoordinatesInRectangle($$0, $$1);
    }

    public void playDownSound(SoundManager $$0) {
        AbstractWidget.playButtonClickSound($$0);
    }

    public static void playButtonClickSound(SoundManager $$0) {
        $$0.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int $$0) {
        this.width = $$0;
    }

    public void setHeight(int $$0) {
        this.height = $$0;
    }

    public void setAlpha(float $$0) {
        this.alpha = $$0;
    }

    public void setMessage(Component $$0) {
        this.message = $$0;
    }

    public Component getMessage() {
        return this.message;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }

    @Override
    public boolean isActive() {
        return this.visible && this.active;
    }

    @Override
    public void setFocused(boolean $$0) {
        this.focused = $$0;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.isHovered) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public final void updateNarration(NarrationElementOutput $$0) {
        this.updateWidgetNarration($$0);
        this.tooltip.updateNarration($$0);
    }

    protected abstract void updateWidgetNarration(NarrationElementOutput var1);

    protected void defaultButtonNarrationText(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
            } else {
                $$0.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
            }
        }
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int $$0) {
        this.x = $$0;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int $$0) {
        this.y = $$0;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> $$0) {
        $$0.accept(this);
    }

    public void setSize(int $$0, int $$1) {
        this.width = $$0;
        this.height = $$1;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    private boolean areCoordinatesInRectangle(double $$0, double $$1) {
        return $$0 >= (double)this.getX() && $$1 >= (double)this.getY() && $$0 < (double)this.getRight() && $$1 < (double)this.getBottom();
    }

    public void setRectangle(int $$0, int $$1, int $$2, int $$3) {
        this.setSize($$0, $$1);
        this.setPosition($$2, $$3);
    }

    @Override
    public int getTabOrderGroup() {
        return this.tabOrderGroup;
    }

    public void setTabOrderGroup(int $$0) {
        this.tabOrderGroup = $$0;
    }
}

