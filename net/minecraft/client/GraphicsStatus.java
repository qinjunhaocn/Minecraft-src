/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public final class GraphicsStatus
extends Enum<GraphicsStatus>
implements OptionEnum {
    public static final /* enum */ GraphicsStatus FAST = new GraphicsStatus(0, "options.graphics.fast");
    public static final /* enum */ GraphicsStatus FANCY = new GraphicsStatus(1, "options.graphics.fancy");
    public static final /* enum */ GraphicsStatus FABULOUS = new GraphicsStatus(2, "options.graphics.fabulous");
    private static final IntFunction<GraphicsStatus> BY_ID;
    private final int id;
    private final String key;
    private static final /* synthetic */ GraphicsStatus[] $VALUES;

    public static GraphicsStatus[] values() {
        return (GraphicsStatus[])$VALUES.clone();
    }

    public static GraphicsStatus valueOf(String $$0) {
        return Enum.valueOf(GraphicsStatus.class, $$0);
    }

    private GraphicsStatus(int $$0, String $$1) {
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

    public String toString() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "fast";
            case 1 -> "fancy";
            case 2 -> "fabulous";
        };
    }

    public static GraphicsStatus byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ GraphicsStatus[] c() {
        return new GraphicsStatus[]{FAST, FANCY, FABULOUS};
    }

    static {
        $VALUES = GraphicsStatus.c();
        BY_ID = ByIdMap.a(GraphicsStatus::getId, GraphicsStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

