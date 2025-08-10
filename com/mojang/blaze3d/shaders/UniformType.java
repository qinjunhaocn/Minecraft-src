/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.shaders;

public final class UniformType
extends Enum<UniformType> {
    public static final /* enum */ UniformType UNIFORM_BUFFER = new UniformType("ubo");
    public static final /* enum */ UniformType TEXEL_BUFFER = new UniformType("utb");
    final String name;
    private static final /* synthetic */ UniformType[] $VALUES;

    public static UniformType[] values() {
        return (UniformType[])$VALUES.clone();
    }

    public static UniformType valueOf(String $$0) {
        return Enum.valueOf(UniformType.class, $$0);
    }

    private UniformType(String $$0) {
        this.name = $$0;
    }

    private static /* synthetic */ UniformType[] a() {
        return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
    }

    static {
        $VALUES = UniformType.a();
    }
}

