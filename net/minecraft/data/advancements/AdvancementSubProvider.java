/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementSubProvider {
    public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2);

    public static AdvancementHolder createPlaceholder(String $$0) {
        return Advancement.Builder.advancement().build(ResourceLocation.parse($$0));
    }
}

