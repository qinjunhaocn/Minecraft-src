/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;

public final class ChatFormatting
extends Enum<ChatFormatting>
implements StringRepresentable {
    public static final /* enum */ ChatFormatting BLACK = new ChatFormatting("BLACK", '0', 0, 0);
    public static final /* enum */ ChatFormatting DARK_BLUE = new ChatFormatting("DARK_BLUE", '1', 1, 170);
    public static final /* enum */ ChatFormatting DARK_GREEN = new ChatFormatting("DARK_GREEN", '2', 2, 43520);
    public static final /* enum */ ChatFormatting DARK_AQUA = new ChatFormatting("DARK_AQUA", '3', 3, 43690);
    public static final /* enum */ ChatFormatting DARK_RED = new ChatFormatting("DARK_RED", '4', 4, 0xAA0000);
    public static final /* enum */ ChatFormatting DARK_PURPLE = new ChatFormatting("DARK_PURPLE", '5', 5, 0xAA00AA);
    public static final /* enum */ ChatFormatting GOLD = new ChatFormatting("GOLD", '6', 6, 0xFFAA00);
    public static final /* enum */ ChatFormatting GRAY = new ChatFormatting("GRAY", '7', 7, 0xAAAAAA);
    public static final /* enum */ ChatFormatting DARK_GRAY = new ChatFormatting("DARK_GRAY", '8', 8, 0x555555);
    public static final /* enum */ ChatFormatting BLUE = new ChatFormatting("BLUE", '9', 9, 0x5555FF);
    public static final /* enum */ ChatFormatting GREEN = new ChatFormatting("GREEN", 'a', 10, 0x55FF55);
    public static final /* enum */ ChatFormatting AQUA = new ChatFormatting("AQUA", 'b', 11, 0x55FFFF);
    public static final /* enum */ ChatFormatting RED = new ChatFormatting("RED", 'c', 12, 0xFF5555);
    public static final /* enum */ ChatFormatting LIGHT_PURPLE = new ChatFormatting("LIGHT_PURPLE", 'd', 13, 0xFF55FF);
    public static final /* enum */ ChatFormatting YELLOW = new ChatFormatting("YELLOW", 'e', 14, 0xFFFF55);
    public static final /* enum */ ChatFormatting WHITE = new ChatFormatting("WHITE", 'f', 15, 0xFFFFFF);
    public static final /* enum */ ChatFormatting OBFUSCATED = new ChatFormatting("OBFUSCATED", 'k', true);
    public static final /* enum */ ChatFormatting BOLD = new ChatFormatting("BOLD", 'l', true);
    public static final /* enum */ ChatFormatting STRIKETHROUGH = new ChatFormatting("STRIKETHROUGH", 'm', true);
    public static final /* enum */ ChatFormatting UNDERLINE = new ChatFormatting("UNDERLINE", 'n', true);
    public static final /* enum */ ChatFormatting ITALIC = new ChatFormatting("ITALIC", 'o', true);
    public static final /* enum */ ChatFormatting RESET = new ChatFormatting("RESET", 'r', -1, null);
    public static final Codec<ChatFormatting> CODEC;
    public static final Codec<ChatFormatting> COLOR_CODEC;
    public static final char PREFIX_CODE = '\u00a7';
    private static final Map<String, ChatFormatting> FORMATTING_BY_NAME;
    private static final Pattern STRIP_FORMATTING_PATTERN;
    private final String name;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final int id;
    @Nullable
    private final Integer color;
    private static final /* synthetic */ ChatFormatting[] $VALUES;

    public static ChatFormatting[] values() {
        return (ChatFormatting[])$VALUES.clone();
    }

    public static ChatFormatting valueOf(String $$0) {
        return Enum.valueOf(ChatFormatting.class, $$0);
    }

    private static String cleanName(String $$0) {
        return $$0.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    private ChatFormatting(String $$0, @Nullable char $$1, int $$2, Integer $$3) {
        this($$0, $$1, false, $$2, $$3);
    }

    private ChatFormatting(String $$0, char $$1, boolean $$2) {
        this($$0, $$1, $$2, -1, null);
    }

    private ChatFormatting(String $$0, char $$1, @Nullable boolean $$2, int $$3, Integer $$4) {
        this.name = $$0;
        this.code = $$1;
        this.isFormat = $$2;
        this.id = $$3;
        this.color = $$4;
        this.toString = "\u00a7" + String.valueOf($$1);
    }

    public char a() {
        return this.code;
    }

    public int getId() {
        return this.id;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    @Nullable
    public Integer getColor() {
        return this.color;
    }

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.toString;
    }

    @Nullable
    @Contract(value="!null->!null;_->_")
    public static String stripFormatting(@Nullable String $$0) {
        return $$0 == null ? null : STRIP_FORMATTING_PATTERN.matcher($$0).replaceAll("");
    }

    @Nullable
    public static ChatFormatting getByName(@Nullable String $$0) {
        if ($$0 == null) {
            return null;
        }
        return FORMATTING_BY_NAME.get(ChatFormatting.cleanName($$0));
    }

    @Nullable
    public static ChatFormatting getById(int $$0) {
        if ($$0 < 0) {
            return RESET;
        }
        for (ChatFormatting $$1 : ChatFormatting.values()) {
            if ($$1.getId() != $$0) continue;
            return $$1;
        }
        return null;
    }

    @Nullable
    public static ChatFormatting a(char $$0) {
        char $$1 = Character.toLowerCase($$0);
        for (ChatFormatting $$2 : ChatFormatting.values()) {
            if ($$2.code != $$1) continue;
            return $$2;
        }
        return null;
    }

    public static Collection<String> getNames(boolean $$0, boolean $$1) {
        ArrayList<String> $$2 = Lists.newArrayList();
        for (ChatFormatting $$3 : ChatFormatting.values()) {
            if ($$3.isColor() && !$$0 || $$3.isFormat() && !$$1) continue;
            $$2.add($$3.getName());
        }
        return $$2;
    }

    @Override
    public String getSerializedName() {
        return this.getName();
    }

    private static /* synthetic */ ChatFormatting[] h() {
        return new ChatFormatting[]{BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET};
    }

    static {
        $VALUES = ChatFormatting.h();
        CODEC = StringRepresentable.fromEnum(ChatFormatting::values);
        COLOR_CODEC = CODEC.validate($$0 -> $$0.isFormat() ? DataResult.error(() -> "Formatting was not a valid color: " + String.valueOf($$0)) : DataResult.success((Object)$$0));
        FORMATTING_BY_NAME = Arrays.stream(ChatFormatting.values()).collect(Collectors.toMap($$0 -> ChatFormatting.cleanName($$0.name), $$0 -> $$0));
        STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    }
}

