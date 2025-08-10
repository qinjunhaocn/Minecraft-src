/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.body.DialogBody;

public record CommonDialogData(Component title, Optional<Component> externalTitle, boolean canCloseWithEscape, boolean pause, DialogAction afterAction, List<DialogBody> body, List<Input> inputs) {
    public static final MapCodec<CommonDialogData> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("title").forGetter(CommonDialogData::title), (App)ComponentSerialization.CODEC.optionalFieldOf("external_title").forGetter(CommonDialogData::externalTitle), (App)Codec.BOOL.optionalFieldOf("can_close_with_escape", (Object)true).forGetter(CommonDialogData::canCloseWithEscape), (App)Codec.BOOL.optionalFieldOf("pause", (Object)true).forGetter(CommonDialogData::pause), (App)DialogAction.CODEC.optionalFieldOf("after_action", DialogAction.CLOSE).forGetter(CommonDialogData::afterAction), (App)DialogBody.COMPACT_LIST_CODEC.optionalFieldOf("body", (Object)List.of()).forGetter(CommonDialogData::body), (App)Input.CODEC.listOf().optionalFieldOf("inputs", (Object)List.of()).forGetter(CommonDialogData::inputs)).apply((Applicative)$$0, CommonDialogData::new)).validate($$0 -> {
        if ($$0.pause && !$$0.afterAction.willUnpause()) {
            return DataResult.error(() -> "Dialogs that pause the game must use after_action values that unpause it after user action!");
        }
        return DataResult.success((Object)$$0);
    });

    public Component computeExternalTitle() {
        return this.externalTitle.orElse(this.title);
    }
}

