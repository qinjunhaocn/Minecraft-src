/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Contract;

public final class DyeColor
extends Enum<DyeColor>
implements StringRepresentable {
    public static final /* enum */ DyeColor WHITE = new DyeColor(0, "white", 0xF9FFFE, MapColor.SNOW, 0xF0F0F0, 0xFFFFFF);
    public static final /* enum */ DyeColor ORANGE = new DyeColor(1, "orange", 16351261, MapColor.COLOR_ORANGE, 15435844, 16738335);
    public static final /* enum */ DyeColor MAGENTA = new DyeColor(2, "magenta", 13061821, MapColor.COLOR_MAGENTA, 12801229, 0xFF00FF);
    public static final /* enum */ DyeColor LIGHT_BLUE = new DyeColor(3, "light_blue", 3847130, MapColor.COLOR_LIGHT_BLUE, 6719955, 10141901);
    public static final /* enum */ DyeColor YELLOW = new DyeColor(4, "yellow", 16701501, MapColor.COLOR_YELLOW, 14602026, 0xFFFF00);
    public static final /* enum */ DyeColor LIME = new DyeColor(5, "lime", 8439583, MapColor.COLOR_LIGHT_GREEN, 4312372, 0xBFFF00);
    public static final /* enum */ DyeColor PINK = new DyeColor(6, "pink", 15961002, MapColor.COLOR_PINK, 14188952, 16738740);
    public static final /* enum */ DyeColor GRAY = new DyeColor(7, "gray", 4673362, MapColor.COLOR_GRAY, 0x434343, 0x808080);
    public static final /* enum */ DyeColor LIGHT_GRAY = new DyeColor(8, "light_gray", 0x9D9D97, MapColor.COLOR_LIGHT_GRAY, 0xABABAB, 0xD3D3D3);
    public static final /* enum */ DyeColor CYAN = new DyeColor(9, "cyan", 1481884, MapColor.COLOR_CYAN, 2651799, 65535);
    public static final /* enum */ DyeColor PURPLE = new DyeColor(10, "purple", 8991416, MapColor.COLOR_PURPLE, 8073150, 10494192);
    public static final /* enum */ DyeColor BLUE = new DyeColor(11, "blue", 3949738, MapColor.COLOR_BLUE, 2437522, 255);
    public static final /* enum */ DyeColor BROWN = new DyeColor(12, "brown", 8606770, MapColor.COLOR_BROWN, 5320730, 9127187);
    public static final /* enum */ DyeColor GREEN = new DyeColor(13, "green", 6192150, MapColor.COLOR_GREEN, 3887386, 65280);
    public static final /* enum */ DyeColor RED = new DyeColor(14, "red", 11546150, MapColor.COLOR_RED, 11743532, 0xFF0000);
    public static final /* enum */ DyeColor BLACK = new DyeColor(15, "black", 0x1D1D21, MapColor.COLOR_BLACK, 0x1E1B1B, 0);
    private static final IntFunction<DyeColor> BY_ID;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    public static final StringRepresentable.EnumCodec<DyeColor> CODEC;
    public static final StreamCodec<ByteBuf, DyeColor> STREAM_CODEC;
    @Deprecated
    public static final Codec<DyeColor> LEGACY_ID_CODEC;
    private final int id;
    private final String name;
    private final MapColor mapColor;
    private final int textureDiffuseColor;
    private final int fireworkColor;
    private final int textColor;
    private static final /* synthetic */ DyeColor[] $VALUES;

    public static DyeColor[] values() {
        return (DyeColor[])$VALUES.clone();
    }

    public static DyeColor valueOf(String $$0) {
        return Enum.valueOf(DyeColor.class, $$0);
    }

    private DyeColor(int $$0, String $$1, int $$2, MapColor $$3, int $$4, int $$5) {
        this.id = $$0;
        this.name = $$1;
        this.mapColor = $$3;
        this.textColor = ARGB.opaque($$5);
        this.textureDiffuseColor = ARGB.opaque($$2);
        this.fireworkColor = $$4;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getTextureDiffuseColor() {
        return this.textureDiffuseColor;
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public static DyeColor byId(int $$0) {
        return BY_ID.apply($$0);
    }

    @Nullable
    @Contract(value="_,!null->!null;_,null->_")
    public static DyeColor byName(String $$0, @Nullable DyeColor $$1) {
        DyeColor $$2 = CODEC.byName($$0);
        return $$2 != null ? $$2 : $$1;
    }

    @Nullable
    public static DyeColor byFireworkColor(int $$0) {
        return (DyeColor)BY_FIREWORK_COLOR.get($$0);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static DyeColor getMixedColor(ServerLevel $$0, DyeColor $$1, DyeColor $$22) {
        CraftingInput $$3 = DyeColor.makeCraftColorInput($$1, $$22);
        return $$0.recipeAccess().getRecipeFor(RecipeType.CRAFTING, $$3, $$0).map($$2 -> ((CraftingRecipe)$$2.value()).assemble($$3, $$0.registryAccess())).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> $$0.random.nextBoolean() ? $$1 : $$22);
    }

    private static CraftingInput makeCraftColorInput(DyeColor $$0, DyeColor $$1) {
        return CraftingInput.of(2, 1, List.of((Object)new ItemStack(DyeItem.byColor($$0)), (Object)new ItemStack(DyeItem.byColor($$1))));
    }

    private static /* synthetic */ DyeColor[] h() {
        return new DyeColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
    }

    static {
        $VALUES = DyeColor.h();
        BY_ID = ByIdMap.a(DyeColor::getId, DyeColor.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap($$0 -> $$0.fireworkColor, $$0 -> $$0)));
        CODEC = StringRepresentable.fromEnum(DyeColor::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DyeColor::getId);
        LEGACY_ID_CODEC = Codec.BYTE.xmap(DyeColor::byId, $$0 -> (byte)$$0.id);
    }
}

