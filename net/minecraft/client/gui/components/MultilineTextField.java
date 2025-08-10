/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.components;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class MultilineTextField {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int NO_LIMIT = Integer.MAX_VALUE;
    private static final int LINE_SEEK_PIXEL_BIAS = 2;
    private final Font font;
    private final List<StringView> displayLines = Lists.newArrayList();
    private String value;
    private int cursor;
    private int selectCursor;
    private boolean selecting;
    private int characterLimit = Integer.MAX_VALUE;
    private int lineLimit = Integer.MAX_VALUE;
    private final int width;
    private Consumer<String> valueListener = $$0 -> {};
    private Runnable cursorListener = () -> {};

    public MultilineTextField(Font $$02, int $$1) {
        this.font = $$02;
        this.width = $$1;
        this.setValue("");
    }

    public int characterLimit() {
        return this.characterLimit;
    }

    public void setCharacterLimit(int $$0) {
        if ($$0 < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.characterLimit = $$0;
    }

    public void setLineLimit(int $$0) {
        if ($$0 < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        }
        this.lineLimit = $$0;
    }

    public boolean hasCharacterLimit() {
        return this.characterLimit != Integer.MAX_VALUE;
    }

    public boolean hasLineLimit() {
        return this.lineLimit != Integer.MAX_VALUE;
    }

    public void setValueListener(Consumer<String> $$0) {
        this.valueListener = $$0;
    }

    public void setCursorListener(Runnable $$0) {
        this.cursorListener = $$0;
    }

    public void setValue(String $$0) {
        this.setValue($$0, false);
    }

    public void setValue(String $$0, boolean $$1) {
        String $$2 = this.truncateFullText($$0);
        if (!$$1 && this.overflowsLineLimit($$2)) {
            return;
        }
        this.value = $$2;
        this.selectCursor = this.cursor = this.value.length();
        this.onValueChange();
    }

    public String value() {
        return this.value;
    }

    public void insertText(String $$0) {
        if ($$0.isEmpty() && !this.hasSelection()) {
            return;
        }
        String $$1 = this.truncateInsertionText(StringUtil.filterText($$0, true));
        StringView $$2 = this.getSelected();
        String $$3 = new StringBuilder(this.value).replace($$2.beginIndex, $$2.endIndex, $$1).toString();
        if (this.overflowsLineLimit($$3)) {
            return;
        }
        this.value = $$3;
        this.selectCursor = this.cursor = $$2.beginIndex + $$1.length();
        this.onValueChange();
    }

    public void deleteText(int $$0) {
        if (!this.hasSelection()) {
            this.selectCursor = Mth.clamp(this.cursor + $$0, 0, this.value.length());
        }
        this.insertText("");
    }

    public int cursor() {
        return this.cursor;
    }

    public void setSelecting(boolean $$0) {
        this.selecting = $$0;
    }

    public StringView getSelected() {
        return new StringView(Math.min(this.selectCursor, this.cursor), Math.max(this.selectCursor, this.cursor));
    }

    public int getLineCount() {
        return this.displayLines.size();
    }

    public int getLineAtCursor() {
        for (int $$0 = 0; $$0 < this.displayLines.size(); ++$$0) {
            StringView $$1 = this.displayLines.get($$0);
            if (this.cursor < $$1.beginIndex || this.cursor > $$1.endIndex) continue;
            return $$0;
        }
        return -1;
    }

    public StringView getLineView(int $$0) {
        return this.displayLines.get(Mth.clamp($$0, 0, this.displayLines.size() - 1));
    }

    public void seekCursor(Whence $$0, int $$1) {
        switch ($$0) {
            case ABSOLUTE: {
                this.cursor = $$1;
                break;
            }
            case RELATIVE: {
                this.cursor += $$1;
                break;
            }
            case END: {
                this.cursor = this.value.length() + $$1;
            }
        }
        this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
        this.cursorListener.run();
        if (!this.selecting) {
            this.selectCursor = this.cursor;
        }
    }

    public void seekCursorLine(int $$0) {
        if ($$0 == 0) {
            return;
        }
        int $$1 = this.font.width(this.value.substring(this.getCursorLineView().beginIndex, this.cursor)) + 2;
        StringView $$2 = this.getCursorLineView($$0);
        int $$3 = this.font.plainSubstrByWidth(this.value.substring($$2.beginIndex, $$2.endIndex), $$1).length();
        this.seekCursor(Whence.ABSOLUTE, $$2.beginIndex + $$3);
    }

    public void seekCursorToPoint(double $$0, double $$1) {
        int $$2 = Mth.floor($$0);
        int $$3 = Mth.floor($$1 / (double)this.font.lineHeight);
        StringView $$4 = this.displayLines.get(Mth.clamp($$3, 0, this.displayLines.size() - 1));
        int $$5 = this.font.plainSubstrByWidth(this.value.substring($$4.beginIndex, $$4.endIndex), $$2).length();
        this.seekCursor(Whence.ABSOLUTE, $$4.beginIndex + $$5);
    }

    public boolean keyPressed(int $$0) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll($$0)) {
            this.cursor = this.value.length();
            this.selectCursor = 0;
            return true;
        }
        if (Screen.isCopy($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste($$0)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        }
        if (Screen.isCut($$0)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.insertText("");
            return true;
        }
        switch ($$0) {
            case 263: {
                if (Screen.hasControlDown()) {
                    StringView $$1 = this.getPreviousWord();
                    this.seekCursor(Whence.ABSOLUTE, $$1.beginIndex);
                } else {
                    this.seekCursor(Whence.RELATIVE, -1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    StringView $$2 = this.getNextWord();
                    this.seekCursor(Whence.ABSOLUTE, $$2.beginIndex);
                } else {
                    this.seekCursor(Whence.RELATIVE, 1);
                }
                return true;
            }
            case 265: {
                if (!Screen.hasControlDown()) {
                    this.seekCursorLine(-1);
                }
                return true;
            }
            case 264: {
                if (!Screen.hasControlDown()) {
                    this.seekCursorLine(1);
                }
                return true;
            }
            case 266: {
                this.seekCursor(Whence.ABSOLUTE, 0);
                return true;
            }
            case 267: {
                this.seekCursor(Whence.END, 0);
                return true;
            }
            case 268: {
                if (Screen.hasControlDown()) {
                    this.seekCursor(Whence.ABSOLUTE, 0);
                } else {
                    this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().beginIndex);
                }
                return true;
            }
            case 269: {
                if (Screen.hasControlDown()) {
                    this.seekCursor(Whence.END, 0);
                } else {
                    this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().endIndex);
                }
                return true;
            }
            case 259: {
                if (Screen.hasControlDown()) {
                    StringView $$3 = this.getPreviousWord();
                    this.deleteText($$3.beginIndex - this.cursor);
                } else {
                    this.deleteText(-1);
                }
                return true;
            }
            case 261: {
                if (Screen.hasControlDown()) {
                    StringView $$4 = this.getNextWord();
                    this.deleteText($$4.beginIndex - this.cursor);
                } else {
                    this.deleteText(1);
                }
                return true;
            }
            case 257: 
            case 335: {
                this.insertText("\n");
                return true;
            }
        }
        return false;
    }

    public Iterable<StringView> iterateLines() {
        return this.displayLines;
    }

    public boolean hasSelection() {
        return this.selectCursor != this.cursor;
    }

    @VisibleForTesting
    public String getSelectedText() {
        StringView $$0 = this.getSelected();
        return this.value.substring($$0.beginIndex, $$0.endIndex);
    }

    private StringView getCursorLineView() {
        return this.getCursorLineView(0);
    }

    private StringView getCursorLineView(int $$0) {
        int $$1 = this.getLineAtCursor();
        if ($$1 < 0) {
            LOGGER.error("Cursor is not within text (cursor = {}, length = {})", (Object)this.cursor, (Object)this.value.length());
            return (StringView)((Object)this.displayLines.getLast());
        }
        return this.displayLines.get(Mth.clamp($$1 + $$0, 0, this.displayLines.size() - 1));
    }

    @VisibleForTesting
    public StringView getPreviousWord() {
        int $$0;
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        }
        for ($$0 = Mth.clamp(this.cursor, 0, this.value.length() - 1); $$0 > 0 && Character.isWhitespace(this.value.charAt($$0 - 1)); --$$0) {
        }
        while ($$0 > 0 && !Character.isWhitespace(this.value.charAt($$0 - 1))) {
            --$$0;
        }
        return new StringView($$0, this.getWordEndPosition($$0));
    }

    @VisibleForTesting
    public StringView getNextWord() {
        int $$0;
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        }
        for ($$0 = Mth.clamp(this.cursor, 0, this.value.length() - 1); $$0 < this.value.length() && !Character.isWhitespace(this.value.charAt($$0)); ++$$0) {
        }
        while ($$0 < this.value.length() && Character.isWhitespace(this.value.charAt($$0))) {
            ++$$0;
        }
        return new StringView($$0, this.getWordEndPosition($$0));
    }

    private int getWordEndPosition(int $$0) {
        int $$1;
        for ($$1 = $$0; $$1 < this.value.length() && !Character.isWhitespace(this.value.charAt($$1)); ++$$1) {
        }
        return $$1;
    }

    private void onValueChange() {
        this.reflowDisplayLines();
        this.valueListener.accept(this.value);
        this.cursorListener.run();
    }

    private void reflowDisplayLines() {
        this.displayLines.clear();
        if (this.value.isEmpty()) {
            this.displayLines.add(StringView.EMPTY);
            return;
        }
        this.font.getSplitter().splitLines(this.value, this.width, Style.EMPTY, false, ($$0, $$1, $$2) -> this.displayLines.add(new StringView($$1, $$2)));
        if (this.value.charAt(this.value.length() - 1) == '\n') {
            this.displayLines.add(new StringView(this.value.length(), this.value.length()));
        }
    }

    private String truncateFullText(String $$0) {
        if (this.hasCharacterLimit()) {
            return StringUtil.truncateStringIfNecessary($$0, this.characterLimit, false);
        }
        return $$0;
    }

    private String truncateInsertionText(String $$0) {
        String $$1 = $$0;
        if (this.hasCharacterLimit()) {
            int $$2 = this.characterLimit - this.value.length();
            $$1 = StringUtil.truncateStringIfNecessary($$0, $$2, false);
        }
        return $$1;
    }

    private boolean overflowsLineLimit(String $$0) {
        return this.hasLineLimit() && this.font.getSplitter().splitLines($$0, this.width, Style.EMPTY).size() + (StringUtil.endsWithNewLine($$0) ? 1 : 0) > this.lineLimit;
    }

    protected static final class StringView
    extends Record {
        final int beginIndex;
        final int endIndex;
        static final StringView EMPTY = new StringView(0, 0);

        protected StringView(int $$0, int $$1) {
            this.beginIndex = $$0;
            this.endIndex = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StringView.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StringView.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StringView.class, "beginIndex;endIndex", "beginIndex", "endIndex"}, this, $$0);
        }

        public int beginIndex() {
            return this.beginIndex;
        }

        public int endIndex() {
            return this.endIndex;
        }
    }
}

