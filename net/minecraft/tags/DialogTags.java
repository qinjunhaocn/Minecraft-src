/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.tags.TagKey;

public class DialogTags {
    public static final TagKey<Dialog> PAUSE_SCREEN_ADDITIONS = DialogTags.create("pause_screen_additions");
    public static final TagKey<Dialog> QUICK_ACTIONS = DialogTags.create("quick_actions");

    private DialogTags() {
    }

    private static TagKey<Dialog> create(String $$0) {
        return TagKey.create(Registries.DIALOG, ResourceLocation.withDefaultNamespace($$0));
    }
}

