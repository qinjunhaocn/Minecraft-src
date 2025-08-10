/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public final class MapPostProcessing
extends Enum<MapPostProcessing> {
    public static final /* enum */ MapPostProcessing LOCK = new MapPostProcessing(0);
    public static final /* enum */ MapPostProcessing SCALE = new MapPostProcessing(1);
    public static final IntFunction<MapPostProcessing> ID_MAP;
    public static final StreamCodec<ByteBuf, MapPostProcessing> STREAM_CODEC;
    private final int id;
    private static final /* synthetic */ MapPostProcessing[] $VALUES;

    public static MapPostProcessing[] values() {
        return (MapPostProcessing[])$VALUES.clone();
    }

    public static MapPostProcessing valueOf(String $$0) {
        return Enum.valueOf(MapPostProcessing.class, $$0);
    }

    private MapPostProcessing(int $$0) {
        this.id = $$0;
    }

    public int id() {
        return this.id;
    }

    private static /* synthetic */ MapPostProcessing[] b() {
        return new MapPostProcessing[]{LOCK, SCALE};
    }

    static {
        $VALUES = MapPostProcessing.b();
        ID_MAP = ByIdMap.a(MapPostProcessing::id, MapPostProcessing.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(ID_MAP, MapPostProcessing::id);
    }
}

