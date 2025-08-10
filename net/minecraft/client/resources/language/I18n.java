/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.language;

import java.util.IllegalFormatException;
import net.minecraft.locale.Language;

public class I18n {
    private static volatile Language language = Language.getInstance();

    private I18n() {
    }

    static void setLanguage(Language $$0) {
        language = $$0;
    }

    public static String a(String $$0, Object ... $$1) {
        String $$2 = language.getOrDefault($$0);
        try {
            return String.format($$2, $$1);
        } catch (IllegalFormatException $$3) {
            return "Format error: " + $$2;
        }
    }

    public static boolean exists(String $$0) {
        return language.has($$0);
    }
}

