/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class CraftingBookCategory
extends Enum<CraftingBookCategory>
implements StringRepresentable {
    public static final /* enum */ CraftingBookCategory BUILDING = new CraftingBookCategory("building", 0);
    public static final /* enum */ CraftingBookCategory REDSTONE = new CraftingBookCategory("redstone", 1);
    public static final /* enum */ CraftingBookCategory EQUIPMENT = new CraftingBookCategory("equipment", 2);
    public static final /* enum */ CraftingBookCategory MISC = new CraftingBookCategory("misc", 3);
    public static final Codec<CraftingBookCategory> CODEC;
    public static final IntFunction<CraftingBookCategory> BY_ID;
    public static final StreamCodec<ByteBuf, CraftingBookCategory> STREAM_CODEC;
    private final String name;
    private final int id;
    private static final /* synthetic */ CraftingBookCategory[] $VALUES;

    public static CraftingBookCategory[] values() {
        return (CraftingBookCategory[])$VALUES.clone();
    }

    public static CraftingBookCategory valueOf(String $$0) {
        return Enum.valueOf(CraftingBookCategory.class, $$0);
    }

    private CraftingBookCategory(String $$0, int $$1) {
        this.name = $$0;
        this.id = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private int id() {
        return this.id;
    }

    private static /* synthetic */ CraftingBookCategory[] b() {
        return new CraftingBookCategory[]{BUILDING, REDSTONE, EQUIPMENT, MISC};
    }

    static {
        $VALUES = CraftingBookCategory.b();
        CODEC = StringRepresentable.fromEnum(CraftingBookCategory::values);
        BY_ID = ByIdMap.a(CraftingBookCategory::id, CraftingBookCategory.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CraftingBookCategory::id);
    }
}

