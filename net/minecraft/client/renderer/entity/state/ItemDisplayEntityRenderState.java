/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ItemDisplayEntityRenderState
extends DisplayEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();

    @Override
    public boolean hasSubState() {
        return !this.item.isEmpty();
    }
}

