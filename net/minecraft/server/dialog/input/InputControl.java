/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;

public interface InputControl {
    public static final MapCodec<InputControl> MAP_CODEC = BuiltInRegistries.INPUT_CONTROL_TYPE.byNameCodec().dispatchMap(InputControl::mapCodec, $$0 -> $$0);

    public MapCodec<? extends InputControl> mapCodec();
}

