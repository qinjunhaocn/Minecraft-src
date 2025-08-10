/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class TrackingItemStackRenderState
extends ItemStackRenderState {
    private final List<Object> modelIdentityElements = new ArrayList<Object>();

    @Override
    public void appendModelIdentityElement(Object $$0) {
        this.modelIdentityElements.add($$0);
    }

    public Object getModelIdentity() {
        return this.modelIdentityElements;
    }
}

