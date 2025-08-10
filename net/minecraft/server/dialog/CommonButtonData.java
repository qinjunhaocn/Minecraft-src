/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;

public record CommonButtonData(Component label, Optional<Component> tooltip, int width) {
    public static final int DEFAULT_WIDTH = 150;
    public static final MapCodec<CommonButtonData> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("label").forGetter(CommonButtonData::label), (App)ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(CommonButtonData::tooltip), (App)Dialog.WIDTH_CODEC.optionalFieldOf("width", (Object)150).forGetter(CommonButtonData::width)).apply((Applicative)$$0, CommonButtonData::new));

    public CommonButtonData(Component $$0, int $$1) {
        this($$0, Optional.empty(), $$1);
    }
}

