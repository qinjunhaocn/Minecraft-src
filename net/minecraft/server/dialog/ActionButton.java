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
import java.util.Optional;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.action.Action;

public record ActionButton(CommonButtonData button, Optional<Action> action) {
    public static final Codec<ActionButton> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)CommonButtonData.MAP_CODEC.forGetter(ActionButton::button), (App)Action.CODEC.optionalFieldOf("action").forGetter(ActionButton::action)).apply((Applicative)$$0, ActionButton::new));
}

