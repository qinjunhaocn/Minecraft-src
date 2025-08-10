/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ChatOptionsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.chat.title");

    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.chatVisibility(), $$0.chatColors(), $$0.chatLinks(), $$0.chatLinksPrompt(), $$0.chatOpacity(), $$0.textBackgroundOpacity(), $$0.chatScale(), $$0.chatLineSpacing(), $$0.chatDelay(), $$0.chatWidth(), $$0.chatHeightFocused(), $$0.chatHeightUnfocused(), $$0.narrator(), $$0.autoSuggestions(), $$0.hideMatchedNames(), $$0.reducedDebugInfo(), $$0.onlyShowSecureChat()};
    }

    public ChatOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addOptions() {
        this.list.a(ChatOptionsScreen.a(this.options));
    }
}

