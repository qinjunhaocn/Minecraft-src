/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.ComponentCollector;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class StringSplitter {
    final WidthProvider widthProvider;

    public StringSplitter(WidthProvider $$0) {
        this.widthProvider = $$0;
    }

    public float stringWidth(@Nullable String $$0) {
        if ($$0 == null) {
            return 0.0f;
        }
        MutableFloat $$12 = new MutableFloat();
        StringDecomposer.iterateFormatted($$0, Style.EMPTY, ($$1, $$2, $$3) -> {
            $$12.add(this.widthProvider.getWidth($$3, $$2));
            return true;
        });
        return $$12.floatValue();
    }

    public float stringWidth(FormattedText $$0) {
        MutableFloat $$12 = new MutableFloat();
        StringDecomposer.iterateFormatted($$0, Style.EMPTY, ($$1, $$2, $$3) -> {
            $$12.add(this.widthProvider.getWidth($$3, $$2));
            return true;
        });
        return $$12.floatValue();
    }

    public float stringWidth(FormattedCharSequence $$0) {
        MutableFloat $$12 = new MutableFloat();
        $$0.accept(($$1, $$2, $$3) -> {
            $$12.add(this.widthProvider.getWidth($$3, $$2));
            return true;
        });
        return $$12.floatValue();
    }

    public int plainIndexAtWidth(String $$0, int $$1, Style $$2) {
        WidthLimitedCharSink $$3 = new WidthLimitedCharSink($$1);
        StringDecomposer.iterate($$0, $$2, $$3);
        return $$3.getPosition();
    }

    public String plainHeadByWidth(String $$0, int $$1, Style $$2) {
        return $$0.substring(0, this.plainIndexAtWidth($$0, $$1, $$2));
    }

    public String plainTailByWidth(String $$0, int $$1, Style $$2) {
        MutableFloat $$32 = new MutableFloat();
        MutableInt $$42 = new MutableInt($$0.length());
        StringDecomposer.iterateBackwards($$0, $$2, ($$3, $$4, $$5) -> {
            float $$6 = $$32.addAndGet(this.widthProvider.getWidth($$5, $$4));
            if ($$6 > (float)$$1) {
                return false;
            }
            $$42.setValue($$3);
            return true;
        });
        return $$0.substring($$42.intValue());
    }

    public int formattedIndexByWidth(String $$0, int $$1, Style $$2) {
        WidthLimitedCharSink $$3 = new WidthLimitedCharSink($$1);
        StringDecomposer.iterateFormatted($$0, $$2, (FormattedCharSink)$$3);
        return $$3.getPosition();
    }

    @Nullable
    public Style componentStyleAtWidth(FormattedText $$0, int $$12) {
        WidthLimitedCharSink $$22 = new WidthLimitedCharSink($$12);
        return $$0.visit(($$1, $$2) -> StringDecomposer.iterateFormatted($$2, $$1, (FormattedCharSink)$$22) ? Optional.empty() : Optional.of($$1), Style.EMPTY).orElse(null);
    }

    @Nullable
    public Style componentStyleAtWidth(FormattedCharSequence $$0, int $$1) {
        WidthLimitedCharSink $$22 = new WidthLimitedCharSink($$1);
        MutableObject $$32 = new MutableObject();
        $$0.accept(($$2, $$3, $$4) -> {
            if (!$$22.accept($$2, $$3, $$4)) {
                $$32.setValue($$3);
                return false;
            }
            return true;
        });
        return (Style)$$32.getValue();
    }

    public String formattedHeadByWidth(String $$0, int $$1, Style $$2) {
        return $$0.substring(0, this.formattedIndexByWidth($$0, $$1, $$2));
    }

    public FormattedText headByWidth(FormattedText $$0, int $$1, Style $$2) {
        final WidthLimitedCharSink $$3 = new WidthLimitedCharSink($$1);
        return $$0.visit(new FormattedText.StyledContentConsumer<FormattedText>(){
            private final ComponentCollector collector = new ComponentCollector();

            @Override
            public Optional<FormattedText> accept(Style $$0, String $$1) {
                $$3.resetPosition();
                if (!StringDecomposer.iterateFormatted($$1, $$0, (FormattedCharSink)$$3)) {
                    String $$2 = $$1.substring(0, $$3.getPosition());
                    if (!$$2.isEmpty()) {
                        this.collector.append(FormattedText.of($$2, $$0));
                    }
                    return Optional.of(this.collector.getResultOrEmpty());
                }
                if (!$$1.isEmpty()) {
                    this.collector.append(FormattedText.of($$1, $$0));
                }
                return Optional.empty();
            }
        }, $$2).orElse($$0);
    }

    public int findLineBreak(String $$0, int $$1, Style $$2) {
        LineBreakFinder $$3 = new LineBreakFinder($$1);
        StringDecomposer.iterateFormatted($$0, $$2, (FormattedCharSink)$$3);
        return $$3.getSplitPosition();
    }

    public static int getWordPosition(String $$0, int $$1, int $$2, boolean $$3) {
        int $$4 = $$2;
        boolean $$5 = $$1 < 0;
        int $$6 = Math.abs($$1);
        for (int $$7 = 0; $$7 < $$6; ++$$7) {
            if ($$5) {
                while ($$3 && $$4 > 0 && ($$0.charAt($$4 - 1) == ' ' || $$0.charAt($$4 - 1) == '\n')) {
                    --$$4;
                }
                while ($$4 > 0 && $$0.charAt($$4 - 1) != ' ' && $$0.charAt($$4 - 1) != '\n') {
                    --$$4;
                }
                continue;
            }
            int $$8 = $$0.length();
            int $$9 = $$0.indexOf(32, $$4);
            int $$10 = $$0.indexOf(10, $$4);
            $$4 = $$9 == -1 && $$10 == -1 ? -1 : ($$9 != -1 && $$10 != -1 ? Math.min($$9, $$10) : ($$9 != -1 ? $$9 : $$10));
            if ($$4 == -1) {
                $$4 = $$8;
                continue;
            }
            while ($$3 && $$4 < $$8 && ($$0.charAt($$4) == ' ' || $$0.charAt($$4) == '\n')) {
                ++$$4;
            }
        }
        return $$4;
    }

    public void splitLines(String $$0, int $$1, Style $$2, boolean $$3, LinePosConsumer $$4) {
        int $$5 = 0;
        int $$6 = $$0.length();
        Style $$7 = $$2;
        while ($$5 < $$6) {
            LineBreakFinder $$8 = new LineBreakFinder($$1);
            boolean $$9 = StringDecomposer.iterateFormatted($$0, $$5, $$7, $$2, $$8);
            if ($$9) {
                $$4.accept($$7, $$5, $$6);
                break;
            }
            int $$10 = $$8.getSplitPosition();
            char $$11 = $$0.charAt($$10);
            int $$12 = $$11 == '\n' || $$11 == ' ' ? $$10 + 1 : $$10;
            $$4.accept($$7, $$5, $$3 ? $$12 : $$10);
            $$5 = $$12;
            $$7 = $$8.getSplitStyle();
        }
    }

    public List<FormattedText> splitLines(String $$0, int $$1, Style $$22) {
        ArrayList<FormattedText> $$32 = Lists.newArrayList();
        this.splitLines($$0, $$1, $$22, false, ($$2, $$3, $$4) -> $$32.add(FormattedText.of($$0.substring($$3, $$4), $$2)));
        return $$32;
    }

    public List<FormattedText> splitLines(FormattedText $$0, int $$12, Style $$22) {
        ArrayList<FormattedText> $$3 = Lists.newArrayList();
        this.splitLines($$0, $$12, $$22, (FormattedText $$1, Boolean $$2) -> $$3.add((FormattedText)$$1));
        return $$3;
    }

    public List<FormattedText> splitLines(FormattedText $$0, int $$1, Style $$22, FormattedText $$32) {
        ArrayList<FormattedText> $$4 = Lists.newArrayList();
        this.splitLines($$0, $$1, $$22, (FormattedText $$2, Boolean $$3) -> $$4.add($$3 != false ? FormattedText.a($$32, $$2) : $$2));
        return $$4;
    }

    public void splitLines(FormattedText $$0, int $$12, Style $$22, BiConsumer<FormattedText, Boolean> $$3) {
        ArrayList<LineComponent> $$4 = Lists.newArrayList();
        $$0.visit(($$1, $$2) -> {
            if (!$$2.isEmpty()) {
                $$4.add(new LineComponent($$2, $$1));
            }
            return Optional.empty();
        }, $$22);
        FlatComponents $$5 = new FlatComponents($$4);
        boolean $$6 = true;
        boolean $$7 = false;
        boolean $$8 = false;
        block0: while ($$6) {
            $$6 = false;
            LineBreakFinder $$9 = new LineBreakFinder($$12);
            for (LineComponent $$10 : $$5.parts) {
                boolean $$11 = StringDecomposer.iterateFormatted($$10.contents, 0, $$10.style, $$22, $$9);
                if (!$$11) {
                    int $$122 = $$9.getSplitPosition();
                    Style $$13 = $$9.getSplitStyle();
                    char $$14 = $$5.a($$122);
                    boolean $$15 = $$14 == '\n';
                    boolean $$16 = $$15 || $$14 == ' ';
                    $$7 = $$15;
                    FormattedText $$17 = $$5.splitAt($$122, $$16 ? 1 : 0, $$13);
                    $$3.accept($$17, $$8);
                    $$8 = !$$15;
                    $$6 = true;
                    continue block0;
                }
                $$9.addToOffset($$10.contents.length());
            }
        }
        FormattedText $$18 = $$5.getRemainder();
        if ($$18 != null) {
            $$3.accept($$18, $$8);
        } else if ($$7) {
            $$3.accept(FormattedText.EMPTY, false);
        }
    }

    @FunctionalInterface
    public static interface WidthProvider {
        public float getWidth(int var1, Style var2);
    }

    class WidthLimitedCharSink
    implements FormattedCharSink {
        private float maxWidth;
        private int position;

        public WidthLimitedCharSink(float $$0) {
            this.maxWidth = $$0;
        }

        @Override
        public boolean accept(int $$0, Style $$1, int $$2) {
            this.maxWidth -= StringSplitter.this.widthProvider.getWidth($$2, $$1);
            if (this.maxWidth >= 0.0f) {
                this.position = $$0 + Character.charCount($$2);
                return true;
            }
            return false;
        }

        public int getPosition() {
            return this.position;
        }

        public void resetPosition() {
            this.position = 0;
        }
    }

    class LineBreakFinder
    implements FormattedCharSink {
        private final float maxWidth;
        private int lineBreak = -1;
        private Style lineBreakStyle = Style.EMPTY;
        private boolean hadNonZeroWidthChar;
        private float width;
        private int lastSpace = -1;
        private Style lastSpaceStyle = Style.EMPTY;
        private int nextChar;
        private int offset;

        public LineBreakFinder(float $$0) {
            this.maxWidth = Math.max($$0, 1.0f);
        }

        @Override
        public boolean accept(int $$0, Style $$1, int $$2) {
            int $$3 = $$0 + this.offset;
            switch ($$2) {
                case 10: {
                    return this.finishIteration($$3, $$1);
                }
                case 32: {
                    this.lastSpace = $$3;
                    this.lastSpaceStyle = $$1;
                }
            }
            float $$4 = StringSplitter.this.widthProvider.getWidth($$2, $$1);
            this.width += $$4;
            if (this.hadNonZeroWidthChar && this.width > this.maxWidth) {
                if (this.lastSpace != -1) {
                    return this.finishIteration(this.lastSpace, this.lastSpaceStyle);
                }
                return this.finishIteration($$3, $$1);
            }
            this.hadNonZeroWidthChar |= $$4 != 0.0f;
            this.nextChar = $$3 + Character.charCount($$2);
            return true;
        }

        private boolean finishIteration(int $$0, Style $$1) {
            this.lineBreak = $$0;
            this.lineBreakStyle = $$1;
            return false;
        }

        private boolean lineBreakFound() {
            return this.lineBreak != -1;
        }

        public int getSplitPosition() {
            return this.lineBreakFound() ? this.lineBreak : this.nextChar;
        }

        public Style getSplitStyle() {
            return this.lineBreakStyle;
        }

        public void addToOffset(int $$0) {
            this.offset += $$0;
        }
    }

    @FunctionalInterface
    public static interface LinePosConsumer {
        public void accept(Style var1, int var2, int var3);
    }

    static class FlatComponents {
        final List<LineComponent> parts;
        private String flatParts;

        public FlatComponents(List<LineComponent> $$02) {
            this.parts = $$02;
            this.flatParts = $$02.stream().map($$0 -> $$0.contents).collect(Collectors.joining());
        }

        public char a(int $$0) {
            return this.flatParts.charAt($$0);
        }

        public FormattedText splitAt(int $$0, int $$1, Style $$2) {
            ComponentCollector $$3 = new ComponentCollector();
            ListIterator<LineComponent> $$4 = this.parts.listIterator();
            int $$5 = $$0;
            boolean $$6 = false;
            while ($$4.hasNext()) {
                LineComponent $$7 = $$4.next();
                String $$8 = $$7.contents;
                int $$9 = $$8.length();
                if (!$$6) {
                    if ($$5 > $$9) {
                        $$3.append($$7);
                        $$4.remove();
                        $$5 -= $$9;
                    } else {
                        String $$10 = $$8.substring(0, $$5);
                        if (!$$10.isEmpty()) {
                            $$3.append(FormattedText.of($$10, $$7.style));
                        }
                        $$5 += $$1;
                        $$6 = true;
                    }
                }
                if (!$$6) continue;
                if ($$5 > $$9) {
                    $$4.remove();
                    $$5 -= $$9;
                    continue;
                }
                String $$11 = $$8.substring($$5);
                if ($$11.isEmpty()) {
                    $$4.remove();
                    break;
                }
                $$4.set(new LineComponent($$11, $$2));
                break;
            }
            this.flatParts = this.flatParts.substring($$0 + $$1);
            return $$3.getResultOrEmpty();
        }

        @Nullable
        public FormattedText getRemainder() {
            ComponentCollector $$0 = new ComponentCollector();
            this.parts.forEach($$0::append);
            this.parts.clear();
            return $$0.getResult();
        }
    }

    static class LineComponent
    implements FormattedText {
        final String contents;
        final Style style;

        public LineComponent(String $$0, Style $$1) {
            this.contents = $$0;
            this.style = $$1;
        }

        @Override
        public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
            return $$0.accept(this.contents);
        }

        @Override
        public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
            return $$0.accept(this.style.applyTo($$1), this.contents);
        }
    }
}

