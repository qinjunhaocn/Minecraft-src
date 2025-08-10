/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public final class PrioritizeChunkUpdates
extends Enum<PrioritizeChunkUpdates>
implements OptionEnum {
    public static final /* enum */ PrioritizeChunkUpdates NONE = new PrioritizeChunkUpdates(0, "options.prioritizeChunkUpdates.none");
    public static final /* enum */ PrioritizeChunkUpdates PLAYER_AFFECTED = new PrioritizeChunkUpdates(1, "options.prioritizeChunkUpdates.byPlayer");
    public static final /* enum */ PrioritizeChunkUpdates NEARBY = new PrioritizeChunkUpdates(2, "options.prioritizeChunkUpdates.nearby");
    private static final IntFunction<PrioritizeChunkUpdates> BY_ID;
    private final int id;
    private final String key;
    private static final /* synthetic */ PrioritizeChunkUpdates[] $VALUES;

    public static PrioritizeChunkUpdates[] values() {
        return (PrioritizeChunkUpdates[])$VALUES.clone();
    }

    public static PrioritizeChunkUpdates valueOf(String $$0) {
        return Enum.valueOf(PrioritizeChunkUpdates.class, $$0);
    }

    private PrioritizeChunkUpdates(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public static PrioritizeChunkUpdates byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ PrioritizeChunkUpdates[] c() {
        return new PrioritizeChunkUpdates[]{NONE, PLAYER_AFFECTED, NEARBY};
    }

    static {
        $VALUES = PrioritizeChunkUpdates.c();
        BY_ID = ByIdMap.a(PrioritizeChunkUpdates::getId, PrioritizeChunkUpdates.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

