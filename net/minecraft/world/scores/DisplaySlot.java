/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.scores;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class DisplaySlot
extends Enum<DisplaySlot>
implements StringRepresentable {
    public static final /* enum */ DisplaySlot LIST = new DisplaySlot(0, "list");
    public static final /* enum */ DisplaySlot SIDEBAR = new DisplaySlot(1, "sidebar");
    public static final /* enum */ DisplaySlot BELOW_NAME = new DisplaySlot(2, "below_name");
    public static final /* enum */ DisplaySlot TEAM_BLACK = new DisplaySlot(3, "sidebar.team.black");
    public static final /* enum */ DisplaySlot TEAM_DARK_BLUE = new DisplaySlot(4, "sidebar.team.dark_blue");
    public static final /* enum */ DisplaySlot TEAM_DARK_GREEN = new DisplaySlot(5, "sidebar.team.dark_green");
    public static final /* enum */ DisplaySlot TEAM_DARK_AQUA = new DisplaySlot(6, "sidebar.team.dark_aqua");
    public static final /* enum */ DisplaySlot TEAM_DARK_RED = new DisplaySlot(7, "sidebar.team.dark_red");
    public static final /* enum */ DisplaySlot TEAM_DARK_PURPLE = new DisplaySlot(8, "sidebar.team.dark_purple");
    public static final /* enum */ DisplaySlot TEAM_GOLD = new DisplaySlot(9, "sidebar.team.gold");
    public static final /* enum */ DisplaySlot TEAM_GRAY = new DisplaySlot(10, "sidebar.team.gray");
    public static final /* enum */ DisplaySlot TEAM_DARK_GRAY = new DisplaySlot(11, "sidebar.team.dark_gray");
    public static final /* enum */ DisplaySlot TEAM_BLUE = new DisplaySlot(12, "sidebar.team.blue");
    public static final /* enum */ DisplaySlot TEAM_GREEN = new DisplaySlot(13, "sidebar.team.green");
    public static final /* enum */ DisplaySlot TEAM_AQUA = new DisplaySlot(14, "sidebar.team.aqua");
    public static final /* enum */ DisplaySlot TEAM_RED = new DisplaySlot(15, "sidebar.team.red");
    public static final /* enum */ DisplaySlot TEAM_LIGHT_PURPLE = new DisplaySlot(16, "sidebar.team.light_purple");
    public static final /* enum */ DisplaySlot TEAM_YELLOW = new DisplaySlot(17, "sidebar.team.yellow");
    public static final /* enum */ DisplaySlot TEAM_WHITE = new DisplaySlot(18, "sidebar.team.white");
    public static final StringRepresentable.EnumCodec<DisplaySlot> CODEC;
    public static final IntFunction<DisplaySlot> BY_ID;
    private final int id;
    private final String name;
    private static final /* synthetic */ DisplaySlot[] $VALUES;

    public static DisplaySlot[] values() {
        return (DisplaySlot[])$VALUES.clone();
    }

    public static DisplaySlot valueOf(String $$0) {
        return Enum.valueOf(DisplaySlot.class, $$0);
    }

    private DisplaySlot(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    public int id() {
        return this.id;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Nullable
    public static DisplaySlot teamColorToSlot(ChatFormatting $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case ChatFormatting.BLACK -> TEAM_BLACK;
            case ChatFormatting.DARK_BLUE -> TEAM_DARK_BLUE;
            case ChatFormatting.DARK_GREEN -> TEAM_DARK_GREEN;
            case ChatFormatting.DARK_AQUA -> TEAM_DARK_AQUA;
            case ChatFormatting.DARK_RED -> TEAM_DARK_RED;
            case ChatFormatting.DARK_PURPLE -> TEAM_DARK_PURPLE;
            case ChatFormatting.GOLD -> TEAM_GOLD;
            case ChatFormatting.GRAY -> TEAM_GRAY;
            case ChatFormatting.DARK_GRAY -> TEAM_DARK_GRAY;
            case ChatFormatting.BLUE -> TEAM_BLUE;
            case ChatFormatting.GREEN -> TEAM_GREEN;
            case ChatFormatting.AQUA -> TEAM_AQUA;
            case ChatFormatting.RED -> TEAM_RED;
            case ChatFormatting.LIGHT_PURPLE -> TEAM_LIGHT_PURPLE;
            case ChatFormatting.YELLOW -> TEAM_YELLOW;
            case ChatFormatting.WHITE -> TEAM_WHITE;
            case ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE, ChatFormatting.RESET, ChatFormatting.OBFUSCATED, ChatFormatting.STRIKETHROUGH -> null;
        };
    }

    private static /* synthetic */ DisplaySlot[] b() {
        return new DisplaySlot[]{LIST, SIDEBAR, BELOW_NAME, TEAM_BLACK, TEAM_DARK_BLUE, TEAM_DARK_GREEN, TEAM_DARK_AQUA, TEAM_DARK_RED, TEAM_DARK_PURPLE, TEAM_GOLD, TEAM_GRAY, TEAM_DARK_GRAY, TEAM_BLUE, TEAM_GREEN, TEAM_AQUA, TEAM_RED, TEAM_LIGHT_PURPLE, TEAM_YELLOW, TEAM_WHITE};
    }

    static {
        $VALUES = DisplaySlot.b();
        CODEC = StringRepresentable.fromEnum(DisplaySlot::values);
        BY_ID = ByIdMap.a(DisplaySlot::id, DisplaySlot.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }
}

