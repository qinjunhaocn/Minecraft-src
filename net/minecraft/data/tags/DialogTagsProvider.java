/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.tags.DialogTags;

public class DialogTagsProvider
extends KeyTagProvider<Dialog> {
    public DialogTagsProvider(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$0, Registries.DIALOG, $$1);
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        this.tag(DialogTags.PAUSE_SCREEN_ADDITIONS);
        this.tag(DialogTags.QUICK_ACTIONS);
    }
}

