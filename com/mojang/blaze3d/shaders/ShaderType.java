/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.DontObfuscate;
import javax.annotation.Nullable;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

@DontObfuscate
public enum ShaderType {
    VERTEX("vertex", ".vsh"),
    FRAGMENT("fragment", ".fsh");

    private static final ShaderType[] TYPES;
    private final String name;
    private final String extension;

    private ShaderType(String $$0, String $$1) {
        this.name = $$0;
        this.extension = $$1;
    }

    @Nullable
    public static ShaderType byLocation(ResourceLocation $$0) {
        for (ShaderType $$1 : TYPES) {
            if (!$$0.getPath().endsWith($$1.extension)) continue;
            return $$1;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public FileToIdConverter idConverter() {
        return new FileToIdConverter("shaders", this.extension);
    }

    static {
        TYPES = ShaderType.values();
    }
}

