/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.inventory;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public final class ClickType
extends Enum<ClickType> {
    public static final /* enum */ ClickType PICKUP = new ClickType(0);
    public static final /* enum */ ClickType QUICK_MOVE = new ClickType(1);
    public static final /* enum */ ClickType SWAP = new ClickType(2);
    public static final /* enum */ ClickType CLONE = new ClickType(3);
    public static final /* enum */ ClickType THROW = new ClickType(4);
    public static final /* enum */ ClickType QUICK_CRAFT = new ClickType(5);
    public static final /* enum */ ClickType PICKUP_ALL = new ClickType(6);
    private static final IntFunction<ClickType> BY_ID;
    public static final StreamCodec<ByteBuf, ClickType> STREAM_CODEC;
    private final int id;
    private static final /* synthetic */ ClickType[] $VALUES;

    public static ClickType[] values() {
        return (ClickType[])$VALUES.clone();
    }

    public static ClickType valueOf(String $$0) {
        return Enum.valueOf(ClickType.class, $$0);
    }

    private ClickType(int $$0) {
        this.id = $$0;
    }

    public int id() {
        return this.id;
    }

    private static /* synthetic */ ClickType[] b() {
        return new ClickType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
    }

    static {
        $VALUES = ClickType.b();
        BY_ID = ByIdMap.a(ClickType::id, ClickType.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ClickType::id);
    }
}

