/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.material;

import com.google.common.base.Preconditions;
import net.minecraft.util.ARGB;

public class MapColor {
    private static final MapColor[] MATERIAL_COLORS = new MapColor[64];
    public static final MapColor NONE = new MapColor(0, 0);
    public static final MapColor GRASS = new MapColor(1, 8368696);
    public static final MapColor SAND = new MapColor(2, 16247203);
    public static final MapColor WOOL = new MapColor(3, 0xC7C7C7);
    public static final MapColor FIRE = new MapColor(4, 0xFF0000);
    public static final MapColor ICE = new MapColor(5, 0xA0A0FF);
    public static final MapColor METAL = new MapColor(6, 0xA7A7A7);
    public static final MapColor PLANT = new MapColor(7, 31744);
    public static final MapColor SNOW = new MapColor(8, 0xFFFFFF);
    public static final MapColor CLAY = new MapColor(9, 10791096);
    public static final MapColor DIRT = new MapColor(10, 9923917);
    public static final MapColor STONE = new MapColor(11, 0x707070);
    public static final MapColor WATER = new MapColor(12, 0x4040FF);
    public static final MapColor WOOD = new MapColor(13, 9402184);
    public static final MapColor QUARTZ = new MapColor(14, 0xFFFCF5);
    public static final MapColor COLOR_ORANGE = new MapColor(15, 14188339);
    public static final MapColor COLOR_MAGENTA = new MapColor(16, 11685080);
    public static final MapColor COLOR_LIGHT_BLUE = new MapColor(17, 6724056);
    public static final MapColor COLOR_YELLOW = new MapColor(18, 0xE5E533);
    public static final MapColor COLOR_LIGHT_GREEN = new MapColor(19, 8375321);
    public static final MapColor COLOR_PINK = new MapColor(20, 15892389);
    public static final MapColor COLOR_GRAY = new MapColor(21, 0x4C4C4C);
    public static final MapColor COLOR_LIGHT_GRAY = new MapColor(22, 0x999999);
    public static final MapColor COLOR_CYAN = new MapColor(23, 5013401);
    public static final MapColor COLOR_PURPLE = new MapColor(24, 8339378);
    public static final MapColor COLOR_BLUE = new MapColor(25, 3361970);
    public static final MapColor COLOR_BROWN = new MapColor(26, 6704179);
    public static final MapColor COLOR_GREEN = new MapColor(27, 6717235);
    public static final MapColor COLOR_RED = new MapColor(28, 0x993333);
    public static final MapColor COLOR_BLACK = new MapColor(29, 0x191919);
    public static final MapColor GOLD = new MapColor(30, 16445005);
    public static final MapColor DIAMOND = new MapColor(31, 6085589);
    public static final MapColor LAPIS = new MapColor(32, 4882687);
    public static final MapColor EMERALD = new MapColor(33, 55610);
    public static final MapColor PODZOL = new MapColor(34, 8476209);
    public static final MapColor NETHER = new MapColor(35, 0x700200);
    public static final MapColor TERRACOTTA_WHITE = new MapColor(36, 13742497);
    public static final MapColor TERRACOTTA_ORANGE = new MapColor(37, 10441252);
    public static final MapColor TERRACOTTA_MAGENTA = new MapColor(38, 9787244);
    public static final MapColor TERRACOTTA_LIGHT_BLUE = new MapColor(39, 7367818);
    public static final MapColor TERRACOTTA_YELLOW = new MapColor(40, 12223780);
    public static final MapColor TERRACOTTA_LIGHT_GREEN = new MapColor(41, 6780213);
    public static final MapColor TERRACOTTA_PINK = new MapColor(42, 10505550);
    public static final MapColor TERRACOTTA_GRAY = new MapColor(43, 0x392923);
    public static final MapColor TERRACOTTA_LIGHT_GRAY = new MapColor(44, 8874850);
    public static final MapColor TERRACOTTA_CYAN = new MapColor(45, 0x575C5C);
    public static final MapColor TERRACOTTA_PURPLE = new MapColor(46, 8014168);
    public static final MapColor TERRACOTTA_BLUE = new MapColor(47, 4996700);
    public static final MapColor TERRACOTTA_BROWN = new MapColor(48, 4993571);
    public static final MapColor TERRACOTTA_GREEN = new MapColor(49, 5001770);
    public static final MapColor TERRACOTTA_RED = new MapColor(50, 9321518);
    public static final MapColor TERRACOTTA_BLACK = new MapColor(51, 2430480);
    public static final MapColor CRIMSON_NYLIUM = new MapColor(52, 12398641);
    public static final MapColor CRIMSON_STEM = new MapColor(53, 9715553);
    public static final MapColor CRIMSON_HYPHAE = new MapColor(54, 6035741);
    public static final MapColor WARPED_NYLIUM = new MapColor(55, 1474182);
    public static final MapColor WARPED_STEM = new MapColor(56, 3837580);
    public static final MapColor WARPED_HYPHAE = new MapColor(57, 5647422);
    public static final MapColor WARPED_WART_BLOCK = new MapColor(58, 1356933);
    public static final MapColor DEEPSLATE = new MapColor(59, 0x646464);
    public static final MapColor RAW_IRON = new MapColor(60, 14200723);
    public static final MapColor GLOW_LICHEN = new MapColor(61, 8365974);
    public final int col;
    public final int id;

    private MapColor(int $$0, int $$1) {
        if ($$0 < 0 || $$0 > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.id = $$0;
        this.col = $$1;
        MapColor.MATERIAL_COLORS[$$0] = this;
    }

    public int calculateARGBColor(Brightness $$0) {
        if (this == NONE) {
            return 0;
        }
        return ARGB.scaleRGB(ARGB.opaque(this.col), $$0.modifier);
    }

    public static MapColor byId(int $$0) {
        Preconditions.checkPositionIndex($$0, MATERIAL_COLORS.length, "material id");
        return MapColor.byIdUnsafe($$0);
    }

    private static MapColor byIdUnsafe(int $$0) {
        MapColor $$1 = MATERIAL_COLORS[$$0];
        return $$1 != null ? $$1 : NONE;
    }

    public static int getColorFromPackedId(int $$0) {
        int $$1 = $$0 & 0xFF;
        return MapColor.byIdUnsafe($$1 >> 2).calculateARGBColor(Brightness.byIdUnsafe($$1 & 3));
    }

    public byte getPackedId(Brightness $$0) {
        return (byte)(this.id << 2 | $$0.id & 3);
    }

    public static final class Brightness
    extends Enum<Brightness> {
        public static final /* enum */ Brightness LOW = new Brightness(0, 180);
        public static final /* enum */ Brightness NORMAL = new Brightness(1, 220);
        public static final /* enum */ Brightness HIGH = new Brightness(2, 255);
        public static final /* enum */ Brightness LOWEST = new Brightness(3, 135);
        private static final Brightness[] VALUES;
        public final int id;
        public final int modifier;
        private static final /* synthetic */ Brightness[] $VALUES;

        public static Brightness[] values() {
            return (Brightness[])$VALUES.clone();
        }

        public static Brightness valueOf(String $$0) {
            return Enum.valueOf(Brightness.class, $$0);
        }

        private Brightness(int $$0, int $$1) {
            this.id = $$0;
            this.modifier = $$1;
        }

        public static Brightness byId(int $$0) {
            Preconditions.checkPositionIndex($$0, VALUES.length, "brightness id");
            return Brightness.byIdUnsafe($$0);
        }

        static Brightness byIdUnsafe(int $$0) {
            return VALUES[$$0];
        }

        private static /* synthetic */ Brightness[] a() {
            return new Brightness[]{LOW, NORMAL, HIGH, LOWEST};
        }

        static {
            $VALUES = Brightness.a();
            VALUES = new Brightness[]{LOW, NORMAL, HIGH, LOWEST};
        }
    }
}

