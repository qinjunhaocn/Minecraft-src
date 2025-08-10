/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface OptionEnum {
    public int getId();

    public String getKey();

    default public Component getCaption() {
        return Component.translatable(this.getKey());
    }
}

