/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.resources.ResourceLocation;

public record WidgetSprites(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledFocused, ResourceLocation disabledFocused) {
    public WidgetSprites(ResourceLocation $$0, ResourceLocation $$1) {
        this($$0, $$0, $$1, $$1);
    }

    public WidgetSprites(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2) {
        this($$0, $$1, $$2, $$1);
    }

    public ResourceLocation get(boolean $$0, boolean $$1) {
        if ($$0) {
            return $$1 ? this.enabledFocused : this.enabled;
        }
        return $$1 ? this.disabledFocused : this.disabled;
    }
}

