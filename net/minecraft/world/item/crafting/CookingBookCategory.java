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

public final class CookingBookCategory
extends Enum<CookingBookCategory>
implements StringRepresentable {
    public static final /* enum */ CookingBookCategory FOOD = new CookingBookCategory(0, "food");
    public static final /* enum */ CookingBookCategory BLOCKS = new CookingBookCategory(1, "blocks");
    public static final /* enum */ CookingBookCategory MISC = new CookingBookCategory(2, "misc");
    private static final IntFunction<CookingBookCategory> BY_ID;
    public static final Codec<CookingBookCategory> CODEC;
    public static final StreamCodec<ByteBuf, CookingBookCategory> STREAM_CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ CookingBookCategory[] $VALUES;

    public static CookingBookCategory[] values() {
        return (CookingBookCategory[])$VALUES.clone();
    }

    public static CookingBookCategory valueOf(String $$0) {
        return Enum.valueOf(CookingBookCategory.class, $$0);
    }

    private CookingBookCategory(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ CookingBookCategory[] a() {
        return new CookingBookCategory[]{FOOD, BLOCKS, MISC};
    }

    static {
        $VALUES = CookingBookCategory.a();
        BY_ID = ByIdMap.a($$0 -> $$0.id, CookingBookCategory.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(CookingBookCategory::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }
}

