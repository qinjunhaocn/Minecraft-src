/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class ComponentRenderUtils {
    private static final FormattedCharSequence INDENT = FormattedCharSequence.codepoint(32, Style.EMPTY);

    private static String stripColor(String $$0) {
        return Minecraft.getInstance().options.chatColors().get() != false ? $$0 : ChatFormatting.stripFormatting($$0);
    }

    public static List<FormattedCharSequence> wrapComponents(FormattedText $$0, int $$12, Font $$22) {
        ComponentCollector $$3 = new ComponentCollector();
        $$0.visit(($$1, $$2) -> {
            $$3.append(FormattedText.of(ComponentRenderUtils.stripColor($$2), $$1));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList<FormattedCharSequence> $$4 = Lists.newArrayList();
        $$22.getSplitter().splitLines($$3.getResultOrEmpty(), $$12, Style.EMPTY, ($$1, $$2) -> {
            FormattedCharSequence $$3 = Language.getInstance().getVisualOrder((FormattedText)$$1);
            $$4.add($$2 != false ? FormattedCharSequence.composite(INDENT, $$3) : $$3);
        });
        if ($$4.isEmpty()) {
            return Lists.newArrayList(FormattedCharSequence.EMPTY);
        }
        return $$4;
    }
}

