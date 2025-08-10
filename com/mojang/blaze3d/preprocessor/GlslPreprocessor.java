/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.preprocessor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.util.StringUtil;

public abstract class GlslPreprocessor {
    private static final String C_COMMENT = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
    private static final String LINE_COMMENT = "//[^\\v]*";
    private static final Pattern REGEX_MOJ_IMPORT = Pattern.compile("(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))");
    private static final Pattern REGEX_VERSION = Pattern.compile("(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b");
    private static final Pattern REGEX_ENDS_WITH_WHITESPACE = Pattern.compile("(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

    public List<String> process(String $$0) {
        Context $$1 = new Context();
        List<String> $$2 = this.processImports($$0, $$1, "");
        $$2.set(0, this.setVersion($$2.get(0), $$1.glslVersion));
        return $$2;
    }

    private List<String> processImports(String $$0, Context $$1, String $$2) {
        int $$3 = $$1.sourceId;
        int $$4 = 0;
        String $$5 = "";
        ArrayList<String> $$6 = Lists.newArrayList();
        Matcher $$7 = REGEX_MOJ_IMPORT.matcher($$0);
        while ($$7.find()) {
            boolean $$9;
            if (GlslPreprocessor.isDirectiveDisabled($$0, $$7, $$4)) continue;
            String $$8 = $$7.group(2);
            boolean bl = $$9 = $$8 != null;
            if (!$$9) {
                $$8 = $$7.group(3);
            }
            if ($$8 == null) continue;
            String $$10 = $$0.substring($$4, $$7.start(1));
            String $$11 = $$2 + $$8;
            Object $$12 = this.applyImport($$9, $$11);
            if (!Strings.isNullOrEmpty((String)$$12)) {
                if (!StringUtil.endsWithNewLine((String)$$12)) {
                    $$12 = (String)$$12 + System.lineSeparator();
                }
                int $$13 = ++$$1.sourceId;
                List<String> $$14 = this.processImports((String)$$12, $$1, $$9 ? FileUtil.getFullResourcePath($$11) : "");
                $$14.set(0, String.format(Locale.ROOT, "#line %d %d\n%s", 0, $$13, this.processVersions($$14.get(0), $$1)));
                if (!StringUtil.isBlank($$10)) {
                    $$6.add($$10);
                }
                $$6.addAll($$14);
            } else {
                String $$15 = $$9 ? String.format(Locale.ROOT, "/*#moj_import \"%s\"*/", $$8) : String.format(Locale.ROOT, "/*#moj_import <%s>*/", $$8);
                $$6.add($$5 + $$10 + $$15);
            }
            int $$16 = StringUtil.lineCount($$0.substring(0, $$7.end(1)));
            $$5 = String.format(Locale.ROOT, "#line %d %d", $$16, $$3);
            $$4 = $$7.end(1);
        }
        String $$17 = $$0.substring($$4);
        if (!StringUtil.isBlank($$17)) {
            $$6.add($$5 + $$17);
        }
        return $$6;
    }

    private String processVersions(String $$0, Context $$1) {
        Matcher $$2 = REGEX_VERSION.matcher($$0);
        if ($$2.find() && GlslPreprocessor.isDirectiveEnabled($$0, $$2)) {
            $$1.glslVersion = Math.max($$1.glslVersion, Integer.parseInt($$2.group(2)));
            return $$0.substring(0, $$2.start(1)) + "/*" + $$0.substring($$2.start(1), $$2.end(1)) + "*/" + $$0.substring($$2.end(1));
        }
        return $$0;
    }

    private String setVersion(String $$0, int $$1) {
        Matcher $$2 = REGEX_VERSION.matcher($$0);
        if ($$2.find() && GlslPreprocessor.isDirectiveEnabled($$0, $$2)) {
            return $$0.substring(0, $$2.start(2)) + Math.max($$1, Integer.parseInt($$2.group(2))) + $$0.substring($$2.end(2));
        }
        return $$0;
    }

    private static boolean isDirectiveEnabled(String $$0, Matcher $$1) {
        return !GlslPreprocessor.isDirectiveDisabled($$0, $$1, 0);
    }

    private static boolean isDirectiveDisabled(String $$0, Matcher $$1, int $$2) {
        int $$3 = $$1.start() - $$2;
        if ($$3 == 0) {
            return false;
        }
        Matcher $$4 = REGEX_ENDS_WITH_WHITESPACE.matcher($$0.substring($$2, $$1.start()));
        if (!$$4.find()) {
            return true;
        }
        int $$5 = $$4.end(1);
        return $$5 == $$1.start();
    }

    @Nullable
    public abstract String applyImport(boolean var1, String var2);

    public static String injectDefines(String $$0, ShaderDefines $$1) {
        if ($$1.isEmpty()) {
            return $$0;
        }
        int $$2 = $$0.indexOf(10);
        int $$3 = $$2 + 1;
        return $$0.substring(0, $$3) + $$1.asSourceDirectives() + "#line 1 0\n" + $$0.substring($$3);
    }

    static final class Context {
        int glslVersion;
        int sourceId;

        Context() {
        }
    }
}

