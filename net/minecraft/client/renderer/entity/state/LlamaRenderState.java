/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.ItemStack;

public class LlamaRenderState
extends LivingEntityRenderState {
    public Llama.Variant variant = Llama.Variant.DEFAULT;
    public boolean hasChest;
    public ItemStack bodyItem = ItemStack.EMPTY;
    public boolean isTraderLlama;
}

