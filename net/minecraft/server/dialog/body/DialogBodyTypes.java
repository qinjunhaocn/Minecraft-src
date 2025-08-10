/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.server.dialog.body;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;

public class DialogBodyTypes {
    public static MapCodec<? extends DialogBody> bootstrap(Registry<MapCodec<? extends DialogBody>> $$0) {
        Registry.register($$0, ResourceLocation.withDefaultNamespace("item"), ItemBody.MAP_CODEC);
        return Registry.register($$0, ResourceLocation.withDefaultNamespace("plain_message"), PlainMessage.MAP_CODEC);
    }
}

