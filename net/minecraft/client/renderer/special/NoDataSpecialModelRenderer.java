/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface NoDataSpecialModelRenderer
extends SpecialModelRenderer<Void> {
    @Override
    @Nullable
    default public Void extractArgument(ItemStack $$0) {
        return null;
    }

    @Override
    default public void render(@Nullable Void $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, boolean $$6) {
        this.render($$1, $$2, $$3, $$4, $$5, $$6);
    }

    public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6);

    @Override
    @Nullable
    default public /* synthetic */ Object extractArgument(ItemStack itemStack) {
        return this.extractArgument(itemStack);
    }
}

