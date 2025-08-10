/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;

public class InputControlTypes {
    public static MapCodec<? extends InputControl> bootstrap(Registry<MapCodec<? extends InputControl>> $$0) {
        Registry.register($$0, ResourceLocation.withDefaultNamespace("boolean"), BooleanInput.MAP_CODEC);
        Registry.register($$0, ResourceLocation.withDefaultNamespace("number_range"), NumberRangeInput.MAP_CODEC);
        Registry.register($$0, ResourceLocation.withDefaultNamespace("single_option"), SingleOptionInput.MAP_CODEC);
        return Registry.register($$0, ResourceLocation.withDefaultNamespace("text"), TextInput.MAP_CODEC);
    }
}

