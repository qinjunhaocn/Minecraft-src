/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.gui.screens.dialog.body;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.dialog.body.DialogBodyHandler;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import org.slf4j.Logger;

public class DialogBodyHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MapCodec<? extends DialogBody>, DialogBodyHandler<?>> HANDLERS = new HashMap();

    private static <B extends DialogBody> void register(MapCodec<B> $$0, DialogBodyHandler<? super B> $$1) {
        HANDLERS.put($$0, $$1);
    }

    @Nullable
    private static <B extends DialogBody> DialogBodyHandler<B> getHandler(B $$0) {
        return HANDLERS.get($$0.mapCodec());
    }

    @Nullable
    public static <B extends DialogBody> LayoutElement createBodyElement(DialogScreen<?> $$0, B $$1) {
        DialogBodyHandler<B> $$2 = DialogBodyHandlers.getHandler($$1);
        if ($$2 == null) {
            LOGGER.warn("Unrecognized dialog body {}", (Object)$$1);
            return null;
        }
        return $$2.createControls($$0, $$1);
    }

    public static void bootstrap() {
        DialogBodyHandlers.register(PlainMessage.MAP_CODEC, new PlainMessageHandler());
        DialogBodyHandlers.register(ItemBody.MAP_CODEC, new ItemHandler());
    }

    static void runActionOnParent(DialogScreen<?> $$0, @Nullable Style $$1) {
        ClickEvent $$2;
        if ($$1 != null && ($$2 = $$1.getClickEvent()) != null) {
            $$0.runAction(Optional.of($$2));
        }
    }

    static class PlainMessageHandler
    implements DialogBodyHandler<PlainMessage> {
        PlainMessageHandler() {
        }

        @Override
        public LayoutElement createControls(DialogScreen<?> $$0, PlainMessage $$12) {
            return new FocusableTextWidget($$12.width(), $$12.contents(), $$0.getFont(), false, false, 4).configureStyleHandling(true, $$1 -> DialogBodyHandlers.runActionOnParent($$0, $$1)).setCentered(true);
        }
    }

    static class ItemHandler
    implements DialogBodyHandler<ItemBody> {
        ItemHandler() {
        }

        @Override
        public LayoutElement createControls(DialogScreen<?> $$0, ItemBody $$12) {
            if ($$12.description().isPresent()) {
                PlainMessage $$2 = $$12.description().get();
                LinearLayout $$3 = LinearLayout.horizontal().spacing(2);
                $$3.defaultCellSetting().alignVerticallyMiddle();
                ItemDisplayWidget $$4 = new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, $$12.width(), $$12.height(), CommonComponents.EMPTY, $$12.item(), $$12.showDecorations(), $$12.showTooltip());
                $$3.addChild($$4);
                $$3.addChild(new FocusableTextWidget($$2.width(), $$2.contents(), $$0.getFont(), false, false, 4).configureStyleHandling(true, $$1 -> DialogBodyHandlers.runActionOnParent($$0, $$1)));
                return $$3;
            }
            return new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, $$12.width(), $$12.height(), $$12.item().getHoverName(), $$12.item(), $$12.showDecorations(), $$12.showTooltip());
        }
    }
}

