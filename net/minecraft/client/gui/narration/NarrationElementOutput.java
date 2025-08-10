/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.network.chat.Component;

public interface NarrationElementOutput {
    default public void add(NarratedElementType $$0, Component $$1) {
        this.add($$0, NarrationThunk.from($$1.getString()));
    }

    default public void add(NarratedElementType $$0, String $$1) {
        this.add($$0, NarrationThunk.from($$1));
    }

    default public void a(NarratedElementType $$0, Component ... $$1) {
        this.add($$0, NarrationThunk.from(ImmutableList.copyOf($$1)));
    }

    public void add(NarratedElementType var1, NarrationThunk<?> var2);

    public NarrationElementOutput nest();
}

