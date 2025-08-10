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
package net.minecraft.server.dialog.body;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.body.DialogBody;

public record PlainMessage(Component contents, int width) implements DialogBody
{
    public static final int DEFAULT_WIDTH = 200;
    public static final MapCodec<PlainMessage> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("contents").forGetter(PlainMessage::contents), (App)Dialog.WIDTH_CODEC.optionalFieldOf("width", (Object)200).forGetter(PlainMessage::width)).apply((Applicative)$$0, PlainMessage::new));
    public static final Codec<PlainMessage> CODEC = Codec.withAlternative((Codec)MAP_CODEC.codec(), ComponentSerialization.CODEC, $$0 -> new PlainMessage((Component)$$0, 200));

    public MapCodec<PlainMessage> mapCodec() {
        return MAP_CODEC;
    }
}

