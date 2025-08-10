/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class ItemUseAnimation
extends Enum<ItemUseAnimation>
implements StringRepresentable {
    public static final /* enum */ ItemUseAnimation NONE = new ItemUseAnimation(0, "none");
    public static final /* enum */ ItemUseAnimation EAT = new ItemUseAnimation(1, "eat");
    public static final /* enum */ ItemUseAnimation DRINK = new ItemUseAnimation(2, "drink");
    public static final /* enum */ ItemUseAnimation BLOCK = new ItemUseAnimation(3, "block");
    public static final /* enum */ ItemUseAnimation BOW = new ItemUseAnimation(4, "bow");
    public static final /* enum */ ItemUseAnimation SPEAR = new ItemUseAnimation(5, "spear");
    public static final /* enum */ ItemUseAnimation CROSSBOW = new ItemUseAnimation(6, "crossbow");
    public static final /* enum */ ItemUseAnimation SPYGLASS = new ItemUseAnimation(7, "spyglass");
    public static final /* enum */ ItemUseAnimation TOOT_HORN = new ItemUseAnimation(8, "toot_horn");
    public static final /* enum */ ItemUseAnimation BRUSH = new ItemUseAnimation(9, "brush");
    public static final /* enum */ ItemUseAnimation BUNDLE = new ItemUseAnimation(10, "bundle");
    private static final IntFunction<ItemUseAnimation> BY_ID;
    public static final Codec<ItemUseAnimation> CODEC;
    public static final StreamCodec<ByteBuf, ItemUseAnimation> STREAM_CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ ItemUseAnimation[] $VALUES;

    public static ItemUseAnimation[] values() {
        return (ItemUseAnimation[])$VALUES.clone();
    }

    public static ItemUseAnimation valueOf(String $$0) {
        return Enum.valueOf(ItemUseAnimation.class, $$0);
    }

    private ItemUseAnimation(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ ItemUseAnimation[] b() {
        return new ItemUseAnimation[]{NONE, EAT, DRINK, BLOCK, BOW, SPEAR, CROSSBOW, SPYGLASS, TOOT_HORN, BRUSH, BUNDLE};
    }

    static {
        $VALUES = ItemUseAnimation.b();
        BY_ID = ByIdMap.a(ItemUseAnimation::getId, ItemUseAnimation.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(ItemUseAnimation::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ItemUseAnimation::getId);
    }
}

