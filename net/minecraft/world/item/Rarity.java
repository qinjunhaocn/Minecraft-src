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
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class Rarity
extends Enum<Rarity>
implements StringRepresentable {
    public static final /* enum */ Rarity COMMON = new Rarity(0, "common", ChatFormatting.WHITE);
    public static final /* enum */ Rarity UNCOMMON = new Rarity(1, "uncommon", ChatFormatting.YELLOW);
    public static final /* enum */ Rarity RARE = new Rarity(2, "rare", ChatFormatting.AQUA);
    public static final /* enum */ Rarity EPIC = new Rarity(3, "epic", ChatFormatting.LIGHT_PURPLE);
    public static final Codec<Rarity> CODEC;
    public static final IntFunction<Rarity> BY_ID;
    public static final StreamCodec<ByteBuf, Rarity> STREAM_CODEC;
    private final int id;
    private final String name;
    private final ChatFormatting color;
    private static final /* synthetic */ Rarity[] $VALUES;

    public static Rarity[] values() {
        return (Rarity[])$VALUES.clone();
    }

    public static Rarity valueOf(String $$0) {
        return Enum.valueOf(Rarity.class, $$0);
    }

    private Rarity(int $$0, String $$1, ChatFormatting $$2) {
        this.id = $$0;
        this.name = $$1;
        this.color = $$2;
    }

    public ChatFormatting color() {
        return this.color;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ Rarity[] b() {
        return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
    }

    static {
        $VALUES = Rarity.b();
        CODEC = StringRepresentable.fromValues(Rarity::values);
        BY_ID = ByIdMap.a($$0 -> $$0.id, Rarity.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }
}

