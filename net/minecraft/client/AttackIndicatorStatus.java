/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public final class AttackIndicatorStatus
extends Enum<AttackIndicatorStatus>
implements OptionEnum {
    public static final /* enum */ AttackIndicatorStatus OFF = new AttackIndicatorStatus(0, "options.off");
    public static final /* enum */ AttackIndicatorStatus CROSSHAIR = new AttackIndicatorStatus(1, "options.attack.crosshair");
    public static final /* enum */ AttackIndicatorStatus HOTBAR = new AttackIndicatorStatus(2, "options.attack.hotbar");
    private static final IntFunction<AttackIndicatorStatus> BY_ID;
    private final int id;
    private final String key;
    private static final /* synthetic */ AttackIndicatorStatus[] $VALUES;

    public static AttackIndicatorStatus[] values() {
        return (AttackIndicatorStatus[])$VALUES.clone();
    }

    public static AttackIndicatorStatus valueOf(String $$0) {
        return Enum.valueOf(AttackIndicatorStatus.class, $$0);
    }

    private AttackIndicatorStatus(int $$0, String $$1) {
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

    public static AttackIndicatorStatus byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ AttackIndicatorStatus[] c() {
        return new AttackIndicatorStatus[]{OFF, CROSSHAIR, HOTBAR};
    }

    static {
        $VALUES = AttackIndicatorStatus.c();
        BY_ID = ByIdMap.a(AttackIndicatorStatus::getId, AttackIndicatorStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

