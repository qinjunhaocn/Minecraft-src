/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class CommonButtons {
    public static SpriteIconButton language(int $$0, Button.OnPress $$1, boolean $$2) {
        return SpriteIconButton.builder(Component.translatable("options.language"), $$1, $$2).width($$0).sprite(ResourceLocation.withDefaultNamespace("icon/language"), 15, 15).build();
    }

    public static SpriteIconButton accessibility(int $$0, Button.OnPress $$1, boolean $$2) {
        MutableComponent $$3 = $$2 ? Component.translatable("options.accessibility") : Component.translatable("accessibility.onboarding.accessibility.button");
        return SpriteIconButton.builder($$3, $$1, $$2).width($$0).sprite(ResourceLocation.withDefaultNamespace("icon/accessibility"), 15, 15).build();
    }
}

