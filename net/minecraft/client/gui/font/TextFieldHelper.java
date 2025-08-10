/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.font;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

public class TextFieldHelper {
    private final Supplier<String> getMessageFn;
    private final Consumer<String> setMessageFn;
    private final Supplier<String> getClipboardFn;
    private final Consumer<String> setClipboardFn;
    private final Predicate<String> stringValidator;
    private int cursorPos;
    private int selectionPos;

    public TextFieldHelper(Supplier<String> $$0, Consumer<String> $$1, Supplier<String> $$2, Consumer<String> $$3, Predicate<String> $$4) {
        this.getMessageFn = $$0;
        this.setMessageFn = $$1;
        this.getClipboardFn = $$2;
        this.setClipboardFn = $$3;
        this.stringValidator = $$4;
        this.setCursorToEnd();
    }

    public static Supplier<String> createClipboardGetter(Minecraft $$0) {
        return () -> TextFieldHelper.getClipboardContents($$0);
    }

    public static String getClipboardContents(Minecraft $$0) {
        return ChatFormatting.stripFormatting($$0.keyboardHandler.getClipboard().replaceAll("\\r", ""));
    }

    public static Consumer<String> createClipboardSetter(Minecraft $$0) {
        return $$1 -> TextFieldHelper.setClipboardContents($$0, $$1);
    }

    public static void setClipboardContents(Minecraft $$0, String $$1) {
        $$0.keyboardHandler.setClipboard($$1);
    }

    public boolean a(char $$0) {
        if (StringUtil.a($$0)) {
            this.insertText(this.getMessageFn.get(), Character.toString($$0));
        }
        return true;
    }

    public boolean keyPressed(int $$0) {
        CursorStep $$1;
        if (Screen.isSelectAll($$0)) {
            this.selectAll();
            return true;
        }
        if (Screen.isCopy($$0)) {
            this.copy();
            return true;
        }
        if (Screen.isPaste($$0)) {
            this.paste();
            return true;
        }
        if (Screen.isCut($$0)) {
            this.cut();
            return true;
        }
        CursorStep cursorStep = $$1 = Screen.hasControlDown() ? CursorStep.WORD : CursorStep.CHARACTER;
        if ($$0 == 259) {
            this.removeFromCursor(-1, $$1);
            return true;
        }
        if ($$0 == 261) {
            this.removeFromCursor(1, $$1);
        } else {
            if ($$0 == 263) {
                this.moveBy(-1, Screen.hasShiftDown(), $$1);
                return true;
            }
            if ($$0 == 262) {
                this.moveBy(1, Screen.hasShiftDown(), $$1);
                return true;
            }
            if ($$0 == 268) {
                this.setCursorToStart(Screen.hasShiftDown());
                return true;
            }
            if ($$0 == 269) {
                this.setCursorToEnd(Screen.hasShiftDown());
                return true;
            }
        }
        return false;
    }

    private int clampToMsgLength(int $$0) {
        return Mth.clamp($$0, 0, this.getMessageFn.get().length());
    }

    private void insertText(String $$0, String $$1) {
        if (this.selectionPos != this.cursorPos) {
            $$0 = this.deleteSelection($$0);
        }
        this.cursorPos = Mth.clamp(this.cursorPos, 0, $$0.length());
        String $$2 = new StringBuilder($$0).insert(this.cursorPos, $$1).toString();
        if (this.stringValidator.test($$2)) {
            this.setMessageFn.accept($$2);
            this.selectionPos = this.cursorPos = Math.min($$2.length(), this.cursorPos + $$1.length());
        }
    }

    public void insertText(String $$0) {
        this.insertText(this.getMessageFn.get(), $$0);
    }

    private void resetSelectionIfNeeded(boolean $$0) {
        if (!$$0) {
            this.selectionPos = this.cursorPos;
        }
    }

    public void moveBy(int $$0, boolean $$1, CursorStep $$2) {
        switch ($$2.ordinal()) {
            case 0: {
                this.moveByChars($$0, $$1);
                break;
            }
            case 1: {
                this.moveByWords($$0, $$1);
            }
        }
    }

    public void moveByChars(int $$0) {
        this.moveByChars($$0, false);
    }

    public void moveByChars(int $$0, boolean $$1) {
        this.cursorPos = Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, $$0);
        this.resetSelectionIfNeeded($$1);
    }

    public void moveByWords(int $$0) {
        this.moveByWords($$0, false);
    }

    public void moveByWords(int $$0, boolean $$1) {
        this.cursorPos = StringSplitter.getWordPosition(this.getMessageFn.get(), $$0, this.cursorPos, true);
        this.resetSelectionIfNeeded($$1);
    }

    public void removeFromCursor(int $$0, CursorStep $$1) {
        switch ($$1.ordinal()) {
            case 0: {
                this.removeCharsFromCursor($$0);
                break;
            }
            case 1: {
                this.removeWordsFromCursor($$0);
            }
        }
    }

    public void removeWordsFromCursor(int $$0) {
        int $$1 = StringSplitter.getWordPosition(this.getMessageFn.get(), $$0, this.cursorPos, true);
        this.removeCharsFromCursor($$1 - this.cursorPos);
    }

    public void removeCharsFromCursor(int $$0) {
        String $$1 = this.getMessageFn.get();
        if (!$$1.isEmpty()) {
            String $$6;
            if (this.selectionPos != this.cursorPos) {
                String $$2 = this.deleteSelection($$1);
            } else {
                int $$3 = Util.offsetByCodepoints($$1, this.cursorPos, $$0);
                int $$4 = Math.min($$3, this.cursorPos);
                int $$5 = Math.max($$3, this.cursorPos);
                $$6 = new StringBuilder($$1).delete($$4, $$5).toString();
                if ($$0 < 0) {
                    this.selectionPos = this.cursorPos = $$4;
                }
            }
            this.setMessageFn.accept($$6);
        }
    }

    public void cut() {
        String $$0 = this.getMessageFn.get();
        this.setClipboardFn.accept(this.getSelected($$0));
        this.setMessageFn.accept(this.deleteSelection($$0));
    }

    public void paste() {
        this.insertText(this.getMessageFn.get(), this.getClipboardFn.get());
        this.selectionPos = this.cursorPos;
    }

    public void copy() {
        this.setClipboardFn.accept(this.getSelected(this.getMessageFn.get()));
    }

    public void selectAll() {
        this.selectionPos = 0;
        this.cursorPos = this.getMessageFn.get().length();
    }

    private String getSelected(String $$0) {
        int $$1 = Math.min(this.cursorPos, this.selectionPos);
        int $$2 = Math.max(this.cursorPos, this.selectionPos);
        return $$0.substring($$1, $$2);
    }

    private String deleteSelection(String $$0) {
        if (this.selectionPos == this.cursorPos) {
            return $$0;
        }
        int $$1 = Math.min(this.cursorPos, this.selectionPos);
        int $$2 = Math.max(this.cursorPos, this.selectionPos);
        String $$3 = $$0.substring(0, $$1) + $$0.substring($$2);
        this.selectionPos = this.cursorPos = $$1;
        return $$3;
    }

    public void setCursorToStart() {
        this.setCursorToStart(false);
    }

    public void setCursorToStart(boolean $$0) {
        this.cursorPos = 0;
        this.resetSelectionIfNeeded($$0);
    }

    public void setCursorToEnd() {
        this.setCursorToEnd(false);
    }

    public void setCursorToEnd(boolean $$0) {
        this.cursorPos = this.getMessageFn.get().length();
        this.resetSelectionIfNeeded($$0);
    }

    public int getCursorPos() {
        return this.cursorPos;
    }

    public void setCursorPos(int $$0) {
        this.setCursorPos($$0, true);
    }

    public void setCursorPos(int $$0, boolean $$1) {
        this.cursorPos = this.clampToMsgLength($$0);
        this.resetSelectionIfNeeded($$1);
    }

    public int getSelectionPos() {
        return this.selectionPos;
    }

    public void setSelectionPos(int $$0) {
        this.selectionPos = this.clampToMsgLength($$0);
    }

    public void setSelectionRange(int $$0, int $$1) {
        int $$2 = this.getMessageFn.get().length();
        this.cursorPos = Mth.clamp($$0, 0, $$2);
        this.selectionPos = Mth.clamp($$1, 0, $$2);
    }

    public boolean isSelecting() {
        return this.cursorPos != this.selectionPos;
    }

    public static final class CursorStep
    extends Enum<CursorStep> {
        public static final /* enum */ CursorStep CHARACTER = new CursorStep();
        public static final /* enum */ CursorStep WORD = new CursorStep();
        private static final /* synthetic */ CursorStep[] $VALUES;

        public static CursorStep[] values() {
            return (CursorStep[])$VALUES.clone();
        }

        public static CursorStep valueOf(String $$0) {
            return Enum.valueOf(CursorStep.class, $$0);
        }

        private static /* synthetic */ CursorStep[] a() {
            return new CursorStep[]{CHARACTER, WORD};
        }

        static {
            $VALUES = CursorStep.a();
        }
    }
}

