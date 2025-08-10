/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

public class LanguageManager
implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("US", "English", false);
    private Map<String, LanguageInfo> languages = ImmutableMap.of("en_us", DEFAULT_LANGUAGE);
    private String currentCode;
    private final Consumer<ClientLanguage> reloadCallback;

    public LanguageManager(String $$0, Consumer<ClientLanguage> $$1) {
        this.currentCode = $$0;
        this.reloadCallback = $$1;
    }

    private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> $$0) {
        HashMap $$12 = Maps.newHashMap();
        $$0.forEach($$1 -> {
            try {
                LanguageMetadataSection $$2 = $$1.getMetadataSection(LanguageMetadataSection.TYPE);
                if ($$2 != null) {
                    $$2.languages().forEach($$12::putIfAbsent);
                }
            } catch (IOException | RuntimeException $$3) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)$$1.packId(), (Object)$$3);
            }
        });
        return ImmutableMap.copyOf($$12);
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        LanguageInfo $$3;
        this.languages = LanguageManager.extractLanguages($$0.listPacks());
        ArrayList<String> $$1 = new ArrayList<String>(2);
        boolean $$2 = DEFAULT_LANGUAGE.bidirectional();
        $$1.add("en_us");
        if (!this.currentCode.equals("en_us") && ($$3 = this.languages.get(this.currentCode)) != null) {
            $$1.add(this.currentCode);
            $$2 = $$3.bidirectional();
        }
        ClientLanguage $$4 = ClientLanguage.loadFrom($$0, $$1, $$2);
        I18n.setLanguage($$4);
        Language.inject($$4);
        this.reloadCallback.accept($$4);
    }

    public void setSelected(String $$0) {
        this.currentCode = $$0;
    }

    public String getSelected() {
        return this.currentCode;
    }

    public SortedMap<String, LanguageInfo> getLanguages() {
        return new TreeMap<String, LanguageInfo>(this.languages);
    }

    @Nullable
    public LanguageInfo getLanguage(String $$0) {
        return this.languages.get($$0);
    }
}

