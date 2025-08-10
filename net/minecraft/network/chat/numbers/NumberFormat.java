/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.chat.numbers;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormatType;

public interface NumberFormat {
    public MutableComponent format(int var1);

    public NumberFormatType<? extends NumberFormat> type();
}

