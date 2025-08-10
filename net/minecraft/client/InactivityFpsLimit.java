/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public final class InactivityFpsLimit
extends Enum<InactivityFpsLimit>
implements OptionEnum,
StringRepresentable {
    public static final /* enum */ InactivityFpsLimit MINIMIZED = new InactivityFpsLimit(0, "minimized", "options.inactivityFpsLimit.minimized");
    public static final /* enum */ InactivityFpsLimit AFK = new InactivityFpsLimit(1, "afk", "options.inactivityFpsLimit.afk");
    public static final Codec<InactivityFpsLimit> CODEC;
    private final int id;
    private final String serializedName;
    private final String key;
    private static final /* synthetic */ InactivityFpsLimit[] $VALUES;

    public static InactivityFpsLimit[] values() {
        return (InactivityFpsLimit[])$VALUES.clone();
    }

    public static InactivityFpsLimit valueOf(String $$0) {
        return Enum.valueOf(InactivityFpsLimit.class, $$0);
    }

    private InactivityFpsLimit(int $$0, String $$1, String $$2) {
        this.id = $$0;
        this.serializedName = $$1;
        this.key = $$2;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    private static /* synthetic */ InactivityFpsLimit[] e() {
        return new InactivityFpsLimit[]{MINIMIZED, AFK};
    }

    static {
        $VALUES = InactivityFpsLimit.e();
        CODEC = StringRepresentable.fromEnum(InactivityFpsLimit::values);
    }
}

