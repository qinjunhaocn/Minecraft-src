/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.resources.language;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.DeprecatedTranslationsInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ClientLanguage
extends Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, String> storage;
    private final boolean defaultRightToLeft;

    private ClientLanguage(Map<String, String> $$0, boolean $$1) {
        this.storage = $$0;
        this.defaultRightToLeft = $$1;
    }

    public static ClientLanguage loadFrom(ResourceManager $$0, List<String> $$1, boolean $$2) {
        HashMap<String, String> $$3 = new HashMap<String, String>();
        for (String $$4 : $$1) {
            String $$5 = String.format(Locale.ROOT, "lang/%s.json", $$4);
            for (String $$6 : $$0.getNamespaces()) {
                try {
                    ResourceLocation $$7 = ResourceLocation.fromNamespaceAndPath($$6, $$5);
                    ClientLanguage.appendFrom($$4, $$0.getResourceStack($$7), $$3);
                } catch (Exception $$8) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", $$6, $$5, $$8.toString());
                }
            }
        }
        DeprecatedTranslationsInfo.loadFromDefaultResource().applyToMap($$3);
        return new ClientLanguage(Map.copyOf($$3), $$2);
    }

    private static void appendFrom(String $$0, List<Resource> $$1, Map<String, String> $$2) {
        for (Resource $$3 : $$1) {
            try {
                InputStream $$4 = $$3.open();
                try {
                    Language.loadFromJson($$4, $$2::put);
                } finally {
                    if ($$4 == null) continue;
                    $$4.close();
                }
            } catch (IOException $$5) {
                LOGGER.warn("Failed to load translations for {} from pack {}", $$0, $$3.sourcePackId(), $$5);
            }
        }
    }

    @Override
    public String getOrDefault(String $$0, String $$1) {
        return this.storage.getOrDefault($$0, $$1);
    }

    @Override
    public boolean has(String $$0) {
        return this.storage.containsKey($$0);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return this.defaultRightToLeft;
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText $$0) {
        return FormattedBidiReorder.reorder($$0, this.defaultRightToLeft);
    }
}

