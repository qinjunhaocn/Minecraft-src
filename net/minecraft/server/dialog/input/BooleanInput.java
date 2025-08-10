/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog.input;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.input.InputControl;

public record BooleanInput(Component label, boolean initial, String onTrue, String onFalse) implements InputControl
{
    public static final MapCodec<BooleanInput> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("label").forGetter(BooleanInput::label), (App)Codec.BOOL.optionalFieldOf("initial", (Object)false).forGetter(BooleanInput::initial), (App)Codec.STRING.optionalFieldOf("on_true", (Object)"true").forGetter(BooleanInput::onTrue), (App)Codec.STRING.optionalFieldOf("on_false", (Object)"false").forGetter(BooleanInput::onFalse)).apply((Applicative)$$0, BooleanInput::new));

    public MapCodec<BooleanInput> mapCodec() {
        return MAP_CODEC;
    }
}

