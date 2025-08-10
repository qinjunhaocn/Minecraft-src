/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Unit;

public class StringDecomposer {
    private static final char REPLACEMENT_CHAR = '\ufffd';
    private static final Optional<Object> STOP_ITERATION = Optional.of(Unit.INSTANCE);

    private static boolean a(Style $$0, FormattedCharSink $$1, int $$2, char $$3) {
        if (Character.isSurrogate($$3)) {
            return $$1.accept($$2, $$0, 65533);
        }
        return $$1.accept($$2, $$0, $$3);
    }

    public static boolean iterate(String $$0, Style $$1, FormattedCharSink $$2) {
        int $$3 = $$0.length();
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            char $$5 = $$0.charAt($$4);
            if (Character.isHighSurrogate($$5)) {
                if ($$4 + 1 >= $$3) {
                    if ($$2.accept($$4, $$1, 65533)) break;
                    return false;
                }
                char $$6 = $$0.charAt($$4 + 1);
                if (Character.isLowSurrogate($$6)) {
                    if (!$$2.accept($$4, $$1, Character.toCodePoint($$5, $$6))) {
                        return false;
                    }
                    ++$$4;
                    continue;
                }
                if ($$2.accept($$4, $$1, 65533)) continue;
                return false;
            }
            if (StringDecomposer.a($$1, $$2, $$4, $$5)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateBackwards(String $$0, Style $$1, FormattedCharSink $$2) {
        int $$3 = $$0.length();
        for (int $$4 = $$3 - 1; $$4 >= 0; --$$4) {
            char $$5 = $$0.charAt($$4);
            if (Character.isLowSurrogate($$5)) {
                if ($$4 - 1 < 0) {
                    if ($$2.accept(0, $$1, 65533)) break;
                    return false;
                }
                char $$6 = $$0.charAt($$4 - 1);
                if (!(Character.isHighSurrogate($$6) ? !$$2.accept(--$$4, $$1, Character.toCodePoint($$6, $$5)) : !$$2.accept($$4, $$1, 65533))) continue;
                return false;
            }
            if (StringDecomposer.a($$1, $$2, $$4, $$5)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateFormatted(String $$0, Style $$1, FormattedCharSink $$2) {
        return StringDecomposer.iterateFormatted($$0, 0, $$1, $$2);
    }

    public static boolean iterateFormatted(String $$0, int $$1, Style $$2, FormattedCharSink $$3) {
        return StringDecomposer.iterateFormatted($$0, $$1, $$2, $$2, $$3);
    }

    public static boolean iterateFormatted(String $$0, int $$1, Style $$2, Style $$3, FormattedCharSink $$4) {
        int $$5 = $$0.length();
        Style $$6 = $$2;
        for (int $$7 = $$1; $$7 < $$5; ++$$7) {
            char $$8 = $$0.charAt($$7);
            if ($$8 == '\u00a7') {
                if ($$7 + 1 >= $$5) break;
                char $$9 = $$0.charAt($$7 + 1);
                ChatFormatting $$10 = ChatFormatting.a($$9);
                if ($$10 != null) {
                    $$6 = $$10 == ChatFormatting.RESET ? $$3 : $$6.applyLegacyFormat($$10);
                }
                ++$$7;
                continue;
            }
            if (Character.isHighSurrogate($$8)) {
                if ($$7 + 1 >= $$5) {
                    if ($$4.accept($$7, $$6, 65533)) break;
                    return false;
                }
                char $$11 = $$0.charAt($$7 + 1);
                if (Character.isLowSurrogate($$11)) {
                    if (!$$4.accept($$7, $$6, Character.toCodePoint($$8, $$11))) {
                        return false;
                    }
                    ++$$7;
                    continue;
                }
                if ($$4.accept($$7, $$6, 65533)) continue;
                return false;
            }
            if (StringDecomposer.a($$6, $$4, $$7, $$8)) continue;
            return false;
        }
        return true;
    }

    public static boolean iterateFormatted(FormattedText $$0, Style $$12, FormattedCharSink $$22) {
        return $$0.visit(($$1, $$2) -> StringDecomposer.iterateFormatted($$2, 0, $$1, $$22) ? Optional.empty() : STOP_ITERATION, $$12).isEmpty();
    }

    public static String filterBrokenSurrogates(String $$0) {
        StringBuilder $$12 = new StringBuilder();
        StringDecomposer.iterate($$0, Style.EMPTY, ($$1, $$2, $$3) -> {
            $$12.appendCodePoint($$3);
            return true;
        });
        return $$12.toString();
    }

    public static String getPlainText(FormattedText $$0) {
        StringBuilder $$12 = new StringBuilder();
        StringDecomposer.iterateFormatted($$0, Style.EMPTY, (int $$1, Style $$2, int $$3) -> {
            $$12.appendCodePoint($$3);
            return true;
        });
        return $$12.toString();
    }
}

