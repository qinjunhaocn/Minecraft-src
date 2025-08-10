/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

public class EditBox
extends AbstractWidget {
    private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/text_field"), ResourceLocation.withDefaultNamespace("widget/text_field_highlighted"));
    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    public static final int DEFAULT_TEXT_COLOR = -2039584;
    private static final int CURSOR_BLINK_INTERVAL_MS = 300;
    private final Font font;
    private String value = "";
    private int maxLength = 32;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean centered = false;
    private boolean textShadow = true;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = -2039584;
    private int textColorUneditable = -9408400;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = ($$0, $$1) -> FormattedCharSequence.forward($$0, Style.EMPTY);
    @Nullable
    private Component hint;
    private long focusedTime = Util.getMillis();
    private int textX;
    private int textY;

    public EditBox(Font $$0, int $$1, int $$2, Component $$3) {
        this($$0, 0, 0, $$1, $$2, $$3);
    }

    public EditBox(Font $$0, int $$1, int $$2, int $$3, int $$4, Component $$5) {
        this($$0, $$1, $$2, $$3, $$4, null, $$5);
    }

    public EditBox(Font $$02, int $$12, int $$2, int $$3, int $$4, @Nullable EditBox $$5, Component $$6) {
        super($$12, $$2, $$3, $$4, $$6);
        this.font = $$02;
        if ($$5 != null) {
            this.setValue($$5.getValue());
        }
        this.updateTextPosition();
    }

    public void setResponder(Consumer<String> $$0) {
        this.responder = $$0;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> $$0) {
        this.formatter = $$0;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        Component $$0 = this.getMessage();
        return Component.a("gui.narrate.editBox", $$0, this.value);
    }

    public void setValue(String $$0) {
        if (!this.filter.test($$0)) {
            return;
        }
        this.value = $$0.length() > this.maxLength ? $$0.substring(0, this.maxLength) : $$0;
        this.moveCursorToEnd(false);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange($$0);
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int $$0 = Math.min(this.cursorPos, this.highlightPos);
        int $$1 = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring($$0, $$1);
    }

    @Override
    public void setX(int $$0) {
        super.setX($$0);
        this.updateTextPosition();
    }

    @Override
    public void setY(int $$0) {
        super.setY($$0);
        this.updateTextPosition();
    }

    public void setFilter(Predicate<String> $$0) {
        this.filter = $$0;
    }

    public void insertText(String $$0) {
        String $$6;
        int $$1 = Math.min(this.cursorPos, this.highlightPos);
        int $$2 = Math.max(this.cursorPos, this.highlightPos);
        int $$3 = this.maxLength - this.value.length() - ($$1 - $$2);
        if ($$3 <= 0) {
            return;
        }
        String $$4 = StringUtil.filterText($$0);
        int $$5 = $$4.length();
        if ($$3 < $$5) {
            if (Character.isHighSurrogate($$4.charAt($$3 - 1))) {
                --$$3;
            }
            $$4 = $$4.substring(0, $$3);
            $$5 = $$3;
        }
        if (!this.filter.test($$6 = new StringBuilder(this.value).replace($$1, $$2, $$4).toString())) {
            return;
        }
        this.value = $$6;
        this.setCursorPosition($$1 + $$5);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String $$0) {
        if (this.responder != null) {
            this.responder.accept($$0);
        }
        this.updateTextPosition();
    }

    private void deleteText(int $$0) {
        if (Screen.hasControlDown()) {
            this.deleteWords($$0);
        } else {
            this.deleteChars($$0);
        }
    }

    public void deleteWords(int $$0) {
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        this.deleteCharsToPos(this.getWordPosition($$0));
    }

    public void deleteChars(int $$0) {
        this.deleteCharsToPos(this.getCursorPos($$0));
    }

    public void deleteCharsToPos(int $$0) {
        int $$2;
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        int $$1 = Math.min($$0, this.cursorPos);
        if ($$1 == ($$2 = Math.max($$0, this.cursorPos))) {
            return;
        }
        String $$3 = new StringBuilder(this.value).delete($$1, $$2).toString();
        if (!this.filter.test($$3)) {
            return;
        }
        this.value = $$3;
        this.moveCursorTo($$1, false);
    }

    public int getWordPosition(int $$0) {
        return this.getWordPosition($$0, this.getCursorPosition());
    }

    private int getWordPosition(int $$0, int $$1) {
        return this.getWordPosition($$0, $$1, true);
    }

    private int getWordPosition(int $$0, int $$1, boolean $$2) {
        int $$3 = $$1;
        boolean $$4 = $$0 < 0;
        int $$5 = Math.abs($$0);
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            if ($$4) {
                while ($$2 && $$3 > 0 && this.value.charAt($$3 - 1) == ' ') {
                    --$$3;
                }
                while ($$3 > 0 && this.value.charAt($$3 - 1) != ' ') {
                    --$$3;
                }
                continue;
            }
            int $$7 = this.value.length();
            if (($$3 = this.value.indexOf(32, $$3)) == -1) {
                $$3 = $$7;
                continue;
            }
            while ($$2 && $$3 < $$7 && this.value.charAt($$3) == ' ') {
                ++$$3;
            }
        }
        return $$3;
    }

    public void moveCursor(int $$0, boolean $$1) {
        this.moveCursorTo(this.getCursorPos($$0), $$1);
    }

    private int getCursorPos(int $$0) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, $$0);
    }

    public void moveCursorTo(int $$0, boolean $$1) {
        this.setCursorPosition($$0);
        if (!$$1) {
            this.setHighlightPos(this.cursorPos);
        }
        this.onValueChange(this.value);
    }

    public void setCursorPosition(int $$0) {
        this.cursorPos = Mth.clamp($$0, 0, this.value.length());
        this.scrollTo(this.cursorPos);
    }

    public void moveCursorToStart(boolean $$0) {
        this.moveCursorTo(0, $$0);
    }

    public void moveCursorToEnd(boolean $$0) {
        this.moveCursorTo(this.value.length(), $$0);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.isActive() || !this.isFocused()) {
            return false;
        }
        switch ($$0) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                } else {
                    this.moveCursor(-1, Screen.hasShiftDown());
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                } else {
                    this.moveCursor(1, Screen.hasShiftDown());
                }
                return true;
            }
            case 259: {
                if (this.isEditable) {
                    this.deleteText(-1);
                }
                return true;
            }
            case 261: {
                if (this.isEditable) {
                    this.deleteText(1);
                }
                return true;
            }
            case 268: {
                this.moveCursorToStart(Screen.hasShiftDown());
                return true;
            }
            case 269: {
                this.moveCursorToEnd(Screen.hasShiftDown());
                return true;
            }
        }
        if (Screen.isSelectAll($$0)) {
            this.moveCursorToEnd(false);
            this.setHighlightPos(0);
            return true;
        }
        if (Screen.isCopy($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        }
        if (Screen.isPaste($$0)) {
            if (this.isEditable()) {
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }
            return true;
        }
        if (Screen.isCut($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable()) {
                this.insertText("");
            }
            return true;
        }
        return false;
    }

    public boolean canConsumeInput() {
        return this.isActive() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (!this.canConsumeInput()) {
            return false;
        }
        if (StringUtil.a($$0)) {
            if (this.isEditable) {
                this.insertText(Character.toString($$0));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(double $$0, double $$1) {
        int $$2 = Mth.floor($$0) - this.textX;
        String $$3 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.moveCursorTo(this.font.plainSubstrByWidth($$3, $$2).length() + this.displayPos, Screen.hasShiftDown());
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible()) {
            return;
        }
        if (this.isBordered()) {
            ResourceLocation $$4 = SPRITES.get(this.isActive(), this.isFocused());
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$4, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        int $$5 = this.isEditable ? this.textColor : this.textColorUneditable;
        int $$6 = this.cursorPos - this.displayPos;
        String $$7 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean $$8 = $$6 >= 0 && $$6 <= $$7.length();
        boolean $$9 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && $$8;
        int $$10 = this.textX;
        int $$11 = Mth.clamp(this.highlightPos - this.displayPos, 0, $$7.length());
        if (!$$7.isEmpty()) {
            String $$12 = $$8 ? $$7.substring(0, $$6) : $$7;
            FormattedCharSequence $$13 = this.formatter.apply($$12, this.displayPos);
            $$0.drawString(this.font, $$13, $$10, this.textY, $$5, this.textShadow);
            $$10 += this.font.width($$13) + 1;
        }
        boolean $$14 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int $$15 = $$10;
        if (!$$8) {
            $$15 = $$6 > 0 ? this.textX + this.width : this.textX;
        } else if ($$14) {
            --$$15;
            --$$10;
        }
        if (!$$7.isEmpty() && $$8 && $$6 < $$7.length()) {
            $$0.drawString(this.font, this.formatter.apply($$7.substring($$6), this.cursorPos), $$10, this.textY, $$5, this.textShadow);
        }
        if (this.hint != null && $$7.isEmpty() && !this.isFocused()) {
            $$0.drawString(this.font, this.hint, $$10, this.textY, $$5);
        }
        if (!$$14 && this.suggestion != null) {
            $$0.drawString(this.font, this.suggestion, $$15 - 1, this.textY, -8355712, this.textShadow);
        }
        if ($$11 != $$6) {
            int $$16 = this.textX + this.font.width($$7.substring(0, $$11));
            $$0.textHighlight(Math.min($$15, this.getX() + this.width), this.textY - 1, Math.min($$16 - 1, this.getX() + this.width), this.textY + 1 + this.font.lineHeight);
        }
        if ($$9) {
            if ($$14) {
                $$0.fill($$15, this.textY - 1, $$15 + 1, this.textY + 1 + this.font.lineHeight, -3092272);
            } else {
                $$0.drawString(this.font, CURSOR_APPEND_CHARACTER, $$15, this.textY, $$5, this.textShadow);
            }
        }
    }

    private void updateTextPosition() {
        if (this.font == null) {
            return;
        }
        String $$0 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.textX = this.getX() + (this.isCentered() ? (this.getWidth() - this.font.width($$0)) / 2 : (this.bordered ? 4 : 0));
        this.textY = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
    }

    public void setMaxLength(int $$0) {
        this.maxLength = $$0;
        if (this.value.length() > $$0) {
            this.value = this.value.substring(0, $$0);
            this.onValueChange(this.value);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean $$0) {
        this.bordered = $$0;
        this.updateTextPosition();
    }

    public void setTextColor(int $$0) {
        this.textColor = $$0;
    }

    public void setTextColorUneditable(int $$0) {
        this.textColorUneditable = $$0;
    }

    @Override
    public void setFocused(boolean $$0) {
        if (!this.canLoseFocus && !$$0) {
            return;
        }
        super.setFocused($$0);
        if ($$0) {
            this.focusedTime = Util.getMillis();
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean $$0) {
        this.isEditable = $$0;
    }

    private boolean isCentered() {
        return this.centered;
    }

    public void setCentered(boolean $$0) {
        this.centered = $$0;
        this.updateTextPosition();
    }

    public void setTextShadow(boolean $$0) {
        this.textShadow = $$0;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int $$0) {
        this.highlightPos = Mth.clamp($$0, 0, this.value.length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(int $$0) {
        if (this.font == null) {
            return;
        }
        this.displayPos = Math.min(this.displayPos, this.value.length());
        int $$1 = this.getInnerWidth();
        String $$2 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), $$1);
        int $$3 = $$2.length() + this.displayPos;
        if ($$0 == this.displayPos) {
            this.displayPos -= this.font.plainSubstrByWidth(this.value, $$1, true).length();
        }
        if ($$0 > $$3) {
            this.displayPos += $$0 - $$3;
        } else if ($$0 <= this.displayPos) {
            this.displayPos -= this.displayPos - $$0;
        }
        this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
    }

    public void setCanLoseFocus(boolean $$0) {
        this.canLoseFocus = $$0;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean $$0) {
        this.visible = $$0;
    }

    public void setSuggestion(@Nullable String $$0) {
        this.suggestion = $$0;
    }

    public int getScreenX(int $$0) {
        if ($$0 > this.value.length()) {
            return this.getX();
        }
        return this.getX() + this.font.width(this.value.substring(0, $$0));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    public void setHint(Component $$0) {
        this.hint = $$0;
    }
}

