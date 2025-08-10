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
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.ButtonListDialog;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.util.ExtraCodecs;

public record ServerLinksDialog(CommonDialogData common, Optional<ActionButton> exitAction, int columns, int buttonWidth) implements ButtonListDialog
{
    public static final MapCodec<ServerLinksDialog> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CommonDialogData.MAP_CODEC.forGetter(ServerLinksDialog::common), (App)ActionButton.CODEC.optionalFieldOf("exit_action").forGetter(ServerLinksDialog::exitAction), (App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("columns", (Object)2).forGetter(ServerLinksDialog::columns), (App)WIDTH_CODEC.optionalFieldOf("button_width", (Object)150).forGetter(ServerLinksDialog::buttonWidth)).apply((Applicative)$$0, ServerLinksDialog::new));

    public MapCodec<ServerLinksDialog> codec() {
        return MAP_CODEC;
    }
}

