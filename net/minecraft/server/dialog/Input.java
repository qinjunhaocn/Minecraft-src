/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.dialog.action.ParsedTemplate;
import net.minecraft.server.dialog.input.InputControl;

public record Input(String key, InputControl control) {
    public static final Codec<Input> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ParsedTemplate.VARIABLE_CODEC.fieldOf("key").forGetter(Input::key), (App)InputControl.MAP_CODEC.forGetter(Input::control)).apply((Applicative)$$0, Input::new));
}

