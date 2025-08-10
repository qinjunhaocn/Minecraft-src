/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CommandTemplate;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.action.StaticAction;

public class ActionTypes {
    public static MapCodec<? extends Action> bootstrap(Registry<MapCodec<? extends Action>> $$0) {
        StaticAction.WRAPPED_CODECS.forEach(($$1, $$2) -> Registry.register($$0, ResourceLocation.withDefaultNamespace($$1.getSerializedName()), $$2));
        Registry.register($$0, ResourceLocation.withDefaultNamespace("dynamic/run_command"), CommandTemplate.MAP_CODEC);
        return Registry.register($$0, ResourceLocation.withDefaultNamespace("dynamic/custom"), CustomAll.MAP_CODEC);
    }
}

