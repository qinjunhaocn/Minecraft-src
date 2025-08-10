/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft;

import org.apache.commons.lang3.StringEscapeUtils;

public class ResourceLocationException
extends RuntimeException {
    public ResourceLocationException(String $$0) {
        super(StringEscapeUtils.escapeJava($$0));
    }

    public ResourceLocationException(String $$0, Throwable $$1) {
        super(StringEscapeUtils.escapeJava($$0), $$1);
    }
}

