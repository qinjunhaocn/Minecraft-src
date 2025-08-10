/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.gui.screens.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogListDialogScreen;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.dialog.MultiButtonDialogScreen;
import net.minecraft.client.gui.screens.dialog.ServerLinksDialogScreen;
import net.minecraft.client.gui.screens.dialog.SimpleDialogScreen;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;

public class DialogScreens {
    private static final Map<MapCodec<? extends Dialog>, Factory<?>> FACTORIES = new HashMap();

    private static <T extends Dialog> void register(MapCodec<T> $$0, Factory<? super T> $$1) {
        FACTORIES.put($$0, $$1);
    }

    @Nullable
    public static <T extends Dialog> DialogScreen<T> createFromData(T $$0, @Nullable Screen $$1, DialogConnectionAccess $$2) {
        Factory<?> $$3 = FACTORIES.get($$0.codec());
        if ($$3 != null) {
            return $$3.create($$1, $$0, $$2);
        }
        return null;
    }

    public static void bootstrap() {
        DialogScreens.register(ConfirmationDialog.MAP_CODEC, SimpleDialogScreen::new);
        DialogScreens.register(NoticeDialog.MAP_CODEC, SimpleDialogScreen::new);
        DialogScreens.register(DialogListDialog.MAP_CODEC, DialogListDialogScreen::new);
        DialogScreens.register(MultiActionDialog.MAP_CODEC, MultiButtonDialogScreen::new);
        DialogScreens.register(ServerLinksDialog.MAP_CODEC, ServerLinksDialogScreen::new);
    }

    @FunctionalInterface
    public static interface Factory<T extends Dialog> {
        public DialogScreen<T> create(@Nullable Screen var1, T var2, DialogConnectionAccess var3);
    }
}

