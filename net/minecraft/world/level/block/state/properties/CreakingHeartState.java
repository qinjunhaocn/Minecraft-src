/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public final class CreakingHeartState
extends Enum<CreakingHeartState>
implements StringRepresentable {
    public static final /* enum */ CreakingHeartState UPROOTED = new CreakingHeartState("uprooted");
    public static final /* enum */ CreakingHeartState DORMANT = new CreakingHeartState("dormant");
    public static final /* enum */ CreakingHeartState AWAKE = new CreakingHeartState("awake");
    private final String name;
    private static final /* synthetic */ CreakingHeartState[] $VALUES;

    public static CreakingHeartState[] values() {
        return (CreakingHeartState[])$VALUES.clone();
    }

    public static CreakingHeartState valueOf(String $$0) {
        return Enum.valueOf(CreakingHeartState.class, $$0);
    }

    private CreakingHeartState(String $$0) {
        this.name = $$0;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ CreakingHeartState[] a() {
        return new CreakingHeartState[]{UPROOTED, DORMANT, AWAKE};
    }

    static {
        $VALUES = CreakingHeartState.a();
    }
}

