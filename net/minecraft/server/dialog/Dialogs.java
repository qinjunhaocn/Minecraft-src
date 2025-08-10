/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.dialog;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.ServerLinksDialog;
import net.minecraft.tags.DialogTags;

public class Dialogs {
    public static final ResourceKey<Dialog> SERVER_LINKS = Dialogs.create("server_links");
    public static final ResourceKey<Dialog> CUSTOM_OPTIONS = Dialogs.create("custom_options");
    public static final ResourceKey<Dialog> QUICK_ACTIONS = Dialogs.create("quick_actions");
    public static final int BIG_BUTTON_WIDTH = 310;
    private static final ActionButton DEFAULT_BACK_BUTTON = new ActionButton(new CommonButtonData(CommonComponents.GUI_BACK, 200), Optional.empty());

    private static ResourceKey<Dialog> create(String $$0) {
        return ResourceKey.create(Registries.DIALOG, ResourceLocation.withDefaultNamespace($$0));
    }

    public static void bootstrap(BootstrapContext<Dialog> $$0) {
        HolderGetter<Dialog> $$1 = $$0.lookup(Registries.DIALOG);
        $$0.register(SERVER_LINKS, new ServerLinksDialog(new CommonDialogData(Component.translatable("menu.server_links.title"), Optional.of(Component.translatable("menu.server_links")), true, true, DialogAction.CLOSE, List.of(), List.of()), Optional.of(DEFAULT_BACK_BUTTON), 1, 310));
        $$0.register(CUSTOM_OPTIONS, new DialogListDialog(new CommonDialogData(Component.translatable("menu.custom_options.title"), Optional.of(Component.translatable("menu.custom_options")), true, true, DialogAction.CLOSE, List.of(), List.of()), $$1.getOrThrow(DialogTags.PAUSE_SCREEN_ADDITIONS), Optional.of(DEFAULT_BACK_BUTTON), 1, 310));
        $$0.register(QUICK_ACTIONS, new DialogListDialog(new CommonDialogData(Component.translatable("menu.quick_actions.title"), Optional.of(Component.translatable("menu.quick_actions")), true, true, DialogAction.CLOSE, List.of(), List.of()), $$1.getOrThrow(DialogTags.QUICK_ACTIONS), Optional.of(DEFAULT_BACK_BUTTON), 1, 310));
    }
}

