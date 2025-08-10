/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public final class ParticleStatus
extends Enum<ParticleStatus>
implements OptionEnum {
    public static final /* enum */ ParticleStatus ALL = new ParticleStatus(0, "options.particles.all");
    public static final /* enum */ ParticleStatus DECREASED = new ParticleStatus(1, "options.particles.decreased");
    public static final /* enum */ ParticleStatus MINIMAL = new ParticleStatus(2, "options.particles.minimal");
    private static final IntFunction<ParticleStatus> BY_ID;
    private final int id;
    private final String key;
    private static final /* synthetic */ ParticleStatus[] $VALUES;

    public static ParticleStatus[] values() {
        return (ParticleStatus[])$VALUES.clone();
    }

    public static ParticleStatus valueOf(String $$0) {
        return Enum.valueOf(ParticleStatus.class, $$0);
    }

    private ParticleStatus(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ParticleStatus byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ ParticleStatus[] c() {
        return new ParticleStatus[]{ALL, DECREASED, MINIMAL};
    }

    static {
        $VALUES = ParticleStatus.c();
        BY_ID = ByIdMap.a(ParticleStatus::getId, ParticleStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

